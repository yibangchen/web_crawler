package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.*;

/**
 * Entity to store content of XML (and RSS) documents
 * @author Yibang Chen
 *
 */

@Entity
public class XmlDocument implements WebDocument{

	
	@PrimaryKey
	private String pageId;
	
	@SecondaryKey(relate=Relationship.ONE_TO_ONE)
	private String pageUrl;
	@SecondaryKey(relate=Relationship.ONE_TO_ONE)
	private String message;

	private String docContent;
	private String domain;
	private long lastParsed;
	private long lastUpdated;
	private boolean isParseAllowed;
	
	public String getXmlPageId() {
		return pageId;
	}

	public void setXmlPageId(String xmlPageId) {
		this.pageId = xmlPageId;
	}

	public String getXmlDocUrl() {
		return pageUrl;
	}

	public void setXmlDocUrl(String xmlPageUrl) {
		this.pageUrl = xmlPageUrl;
	}

	public String getXmlDomain() {
		return domain;
	}

	public void setXmlDomain(String xmlDomain) {
		this.domain = xmlDomain;
	}

	public long getXmlLastParsed() {
		return lastParsed;
	}

	public void setXmlLastParsed(long xmlLastParsed) {
		this.lastParsed = xmlLastParsed;
	}

	public long getLastModified() {
		return lastUpdated;
	}

	public void setLastModified(long modifiedTime) {
		this.lastUpdated = modifiedTime;
	}

	public boolean isXmlParseBanned() {
		return isParseAllowed;
	}

	public void setXmlParseBanned(boolean xmlParseBanned) {
		this.isParseAllowed = xmlParseBanned;
	}

	public String getXmlContent() {
		return docContent;
	}

	public void setXmlContent(String xmlContent) {
		this.docContent = xmlContent;
	}

	public String getMessageDigest() {
		return message;
	}

	public void setMessageDigest(String messageDigest) {
		this.message = messageDigest;
	}
}