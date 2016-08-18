package edu.upenn.cis455.crawler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.upenn.cis455.storage.*;
import edu.upenn.cis455.xpathengine.XPathEngine;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

/**
 * This class launches crawlers
 * @author Yibang Chen
 *
 */
public class CrawlerLauncher {
	
	private final int THREAD_NUM = 100;
	private String urlString;
	private long maxDocSize;
	private ThreadPool pool;
	private RobotsDA robotsDA;
	private HtmlDocumentDA htmlDA;
	private XmlDocumentDA xmlDA;
	private CrawlHistoryDA crawlHistDA;
	
	public CrawlerLauncher(String dbDir, String url, long maxsize){
			this.urlString = url;
			this.maxDocSize = maxsize;
			new DatabaseWrapper(dbDir);
			robotsDA = new RobotsDA(DatabaseWrapper.getStore());
			htmlDA = new HtmlDocumentDA(DatabaseWrapper.getStore());
			xmlDA = new XmlDocumentDA(DatabaseWrapper.getStore());
			crawlHistDA = new CrawlHistoryDA(DatabaseWrapper.getStore());
			this.pool = new ThreadPool(THREAD_NUM,crawlHistDA,xmlDA,htmlDA,maxsize,robotsDA);
	}	
	
	/**
	 * Launch crawler
	 */
	public void launchCrawler() throws Exception{
		XmlXpathMatcher matcher = new XmlXpathMatcher();				
		CrawlerManager manager = new CrawlerManager(urlString);
		
		manager.start();
		while(!this.pool.isShutdownRequested()){
			matcher.match();
			Thread.sleep(2000);
		}
		DatabaseWrapper.closeEnvironment();
		this.pool.shutDown();
	}
	
	/**
	 * This class launches the crawler
	 */
	class CrawlerManager{
		private String baseUrl;
		public CrawlerManager(String baseUrl){
				this.baseUrl = baseUrl;	
		}
		
		public void start() throws Exception{
			truncateHistory();
			Crawler crawler = new Crawler(this.baseUrl, xmlDA, htmlDA, maxDocSize,robotsDA);
			pool.execute(crawler);
		}
		
		/**
		 * Clear history of visited links in database
		 */
		private void truncateHistory(){
			System.out.println("Clearing old visited links");
			List<CrawlHistory> ent = crawlHistDA.fetchAllEntities();
			for(CrawlHistory entity:ent){
				crawlHistDA.deleteEntity(entity.getPageUrl());
			}
		}
	}
	
	/**
	 * This class matches the xpaths to the xml documents
	 */
	class XmlXpathMatcher {	
		private ChannelDA channelDA;
		private ChannelXmlDA channelXmlDA;
		private XmlDocumentDA xmlDA;
			
		public XmlXpathMatcher(){
				channelDA = new ChannelDA(DatabaseWrapper.getStore());
				channelXmlDA = new ChannelXmlDA(DatabaseWrapper.getStore());
				xmlDA = new XmlDocumentDA(DatabaseWrapper.getStore());
		}
		
		/**
		 * Match the xpaths and xml's
		 */
		public void match(){
				List<XmlDocument> xmlDocuments = this.xmlDA.fetchAllEntities();			
				List<Channel> channels = this.channelDA.fetchAllEntities();
				
				for(Channel channel:channels){
					String[] xPathList = channel.getXPath().split(";");				
					ChannelXmlMap channelXml = this.channelXmlDA.fetchEntityFromPrimaryKey(channel.getChannelId());
					Set<String> xmlDocs=null;
					if(channelXml!=null) xmlDocs = channelXml.getXmlPageID();
					
					for(XmlDocument doc: xmlDocuments){
						if(xmlDocs!=null){
							if(xmlDocs.contains(doc.getXmlPageId())) continue;
						}					
							XPathEngine engine = new XPathEngineImpl();
							engine.setXPaths(xPathList);
							boolean[] status = engine.evaluate(HelperFunctions.getDocs(doc.getXmlContent()));
							loop:for(boolean b : status){
								if(b){								
									if(xmlDocs==null) xmlDocs = new HashSet<String>();
									xmlDocs.add(doc.getXmlPageId());
									break loop;
								}
							}
					}
						
					if(channelXml == null & xmlDocs !=null){
						channelXml = new ChannelXmlMap();
						channelXml.setChannelId(channel.getChannelId());
						channelXml.setXmlPageID(xmlDocs);
						this.channelXmlDA.putEntity(channelXml);
					}else if(xmlDocs!=null){
							channelXml.setXmlPageID(xmlDocs);
							this.channelXmlDA.putEntity(channelXml);
					}
				}
		}
	}
}
