package edu.upenn.cis455.storage;

import java.util.Set;
import java.util.HashSet;

import com.sleepycat.persist.model.*;
import static com.sleepycat.persist.model.Relationship.ONE_TO_ONE;
import static com.sleepycat.persist.model.DeleteAction.CASCADE;

/**
 * Entity to store the mapping between User and Channel entities
 * @author Yibang Chen
 *
 */

@Entity
public class UserChannelMap {

	@PrimaryKey
	private String mappingId;
	
	@SecondaryKey(	relate = ONE_TO_ONE
					, relatedEntity=User.class
					, onRelatedEntityDelete = CASCADE)
	private String userId;	
	private Set<String> channelIdSet = new HashSet<String>();

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String userChannelId) {
		this.mappingId = userChannelId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<String> getChannelSet() {
		return channelIdSet;
	}

	public void setChannel(Set<String> channelSet) {
		this.channelIdSet = channelSet;
	}
}