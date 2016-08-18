package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;
import edu.upenn.cis455.storage.CrawlHistory;

/**
 * Accessor for VisitedListEntity
 * @author cis455
 *
 */
public class CrawlHistoryDA {
	
private PrimaryIndex<String, CrawlHistory> primaryIndex;
	
	
	
	public CrawlHistoryDA(EntityStore store){
		
		primaryIndex = store.getPrimaryIndex(String.class, CrawlHistory.class);
		
	}

	public PrimaryIndex<String, CrawlHistory> getPrimaryIndex() {
		return primaryIndex;
	}

	
	public List<CrawlHistory> fetchAllEntities(){
		List<CrawlHistory> crawlerList = new ArrayList<CrawlHistory>();
		EntityCursor<CrawlHistory> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<CrawlHistory> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				crawlerList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return crawlerList;
	}
	
	public CrawlHistory fetchEntityFromPrimaryKey(String primaryKey){
		CrawlHistory entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
		
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public CrawlHistory putEntity(CrawlHistory entity){
		return this.primaryIndex.put(entity);
	}

}
