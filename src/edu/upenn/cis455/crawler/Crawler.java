package edu.upenn.cis455.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.upenn.cis455.storage.*;

/**
 * Web crawler class
 * @author Yibang Chen
 *
 */
public class Crawler implements Callable<Object> {
	
	private long docSize;
	private String urlString;
	private URL urlObj;
	private XmlDocumentDA xmlDA;
	private HtmlDocumentDA htmlDA;
	private RobotsDA robotsDA;
	private List<String> parsedLinks;
	private long parsedSize=0;
	private final String userAgent = "cis455crawler";
	
	public Crawler(String URL, XmlDocumentDA accessor , HtmlDocumentDA htmlAccessor,long size,RobotsDA robotAccessor)
			throws MalformedURLException{
		
		this.urlString = URL;
		urlObj = new URL(this.urlString);
		this.xmlDA = accessor;
		this.htmlDA = htmlAccessor;
		this.robotsDA=robotAccessor;
		this.docSize = size;
	}
		
	/**
	 * Process the link passed to the crawler
	 * @return
	 * @throws InterruptedException 
	 */
	public Map<String,Object> processLink() throws InterruptedException{
		System.out.println(this.urlString + " : Begin processing...");

		Map<String,Object> linkInfo = new HashMap<>();
		RobotObject robots = getRobot(urlString);
		WebDocument entity = getWebDocument(urlString);
		
		long lastModified=0;
		if(entity!=null) lastModified = entity.getLastModified();
		
		if(isRequestValid(robots)){
			if(robots!=null){
				Integer delay = robots.getCrawlDelay("*");
				Integer d2 = robots.getCrawlDelay(userAgent);
				if (d2 != null) {
					if (d2 > 0) delay = d2;
				}
				
				if (delay != null) {
					if (delay > 0) {
						System.out.println(this.urlString+
								": Crawl Delay. Sleep for " + delay + " seconds");
						Thread.sleep(delay*1000);
					}
				}
			}
			
			System.out.println(this.urlString+" :Fetching headers...");
			Map<String, Object> headers = HelperFunctions.getHeaders(this.urlObj, lastModified);
			Map<String,List<String>> fetchedHeaders = (Map<String,List<String>>)headers.get("Headers");
			if(headers.get("Redirect")!=null){
				this.urlString = headers.get("Redirect").toString();
				try{
					this.urlObj = new URL(urlString);
				}catch(Exception e){					
				}
				return processLink();
			}else{
				System.out.println(this.urlString+" :Verifying headers...");
				if(isReqNeeded(fetchedHeaders)){
					System.out.println(this.urlString+" :Fetching content...");
					HashMap<String,Object> pageInfo = HelperFunctions.fetchPageAndHeaders(this.urlObj);
					System.out.println(this.urlString+":Parsing urls from content...");
					parseNewPage(pageInfo, entity);				
				}
			}
		}
		linkInfo.put("Links", this.parsedLinks);
		linkInfo.put("Size", this.parsedSize);
				
		return linkInfo;
	}
	
	/**
	 * This method parses and stores a new page
	 * @param pageInfo
	 * @param entity
	 * @return
	 */
	private boolean parseNewPage(HashMap<String,Object> pageInfo, WebDocument entity){
		boolean parsed=false;
		Map<String,List<String>> headers;
		Document doc;
		String content;

		if(entity == null){
			headers = (Map<String,List<String>>)pageInfo.get("Headers");
			content = (String)pageInfo.get("Content");
			
			doc = (Document)pageInfo.get("Document");
			if(headers != null & content!=null){
				String digest = hashContent(content);
				this.parsedSize = content.length();
				List<String> contentType = headers.get("Content-Type");
				if(contentType!=null){
					
					String type = contentType.get(0);
					if(HelperFunctions.isHtmlDoc(type)){
						HtmlDocument digestEntity =this.htmlDA.fetchEntityFromDigestKey(digest);
						if(digestEntity==null){
							HtmlDocument htmlDoc = new HtmlDocument();
							htmlDoc.setHtmlPageId(HelperFunctions.getUniqueId());
							htmlDoc.setHtmlPageUrl(this.urlString);
							htmlDoc.setHtmlLastParsed(new Date().getTime());
							htmlDoc.setLastModified(HelperFunctions.convertDateToTime(headers.get("Date").get(0)));
							htmlDoc.setHtmlDomain(this.urlObj.getHost());
							htmlDoc.setHtmlParseBanned(false);
							htmlDoc.setHtmlContent(content);
							htmlDoc.setMessageDigest(digest);
							this.htmlDA.putEntity(htmlDoc);
							this.parsedLinks = parseLinks(doc);
						}else{
							System.out.println(this.urlString+" : document already crawled");
						}
					}
					if(HelperFunctions.isXmlDoc(type)){
						XmlDocument digestEntity = this.xmlDA.fetchEntityFromDigestKey(digest);
						if(digestEntity==null){
							XmlDocument xmlDoc = new XmlDocument();
							xmlDoc.setXmlPageId(HelperFunctions.getUniqueId());
							xmlDoc.setXmlDocUrl(this.urlString);
							xmlDoc.setXmlLastParsed(new Date().getTime());
							xmlDoc.setLastModified(HelperFunctions.convertDateToTime(headers.get("Date").get(0)));
							xmlDoc.setXmlDomain(this.urlObj.getHost());
							xmlDoc.setXmlParseBanned(false);
							xmlDoc.setXmlContent(content);
							xmlDoc.setMessageDigest(digest);
							this.xmlDA.putEntity(xmlDoc);
						}else{
							System.out.println(this.urlString+" : document already crawled");
						}
					}
				}
			}
		}else{
			headers = (Map<String,List<String>>)pageInfo.get("Headers");
			content = (String)pageInfo.get("Content");
			doc = (Document)pageInfo.get("Document");
			if(headers != null & content!=null){
				String digest = hashContent(content);
				this.parsedSize = content.length();
				List<String> contentType = headers.get("Content-Type");

				if(contentType!=null){
					String type = contentType.get(0);
					if(HelperFunctions.isHtmlDoc(type)){
						HtmlDocument htmlDoc = (HtmlDocument)entity;
						htmlDoc.setHtmlLastParsed(new Date().getTime());
						htmlDoc.setLastModified(HelperFunctions.convertDateToTime(headers.get("Date").get(0)));
						htmlDoc.setHtmlDomain(this.urlObj.getHost());
						htmlDoc.setHtmlParseBanned(false);
						htmlDoc.setHtmlContent(content);
						htmlDoc.setMessageDigest(digest);
						this.htmlDA.putEntity(htmlDoc);
						this.parsedLinks = parseLinks(doc);
					}
					if(HelperFunctions.isXmlDoc(type)){
						XmlDocument xmlDoc = (XmlDocument)entity;
						xmlDoc.setXmlLastParsed(new Date().getTime());
						xmlDoc.setLastModified(HelperFunctions.convertDateToTime(headers.get("Date").get(0)));
						xmlDoc.setXmlDomain(this.urlObj.getHost());
						xmlDoc.setXmlParseBanned(false);
						xmlDoc.setXmlContent(content);
						xmlDoc.setMessageDigest(digest);
						this.xmlDA.putEntity(xmlDoc);
					}
				}
			}
		}
		return parsed;
	}
	
	/**
	 * This method checks if a new request is required
	 * @param headers
	 * @return boolean
	 */
	private boolean isReqNeeded(Map<String, List<String>> headers){
		if(headers == null) return false;

		List<String> responseString = headers.get(null);
		boolean isValid = false;
		
		if(responseString!=null){			
			String[] tokenPairs = responseString.get(0).split(" ");
			String status = tokenPairs[1];
			
			if(status.equalsIgnoreCase("200")){
				List<String> contentType = headers.get("Content-Type");
				
				if(contentType!=null){
					String type = contentType.get(0);
					if(!HelperFunctions.isXmlDoc(type) && !HelperFunctions.isHtmlDoc(type)){
						System.out.println(this.urlString+" :Content not xml or html");
					} 
					else {	
						isValid=true;
						List<String> values = headers.get("Content-Length");
						
						if(values==null);
						else if (!values.isEmpty()){
							long value = Long.parseLong(values.get(0));
							if(value>this.docSize){
								System.out.println(this.urlString+
										" : content size over " + docSize + "megabytes");
								isValid=false;
							}
						}
					}
				}
			}
		}

		return isValid;
	}
	
	/**
	 * This method fetches the web document (HTML or XML) from database based on URL string
	 * @return WebDocument
	 */
	private WebDocument getWebDocument(String urlString){
		WebDocument entity;	
		entity = xmlDA.fetchEntityFromSecondaryKey(urlString);
		if(entity==null){
			entity = htmlDA.fetchEntityFromSecondaryKey(urlString);
		}
		return entity;		
	}
	
	/**
	 * This method checks if the request follows robot.txt instructions
	 * @param robot
	 * @return boolean isValid
	 */
	private boolean isRequestValid(RobotObject robot){
		if(robot==null) return true;
		
		String path = this.urlObj.getPath();	
		if(robot.containsUserAgent(userAgent)){
			List<String> disallowed = robot.getDisallowedLinks(userAgent);			
			if(disallowed!=null){
				for(String link:disallowed){
					if(path.startsWith(link)){
						return false;
					}
				}
			}
		}
		else if(robot.containsUserAgent("*")){
			List<String> disallowed = robot.getDisallowedLinks("*");
			if(disallowed!=null){			
				for(String link:disallowed){
					if(path.startsWith(link)){
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Fetch the data as Input stream
	 * @param u
	 * @return
	 */
	private static InputStream getUrlContent(URL u){
		InputStream stream=null;
		boolean redirect=false;
		try{	
			if(!u.getProtocol().equalsIgnoreCase("https")){
			
				HttpURLConnection conn = (HttpURLConnection)u.openConnection();
				conn.setRequestProperty("User-Agent", "cis455crawler");
				conn.connect();
				int status = conn.getResponseCode();
				if (status != HttpURLConnection.HTTP_OK) {
					if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
							|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
				}
				if(redirect){
					String newUrl = conn.getHeaderField("Location");
					URL redirectUrl = new URL(newUrl);
					stream = getUrlContent(redirectUrl);
				}else{
					if(status!= HttpURLConnection.HTTP_OK){
						stream = null;
					}else{
						stream = conn.getInputStream();
					}
				}
			}else{
				if(u.getProtocol().equalsIgnoreCase("https")){					
					HttpsURLConnection conn = (HttpsURLConnection)u.openConnection();
					conn.setRequestProperty("User-Agent", "cis455crawler");
					conn.connect();
					int status = conn.getResponseCode();
					if (status != HttpURLConnection.HTTP_OK) {
						if (status == HttpURLConnection.HTTP_MOVED_TEMP
							|| status == HttpURLConnection.HTTP_MOVED_PERM
								|| status == HttpURLConnection.HTTP_SEE_OTHER)
						redirect = true;
					}
					if(redirect){
						String newUrl = conn.getHeaderField("Location");
						URL redirectUrl = new URL(newUrl);
						stream = getUrlContent(redirectUrl);
					}else{
						if(status!= HttpURLConnection.HTTP_OK){
							stream = null;
						}else{
							stream = conn.getInputStream();
						}
					}
					
				}else{
					URLConnection conn = u.openConnection();
					conn.setRequestProperty("User-Agent", "cis455crawler");
					conn.connect();
					stream = conn.getInputStream();	
				}				
			}
			
		}catch(MalformedURLException mfe){
		}catch(IOException ioe){
		}
		return stream;
	}
	
	/**
	 * This method parses robot file from site
	 * @return parsed robots file
	 */
	private RobotObject getRobot(String urlString){		
		System.out.println("Started parsing Robots.txt from " + urlString);
		// reuse robot when available
		Robots robots = this.robotsDA.fetchEntityFromPrimaryKey(urlObj.getHost());
		if(robots!=null){
			long time = robots.getLastModified();
			long curr = new Date().getTime();
			if((curr-time) <= 864000) // robot fetch delay
				return robots.getRobots();
		}

		RobotObject robot = new RobotObject();
		try{
			URL robotUrl = new URL(urlObj.getProtocol(), urlObj.getHost(), "/robots.txt");			
			InputStream robotStream = getUrlContent(robotUrl);
			BufferedReader reader = new BufferedReader(new InputStreamReader(robotStream));
			String line;
			String currUserName=null;
			
			while((line=reader.readLine())!=null){
				if(line.startsWith("User-agent")){
					String[] tokens = line.split(":");
					if(tokens.length>1){
						String agentName = tokens[1].trim();
						if(agentName.length()>0){
							robot.addUserAgent(agentName);
							currUserName = agentName;
							robot.addCrawlDelay(currUserName, 0);
						}
					}
				}
				if(line.startsWith("Disallow")){
					String[] tokens = line.split(":");
					if(tokens.length>1){
						String disallowName = tokens[1].trim();			
						if(currUserName!=null){
							robot.addDisallowedLink(currUserName, disallowName);
							
						}
					}
					
				}
				if(line.startsWith("Allow")){
					String[] tokens = line.split(":");
					if(tokens.length>1){
					String allowName = tokens[1].trim();			
					if(currUserName!=null){
						robot.addAllowedLink(currUserName, allowName);
						
					}
					}
				}
				
				if(line.startsWith("Crawl-delay")){
					String[] tokens = line.split(":");
					if(tokens.length>1){
						String delay = tokens[1].trim();			
						if(currUserName!=null){
							robot.addCrawlDelay(currUserName, Integer.parseInt(delay));
						}
					}
				}
				
				if(line.startsWith("Sitemap")){
					String[] tokens = line.split(":",2);
					if(tokens.length>1){
					String sitemap = tokens[1].trim();	
					robot.addSitemapLink(sitemap);
					}				
				}
			}
			
			robots = new Robots();
			robots.setDomainName(this.urlObj.getHost());
			robots.setRobots(robot);
			robots.setLastModified(new Date().getTime());
			this.robotsDA.putEntity(robots);
			
			return robot;
		}catch(MalformedURLException mfe){
			return null;
		}catch(IOException ioe){
			return null;
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public Object call() throws Exception {
		return this.processLink();
	}
	
	/**
	 * Parse the document and extract links
	 * @param doc
	 * @return List<String> of links
	 */
	
	private List<String> parseLinks(Document doc){
		List<String> hrefs = new ArrayList<String>();
		List<Element> elements = new ArrayList<Element>();
		Element elem = doc.getDocumentElement();
		
		findAllElementsByTagName(elem, "a", elements);		
		for(Element e:elements){
			NamedNodeMap attMap = e.getAttributes();
			for(int i=0;i<attMap.getLength();i++){
				if(attMap.item(i).getNodeName().equalsIgnoreCase("href")){
					try{
						String link = e.getAttribute("href");
						URL temp = new URL(urlObj, link);
						hrefs.add(temp.toString());
					}catch(MalformedURLException mfe){
					}
				}
			}
		}
		return hrefs;
	} 
	
	/**
	 * Find all elements with a tag
	 * @param item
	 * @param tagName
	 * @param elements
	 */	
	private void findAllElementsByTagName(Element item, String tagName, List<Element> elements){
		if (tagName.equals(item.getTagName()))
			elements.add(item);
	    Element e = getFirst(item);
	    while (e != null) {
	    	findAllElementsByTagName(e, tagName, elements);
	    	e = getNext(e);
		}
	}
	
	/**
	 * Get elements
	 * @param parentNode
	 * @return
	 */
	private Element getFirst(Node parentNode) {
	    Node n = parentNode.getFirstChild();
	    while (n != null && Node.ELEMENT_NODE != n.getNodeType())
	      n = n.getNextSibling();
	    if (n == null) return null;
	    return (Element) n;
	  }
	
	/**
	 * Get next element
	 * @param item
	 * @return Element
	 */
	
	private Element getNext(Element item) {
	    Node current = item.getNextSibling();
	    while (current != null) {
	      if (current.getNodeType() == Node.ELEMENT_NODE) {
	        return (Element) current;
	      }
	      current = current.getNextSibling();
	    }
	    return null;
	  }
	
	/**
	 * Generate the hash of the content
	 * @param content
	 * @return String 
	 */
	private String hashContent(String content){
		String digest = null;
		MessageDigest mdigest;
		try {
			mdigest = MessageDigest.getInstance("SHA-256");
			byte[] digestedBytes = mdigest.digest(content.getBytes());
			digest = new String(digestedBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return digest;
	}
	
	public String getCurrentURL(){
		return this.urlString;
	}

}
