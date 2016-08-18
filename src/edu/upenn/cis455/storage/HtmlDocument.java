package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.*;

/**
 * Entity to store crawled HTML content
 * @author Yibang Chen
 *
 */

@Entity
public class HtmlDocument implements WebDocument{
	
	@PrimaryKey
	private String htmlPageId;
	
	@SecondaryKey(relate=Relationship.ONE_TO_ONE)
	private String htmlPageUrl;
	
	@SecondaryKey(relate=Relationship.ONE_TO_ONE)
	private String messageDigest;
	
	private String htmlDomain;
	private long htmlLastParsed;
	private long htmlLastUpdated;
	private boolean htmlParseBanned;
	private String htmlContent;

	public String getHtmlPageId() {
		return htmlPageId;
	}

	public void setHtmlPageId(String htmlPageId) {
		this.htmlPageId = htmlPageId;
	}

	public String getHtmlPageUrl() {
		return htmlPageUrl;
	}

	public void setHtmlPageUrl(String htmlPageUrl) {
		this.htmlPageUrl = htmlPageUrl;
	}

	public String getHtmlDomain() {
		return htmlDomain;
	}

	public void setHtmlDomain(String htmlDomain) {
		this.htmlDomain = htmlDomain;
	}

	public long getHtmlLastParsed() {
		return htmlLastParsed;
	}

	public void setHtmlLastParsed(long htmlLastParsed) {
		this.htmlLastParsed = htmlLastParsed;
	}

	public long getLastModified() {
		return htmlLastUpdated;
	}

	public void setLastModified(long modifiedTime) {
		this.htmlLastUpdated = modifiedTime;
	}

	public boolean isHtmlParseBanned() {
		return htmlParseBanned;
	}

	public void setHtmlParseBanned(boolean htmlParseBanned) {
		this.htmlParseBanned = htmlParseBanned;
	}

	public String isHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getMessageDigest() {
		return messageDigest;
	}

	public void setMessageDigest(String messageDigest) {
		this.messageDigest = messageDigest;
	}
}