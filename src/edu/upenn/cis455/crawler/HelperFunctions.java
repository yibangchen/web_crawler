package edu.upenn.cis455.crawler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.server.UID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;

import edu.upenn.cis455.storage.*;

public class HelperFunctions {
	
	public static String buildString(InputStream in){
		StringBuilder sb = new StringBuilder();
		try{
			if(in!=null){
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String read="";
				
				while((read=reader.readLine())!=null)
					sb.append(read);
				reader.close();
			}
		}catch(IOException ioe){
			System.out.println(ioe.toString());
		}
		return sb.toString();
	}
	
	public static Document getDocs(String content){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try{ 
		    factory.setNamespaceAware(true);
		    builder = factory.newDocumentBuilder();
		    builder.setErrorHandler(new ErrorHandler() {
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					throw exception;
				}
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					throw exception;
				}
				@Override
				public void error(SAXParseException exception) throws SAXException {
					throw exception;					
				}
			});
		    Document doc = builder.parse(new ByteArrayInputStream(content.getBytes()));
		    
		    return doc;
		}catch(ParserConfigurationException pce){
			return null;
		}catch(IOException ioe){
			return null;
		}catch(SAXException sxe){
			return null;
		}
	}

	/**
	 * Generate html document from string
	 * @param content
	 * @return
	 */
	private static Document getHtmlDocumentFromContent(String content){
		Document doc=null;
		try{
			Tidy tidy = new Tidy();
			tidy.setXHTML(true);
			tidy.setShowWarnings(false);
			tidy.setShowErrors(0);
			doc = tidy.parseDOM(new ByteArrayInputStream(content.getBytes()), null);
		}catch(Exception e){
		}
		return doc;
	}

	/**
	 * Generates the table to be put in the output
	 * @param xPaths
	 * @param results
	 * @return
	 */
	public static String generateTable(String[] xPaths, boolean[] results){
		
		StringBuilder sb = new StringBuilder();
		sb.append("<table><tr><th>XPath</th><th>IsValid</th></tr>");
		for(int i=0;i<xPaths.length;i++){
			sb.append("<tr><td>");
			sb.append(xPaths[i]);
			sb.append("</td><td>");
			sb.append(results[i]);
			sb.append("</td></tr>");
		}
		sb.append("</table>");
		
		return sb.toString();
	}
	
	/**
	 * Reads the gives stream file from disk and returns content as String
	 * @param stream
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String readFile(InputStream stream)
			throws FileNotFoundException{
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		
		try{
			String read="";
			while((read = br.readLine())!=null){
				sb.append(read);
			}
			br.close();
		}catch(IOException ioe){
		}finally{
			if(br != null){
				try{
					br.close();
				}catch(IOException ioe){
				}
			}
		}
		
		return sb.toString();
	}

	/**
	 * Checks if the session is valid
	 * @param session
	 * @return boolean
	 */
	public static boolean isValidSession(HttpSession session){
		boolean isValid = false;
		Date curr = new Date();
		try{
			if(!((session.getLastAccessedTime()-curr.getTime())>session.getMaxInactiveInterval())){
				isValid = true;
			}
		}catch(Exception e){
		}
		
		return isValid;
	}

	/**
	 * Return Headers retrieved from a Head request
	 * @param urlObj
	 * @return
	 */
	public static Map<String,Object> getHeaders(URL urlObj, long modified){
		Map<String, List<String>> headers=null;
		Map<String,Object> retVal = new HashMap<String, Object>();
		boolean redirect=false;
		String redirectURL=null;
		try{
			if(!urlObj.getProtocol().equalsIgnoreCase("https")){
			
				HttpURLConnection conn = (HttpURLConnection)urlObj.openConnection();
				conn.setRequestProperty("User-Agent", "cis455crawler");
				conn.setIfModifiedSince(modified);
				conn.setRequestMethod("HEAD");
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
					redirectURL = newUrl;
					URL redirectUrl = new URL(newUrl);
					retVal = getHeaders(redirectUrl,modified);
				}else{
					headers = conn.getHeaderFields();
				}
			}else{
				if(urlObj.getProtocol().equalsIgnoreCase("https")){
					
					HttpsURLConnection conn = (HttpsURLConnection)urlObj.openConnection();
					HttpsURLConnection.setFollowRedirects(false);
					conn.setRequestProperty("User-Agent", "cis455crawler");
					conn.setIfModifiedSince(modified);
					conn.setRequestMethod("HEAD");
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
						redirectURL = newUrl;
						URL redirectUrl = new URL(newUrl);
						retVal = getHeaders(redirectUrl,modified);
					}else{
						headers = conn.getHeaderFields();
					}				
				}else{
					URLConnection conn = urlObj.openConnection();
					conn.setRequestProperty("User-Agent", "cis455crawler");
					conn.setRequestProperty("If-modified-since", String.valueOf(modified));
					conn.connect();
					headers = conn.getHeaderFields();
				}
				
			}
			retVal.put("Redirect", redirectURL);
			retVal.put("Headers", headers);
		}catch(MalformedURLException mfe){
		}catch(IOException ioe){
		}
		return retVal;	
			
	}

	/**
	 * Fetch the page content and headers and return as a Map
	 * @param u
	 * @return
	 */
	public static HashMap<String,Object> fetchPageAndHeaders(URL u){
		
		HashMap<String,Object> pageContent = new HashMap<String, Object>();
		Map<String, List<String>> headers = null;
		InputStream stream=null;
		Document doc=null;
		String content=null;
		String contentType=null;
		try{
			if(!u.getProtocol().equalsIgnoreCase("https")){
			
				HttpURLConnection conn = (HttpURLConnection)u.openConnection();
				conn.setRequestProperty("User-Agent", "cis455crawler");
				conn.setRequestMethod("GET");
				conn.connect();
				headers = conn.getHeaderFields();
				contentType = conn.getContentType();
				stream = conn.getInputStream();
				
			}else{
				if(u.getProtocol().equalsIgnoreCase("https")){
					
					HttpsURLConnection conn = (HttpsURLConnection)u.openConnection();
					conn.setRequestProperty("User-Agent", "cis455crawler");
					conn.setRequestMethod("GET");
					conn.connect();
					headers = conn.getHeaderFields();
					contentType = conn.getContentType();
					stream = conn.getInputStream();
					
				}else{
					URLConnection conn = u.openConnection();
					conn.setRequestProperty("User-Agent", "cis455crawler");
					conn.connect();
					headers = conn.getHeaderFields();
					contentType = conn.getContentType();
					stream = conn.getInputStream();
				}
				
			}
			content = buildString(stream);
			
			if(HelperFunctions.isHtmlDoc(contentType)){
				doc = getHtmlDocumentFromContent(content);
			}
			
			if(HelperFunctions.isXmlDoc(contentType)){
				doc = getDocs(content);
			}
			
			}catch(MalformedURLException mfe){
			}catch(IOException ioe){
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
			
		pageContent.put("Headers", headers);
		pageContent.put("Content", content);
		pageContent.put("Document", doc);
		
		return pageContent;
	}

	/**
	 * Checks if the content type is an xml type
	 * @param type
	 * @return boolean isXml
	 */
	static boolean isXmlDoc(String contentType){
		boolean isXml = false;
		if(contentType!=null){
			String type = contentType.split(";")[0];
			if(type.trim().equalsIgnoreCase("text/xml") || type.trim().equalsIgnoreCase("application/xml")){
				isXml = true;
			}else{
				if(type.trim().endsWith("+xml")){
					isXml = true;
				}
			}
		}
		return isXml;
	}

	/**
	 * Checks if the content type is html
	 */
	public static boolean isHtmlDoc(String contentType){
		boolean isHtml=false;
		if(contentType!=null){
			String type = contentType.split(";")[0];
			if(type.trim().equalsIgnoreCase("text/html")){
				isHtml=true;
			}
		}
		return isHtml;
	}

	/**
	 * Generate Unique primary key
	 * @return String id
	 */
	public static String getUniqueId(){
		String id = null;
		UID uuid = new UID();
		id = uuid.toString();
		return id;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static long convertDateToTime(String date){
		Date requestedDate = new Date();
		boolean fetched = false;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
			Date dt = sdf.parse(date);
			return dt.getTime();
		}catch(ParseException pe){
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			
		try{
			requestedDate =	sdf.parse(date);
			fetched = true;
		}catch(ParseException pe){
		}
		if(!fetched){
			SimpleDateFormat format2 = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss z");
			format2.setTimeZone(TimeZone.getTimeZone("GMT"));
			try{
				requestedDate = format2.parse(date);
				fetched = true;
			}catch(ParseException pe){
			}
		}
		if(!fetched){
			SimpleDateFormat format3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
			format3.setTimeZone(TimeZone.getTimeZone("GMT"));
			try{
				requestedDate = format3.parse(date);
				fetched = true;
			}catch(ParseException pe){
			}
		}
		return requestedDate.getTime();
	}

	/**
	 * This method checks if the document at the specified path is modified
	 * @param path
	 * @param date
	 * @return
	 */
	public static boolean isDocModified(String path, String date){
		boolean modified = false;
		Date requestedDate = new Date();
		boolean fetched = false;
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try{
		requestedDate =	sdf.parse(date);
		fetched = true;
		}catch(ParseException pe){
		}
		if(!fetched){
			SimpleDateFormat format2 = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			try{
				requestedDate = format2.parse(date);
				fetched = true;
			}catch(ParseException pe){
			}
		}
		if(!fetched){
			SimpleDateFormat format3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			try{
				requestedDate = format3.parse(date);
				fetched = true;
			}catch(ParseException pe){
			}
		}
		if(!fetched)
			return true;
		File f = new File(path);
		long dateMod =  f.lastModified();
		modified = dateMod > requestedDate.getTime();		
		return modified;
	}


	public static String getAllChannels(EntityStore store){
		
		StringBuilder sb = new StringBuilder();		
		ChannelDA accessor = new ChannelDA(store);
		EntityCursor<Channel> entity_cursor = accessor.getPrimaryIndex().entities();
		Iterator<Channel> it = entity_cursor.iterator();
		
		sb.append("<table class=\"channelTable\"><tr><th>S.No.</th><th>Channel Name</th></tr>");
		int ctr=1;
		while(it.hasNext()){
			Channel entity = it.next();
			sb.append("<tr><td>");
			sb.append(ctr++);
			sb.append("</td><td>");
			sb.append("<a href=\"getchannel?id=");
			sb.append(entity.getChannelId());
			sb.append(">");
			sb.append(entity.getChannelName());
			sb.append("</a>");
			sb.append("</td></tr>");
		}
		entity_cursor.close();
		sb.append("</table>");	
		return sb.toString();
	}

	/**
	 * Converts the data from the URl to a String
	 * @param url
	 * @return String
	 */
	public static String getUrlContents(String url){
		StringBuilder sb = new StringBuilder();
		try{
			if(!(url.startsWith("http")|url.startsWith("https"))){
				url = "http://"+url;
			}
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			conn.setRequestProperty("User-Agent", "cis455crawler");
			conn.connect();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String read="";
			
			while((read=reader.readLine())!=null){
				sb.append(read);
			}
			reader.close();
			
		}catch(MalformedURLException mfe){
		}catch(IOException ioe){
		}
		return sb.toString();
	}


}
