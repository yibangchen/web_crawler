package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;

import edu.upenn.cis455.storage.DatabaseWrapper;
//import edu.upenn.cis455.storage.Crawler;
//import edu.upenn.cis455.storage.HtmlDocument;
//import edu.upenn.cis455.storage.UserChannelMap;
import edu.upenn.cis455.storage.XmlDocument;

/**
 * Accessor for XmlDocument
 * @author cis455
 *
 */
public class XmlDocumentDA {

	
private PrimaryIndex<String, XmlDocument> primaryIndex;
	
	private SecondaryIndex<String, String, XmlDocument> secondaryIndex;
	private SecondaryIndex<String, String, XmlDocument> digestIndex;
	
	public XmlDocumentDA(EntityStore store){	
		if(store==null){
			store = DatabaseWrapper.getStore();
		}
		
		primaryIndex = store.getPrimaryIndex(String.class, XmlDocument.class);
		secondaryIndex = store.getSecondaryIndex(primaryIndex,String.class, "xmlPageUrl" );
		digestIndex = store.getSecondaryIndex(primaryIndex,String.class, "messageDigest" );

	}

	public PrimaryIndex<String, XmlDocument> getPrimaryIndex() {
		return primaryIndex;
	}


	public SecondaryIndex<String, String, XmlDocument> getSecondaryIndex() {
		return secondaryIndex;
	}
	
	public SecondaryIndex<String, String, XmlDocument> getDigestIndex() {
		return digestIndex;
	}
	
	
	public List<XmlDocument> fetchAllEntities(){
		List<XmlDocument> crawlerList = new ArrayList<XmlDocument>();
		EntityCursor<XmlDocument> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<XmlDocument> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				crawlerList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return crawlerList;
	}
	
	public XmlDocument fetchEntityFromPrimaryKey(String primaryKey){
		XmlDocument entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
	
	public XmlDocument fetchEntityFromSecondaryKey(String secondaryKey){
		XmlDocument entity = this.secondaryIndex.get(secondaryKey);
		
		return entity;
	}
	
	public XmlDocument fetchEntityFromDigestKey(String messageDigest){
		XmlDocument entity = this.digestIndex.get(messageDigest);
		return entity;
	}
	
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public XmlDocument putEntity(XmlDocument entity){
		
		return this.primaryIndex.put(entity);
		
	}
	
}
