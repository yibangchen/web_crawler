package edu.upenn.cis455.storage;

import java.util.HashMap;
import java.util.Set;

import com.sleepycat.persist.model.*;

public class ChannelTest {
	
	@PrimaryKey
	private String channelName;

	private String[] xPaths;
	private String url;//url to style sheet
	private HashMap<String, String> XMLFiles; //
	
	
	private String user;
	
	public ChannelTest() {
		XMLFiles = new HashMap<String, String>();
	}
	
	public ChannelTest(String name, String user, String url, String[] xPaths){
		this.channelName = name;
		this.setUser(user);
		this.setUrl(url);
		this.setxPaths(xPaths);
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String[] getxPaths() {
		return xPaths;
	}

	public void setxPaths(String[] xPaths) {
		this.xPaths = xPaths;
	}
	
	public void addFile(String url, String content) {
		XMLFiles.put(url, content);
	}
	
	// find a file by its url
	public String getFile(String url) {
		return XMLFiles.get(url);
	}
	
	public Set<String> getUrlSet() {
		return XMLFiles.keySet();
	}
	
	public boolean containsFile(String url) {
		return XMLFiles.containsKey(url);
	}	
}

