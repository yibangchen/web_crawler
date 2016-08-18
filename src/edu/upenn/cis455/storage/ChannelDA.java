package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.*;
import edu.upenn.cis455.storage.Channel;

/**
 * Accessor class for Channel Entity
 * @author cis455
 *
 */
public class ChannelDA {
	
	private PrimaryIndex<String, Channel> primaryIndex;
	private SecondaryIndex<String, String, Channel> secondaryIndex;
	
	public ChannelDA(EntityStore store){		
		primaryIndex = store.getPrimaryIndex(String.class, Channel.class);
	}

	public PrimaryIndex<String, Channel> getPrimaryIndex() {
		return primaryIndex;
	}

	public SecondaryIndex<String, String, Channel> getSecondaryIndex() {
		return secondaryIndex;
	}
	
	public List<Channel> fetchAllEntities(){
		List<Channel> channelList = new ArrayList<Channel>();
		EntityCursor<Channel> channel_cursor = this.primaryIndex.entities();
		
		try{
			
			Iterator<Channel> iter = channel_cursor.iterator();
			
			while(iter.hasNext()){
				
				channelList.add(iter.next());
				
			}
			
		}finally{
			channel_cursor.close();
		}
		
		
		return channelList;
	}
	
	public Channel fetchEntityFromPrimaryKey(String primaryKey){
		
		Channel entity = this.primaryIndex.get(primaryKey);
				
		return entity;
	}
	
	
	
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public Channel putEntity(Channel entity){
		
		return this.primaryIndex.put(entity);
		
	}
	
	
	
	

}
