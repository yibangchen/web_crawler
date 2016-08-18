package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;
import edu.upenn.cis455.storage.Crawler;

/**
 * Accessor for Crawler Class
 * @author cis455
 *
 */


public class CrawlerDA {

	
	private PrimaryIndex<String, Crawler> primaryIndex;
	
	private SecondaryIndex<String, String, Crawler> secondaryIndex;
	
	public CrawlerDA(EntityStore store){
		
		primaryIndex = store.getPrimaryIndex(String.class, Crawler.class);
		secondaryIndex = store.getSecondaryIndex(primaryIndex,String.class, "crawlerDomain" );
		
	}

	public PrimaryIndex<String, Crawler> getPrimaryIndex() {
		return primaryIndex;
	}


	public SecondaryIndex<String, String, Crawler> getSecondaryIndex() {
		return secondaryIndex;
	}
	
	public List<Crawler> fetchAllEntities(){
		List<Crawler> crawlerList = new ArrayList<Crawler>();
		EntityCursor<Crawler> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<Crawler> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				crawlerList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return crawlerList;
	}
	
	public Crawler fetchEntityFromPrimaryKey(String primaryKey){
		
		Crawler entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
	
	public Crawler fetchEntityFromSecondaryKey(String secondaryKey){
		
		Crawler entity = this.secondaryIndex.get(secondaryKey);
		
		return entity;
	}
	
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public Crawler putEntity(Crawler entity){
		
		return this.primaryIndex.put(entity);
		
	}
	
}
