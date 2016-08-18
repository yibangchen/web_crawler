package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.*;

/**
 * Entity to store the history of sites visited
 * @author Yibang Chen
 *
 */

@Entity
public class CrawlHistory {
 
	@PrimaryKey
	private String pageUrl;
	
	private long visitedTime;

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public long getVisitedTime() {
		return visitedTime;
	}

	public void setVisitedTime(long visitedTime) {
		this.visitedTime = visitedTime;
	}
	
	
	
	
}