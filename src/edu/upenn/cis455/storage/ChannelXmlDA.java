package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;

import edu.upenn.cis455.storage.ChannelXmlMap;

/**
 * Accessor for ChannelXmlMap
 * @author cis455
 *
 */

public class ChannelXmlDA {
	
	private PrimaryIndex<String, ChannelXmlMap> primaryIndex;
	private SecondaryIndex<List, String, ChannelXmlMap> xmlPageIndex;

	
	public ChannelXmlDA(EntityStore store){
		
		primaryIndex = store.getPrimaryIndex(String.class, ChannelXmlMap.class);
		xmlPageIndex = store.getSecondaryIndex(primaryIndex,List.class, "xmlPageID" );

	}

	public PrimaryIndex<String, ChannelXmlMap> getPrimaryIndex() {
		return primaryIndex;
	}


		
	public SecondaryIndex<List, String, ChannelXmlMap> getXMLIndex() {
		return xmlPageIndex;
	}
	
	
	public List<ChannelXmlMap> fetchAllEntities(){
		List<ChannelXmlMap> crawlerList = new ArrayList<ChannelXmlMap>();
		EntityCursor<ChannelXmlMap> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<ChannelXmlMap> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				crawlerList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return crawlerList;
	}
	
	public ChannelXmlMap fetchEntityFromPrimaryKey(String primaryKey){
		ChannelXmlMap entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
	
		
	public ChannelXmlMap fetchEntityFromXMLKey(List<String> messageDigest){
		ChannelXmlMap entity = this.xmlPageIndex.get(messageDigest);
		return entity;
	}
	
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public ChannelXmlMap putEntity(ChannelXmlMap entity){
		
		return this.primaryIndex.put(entity);
		
	}
}
