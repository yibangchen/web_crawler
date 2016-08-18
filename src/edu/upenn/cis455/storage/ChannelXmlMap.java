package edu.upenn.cis455.storage;

import java.util.Set;
import java.util.HashSet;

import com.sleepycat.persist.model.*;

/**
 * Entity to store mapping between channel and xml documents
 * @author Yibang Chen
 *
 */

@Entity
public class ChannelXmlMap {
		
	@PrimaryKey
	private String channelId;
	
	@SecondaryKey(	relate=Relationship.MANY_TO_MANY
					, onRelatedEntityDelete=DeleteAction.CASCADE)
	private Set<String> xmlPageID = new HashSet<String>();

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Set<String> getXmlPageID() {
		return xmlPageID;
	}

	public void setXmlPageID(Set<String> xmlPageID) {
		this.xmlPageID = xmlPageID;
	}
}