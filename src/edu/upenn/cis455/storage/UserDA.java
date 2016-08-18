package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

import edu.upenn.cis455.storage.Crawler;
import edu.upenn.cis455.storage.HtmlDocument;
import edu.upenn.cis455.storage.User;

/**
 * Accessor for User
 * @author Yibang Chen
 *
 */

public class UserDA {
	
	private PrimaryIndex<String, User> primaryIndex;
	private SecondaryIndex<String, String, User> secondaryIndex;
	
	public UserDA(EntityStore store){
		
		primaryIndex = store.getPrimaryIndex(String.class, User.class);
		secondaryIndex = store.getSecondaryIndex(primaryIndex,String.class, "userName");		
	}

	public PrimaryIndex<String, User> getPrimaryIndex() {
		return primaryIndex;
	}

	public SecondaryIndex<String, String, User> getSecondaryIndex() {
		return secondaryIndex;
	}
	
	public List<User> fetchAllEntities(){
		List<User> userAccessorList = new ArrayList<User>();
		EntityCursor<User> channel_cursor = this.primaryIndex.entities();
		
		try{
			Iterator<User> it = channel_cursor.iterator();
			while(it.hasNext()){
				userAccessorList.add(it.next());
			}
		}finally{
			channel_cursor.close();
		}

		return userAccessorList;
	}
	
	public User fetchEntityFromPrimaryKey(String primaryKey){
		
		User entity = this.primaryIndex.get(primaryKey);
		return entity;
	}
	
	public User fetchEntityFromSecondaryKey(String secondaryKey){
		User entity = this.secondaryIndex.get(secondaryKey);
		return entity;
	}
	
	public boolean deleteEntity(String primaryKey){
		return this.primaryIndex.delete(primaryKey);
	}
	
	public User putEntity(User entity){
		return this.primaryIndex.put(entity);
	}
	
	
}