package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;
//import edu.upenn.cis455.storage.Crawler;
import edu.upenn.cis455.storage.HtmlDocument;
//import edu.upenn.cis455.storage.XmlDocument;

/**
 * Accessor class for HtmlDocument
 * @author cis455
 *
 */

public class HtmlDocumentDA{
	
private PrimaryIndex<String, HtmlDocument> primaryIndex;
	
	private SecondaryIndex<String, String, HtmlDocument> secondaryIndex;
	
	private SecondaryIndex<String, String, HtmlDocument> digestIndx;
	
	public HtmlDocumentDA(EntityStore store){
		
		primaryIndex = store.getPrimaryIndex(String.class, HtmlDocument.class);
		secondaryIndex = store.getSecondaryIndex(primaryIndex,String.class, "htmlPageUrl" );
		digestIndx = store.getSecondaryIndex(primaryIndex,String.class, "messageDigest" );
		
	}

	public PrimaryIndex<String, HtmlDocument> getPrimaryIndex() {
		return primaryIndex;
	}


	public SecondaryIndex<String, String, HtmlDocument> getSecondaryIndex() {
		return secondaryIndex;
	}
	
	public SecondaryIndex<String, String, HtmlDocument> getDigestIndex() {
		return digestIndx;
	}
	
	public List<HtmlDocument> fetchAllEntities(){
		List<HtmlDocument> crawlerList = new ArrayList<HtmlDocument>();
		EntityCursor<HtmlDocument> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<HtmlDocument> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				crawlerList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return crawlerList;
	}
	
	public HtmlDocument fetchEntityFromPrimaryKey(String primaryKey){
		HtmlDocument entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
	
	public HtmlDocument fetchEntityFromSecondaryKey(String secondaryKey){
		HtmlDocument entity = this.secondaryIndex.get(secondaryKey);
		
		return entity;
	}
	
	public HtmlDocument fetchEntityFromDigestKey(String messageDigest){
		HtmlDocument entity = this.digestIndx.get(messageDigest);
		
		return entity;
	}
	
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public HtmlDocument putEntity(HtmlDocument entity){
		
		return this.primaryIndex.put(entity);
		
	}
	
}
