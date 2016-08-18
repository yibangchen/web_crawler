package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;
import edu.upenn.cis455.storage.Robots;

/**
 * Accessor for Robots Entity
 * @author cis455
 *
 */
public class RobotsDA {
	
private PrimaryIndex<String, Robots> primaryIndex;
	

	public RobotsDA(EntityStore store){
		
		primaryIndex = store.getPrimaryIndex(String.class, Robots.class);
		
	}

	public PrimaryIndex<String, Robots> getPrimaryIndex() {
		return primaryIndex;
	}


	
	public List<Robots> fetchAllEntities(){
		List<Robots> robotsList = new ArrayList<Robots>();
		EntityCursor<Robots> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<Robots> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				robotsList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return robotsList;
	}
	
	public Robots fetchEntityFromPrimaryKey(String primaryKey){
		
		Robots entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
	
		
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public Robots putEntity(Robots entity){
		
		return this.primaryIndex.put(entity);
		
	}

}
