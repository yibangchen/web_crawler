package edu.upenn.cis455.crawler;

import java.util.Set;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.ChannelDA;
import edu.upenn.cis455.storage.UserChannelDA;
import edu.upenn.cis455.storage.UserChannelMap;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.storage.Channel;

/**
 * Utility clas for fetching user related questions
 * @author cis455
 *
 */
public class UserObject {
	
	private User entity;
	
	public UserObject(User entity)
	{
		this.entity = entity;
	}

	/**
	 * Check if the channel is a valid channel for the user
	 * @param channelId
	 * @return
	 */
	
	public boolean isUserChannelPresent(String channelId){
		UserChannelDA accessor = new UserChannelDA(DatabaseWrapper.getStore());		
		UserChannelMap userChannelentity = accessor.fetchEntityFromSecondaryKey(this.entity.getUserId());
		
		if(userChannelentity!=null){
			Set<String> channelIds = userChannelentity.getChannelSet();		
			if(channelIds!=null){
				if(channelIds.contains(channelId))
					return true;
			}
		}
		return false;		
	}
	
	/**
	 * Delete the channel from the user's channels
	 * @param channelId
	 * @return boolean isDeleted
	 */
	
	public boolean deleteChannel(String channelId){
		
		UserChannelDA accessor = new UserChannelDA(DatabaseWrapper.getStore());	
		UserChannelMap userChannelentity = accessor.fetchEntityFromSecondaryKey(this.entity.getUserId());
		ChannelDA channelAccessor = new ChannelDA(DatabaseWrapper.getStore());
		
		if(userChannelentity!=null){
			Set<String> channelIds = userChannelentity.getChannelSet();
			if(channelIds!=null){
				if(channelIds.contains(channelId)){
					Channel chEntity = channelAccessor.fetchEntityFromPrimaryKey(channelId);
					if(chEntity!=null){
						channelIds.remove(channelId);
						userChannelentity.setChannel(channelIds);
						accessor.putEntity(userChannelentity);
						channelAccessor.deleteEntity(chEntity.getChannelId());
						return true;
					}
				}
			}			
		}
				
		return false;
	}
	

}
