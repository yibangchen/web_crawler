package edu.upenn.cis455.storage;

// import static com.sleepycat.persist.model.DeleteAction.NULLIFY;
// import static com.sleepycat.persist.model.Relationship.MANY_TO_MANY;

// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Set;

import com.sleepycat.persist.model.*;

@Entity
public class Channel {
	
	@PrimaryKey
	private String id;
	private String name;
	private String xPath;
	private String xls;
	private String creator;
	private long lastModified;
	
	// @SecondaryKey(	relate = MANY_TO_MANY
	// 				, relatedEntity = XPath.class
	// 				, onRelatedEntityDelete = NULLIFY)
	// Set<String> matchedPaths = new HashSet<String>();	

	public String getChannelId() {
		return id;
	}

	public void setChannelId(String channelId) {
		this.id = channelId;
	}

	public String getChannelName() {
		return name;
	}

	public void setChannelName(String channelName) {
		this.name = channelName;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String userName) {
		this.creator = userName;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long timestamp) {
		this.lastModified = timestamp;
	}

	public String getXPath() {
		return xPath;
	}

	public void setXPath(String xPath) {
		this.xPath = xPath;
	}

	public String getXls() {
		return xls;
	}

	public void setXls(String xlsContent) {
		this.xls = xlsContent;
	}
}

