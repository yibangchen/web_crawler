package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.*;
import java.util.Date;

// Store Crawled HTML/ XML/ RSS document
@Entity
public class Document {
	@PrimaryKey
	private String url;
	
	private Date lastModified;
	private String content;
	private boolean isHtml; // HTML, XML, RSS
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setIsHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}
}
