package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;

//import edu.upenn.cis455.storage.DatabaseWrapper;
//import edu.upenn.cis455.storage.Channel;
//import edu.upenn.cis455.storage.Crawler;
import edu.upenn.cis455.storage.UserChannelMap;
//import edu.upenn.cis455.storage.User;
/**
 * Accessor for UserChannelMap
 * @author cis455
 *
 */
public class UserChannelDA {
	
private PrimaryIndex<String, UserChannelMap> primaryIndex;
	
	private SecondaryIndex<String, String, UserChannelMap> secondaryIndex;
	
	public UserChannelDA(EntityStore store){
		
		primaryIndex = store.getPrimaryIndex(String.class, UserChannelMap.class);
		secondaryIndex = store.getSecondaryIndex(primaryIndex,String.class, "userId" );
		
	}

	public PrimaryIndex<String, UserChannelMap> getPrimaryIndex() {
		return primaryIndex;
	}


	public SecondaryIndex<String, String, UserChannelMap> getSecondaryIndex() {
		return secondaryIndex;
	}
	
	public List<UserChannelMap> fetchAllEntities(){
		List<UserChannelMap> userChannelList = new ArrayList<UserChannelMap>();
		EntityCursor<UserChannelMap> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<UserChannelMap> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				userChannelList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return userChannelList;
	}
	
	public UserChannelMap fetchEntityFromPrimaryKey(String primaryKey){
		
		UserChannelMap entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
	
	public UserChannelMap fetchEntityFromSecondaryKey(String secondaryKey){
		
		UserChannelMap entity = this.secondaryIndex.get(secondaryKey);
		
		return entity;
	}
	
	
	
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	
	
	public UserChannelMap putEntity(UserChannelMap entity){
		
		return this.primaryIndex.put(entity);
		
	}
	
}
