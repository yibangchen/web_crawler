package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.*;

/**
 * Entity to store crawlers
 * @author Yibang Chen
 *
 */

@Entity
public class Crawler {

	@PrimaryKey
	private String crawlerRobotId;
	
	@SecondaryKey(relate = Relationship.ONE_TO_MANY)
	private String crawlerDomain;
	private String crawlerRobotRule;
	private String crawlerRobotRuleType;
	private long crawlerLastVisited;

	public String getCrawlerRobotId() {
		return crawlerRobotId;
	}

	public void setCrawlerRobotId(String crawlerRobotId) {
		this.crawlerRobotId = crawlerRobotId;
	}

	public String getCrawlerDomain() {
		return crawlerDomain;
	}

	public void setCrawlerDomain(String crawlerDomain) {
		this.crawlerDomain = crawlerDomain;
	}

	public String getCrawlerRobotRule() {
		return crawlerRobotRule;
	}

	public void setCrawlerRobotRule(String crawlerRobotRule) {
		this.crawlerRobotRule = crawlerRobotRule;
	}

	public String getCrawlerRobotRuleType() {
		return crawlerRobotRuleType;
	}

	public void setCrawlerRobotRuleType(String crawlerRobotRuleType) {
		this.crawlerRobotRuleType = crawlerRobotRuleType;
	}

	public long getCrawlerLastVisited() {
		return crawlerLastVisited;
	}

	public void setCrawlerLastVisited(long crawlerLastVisited) {
		this.crawlerLastVisited = crawlerLastVisited;
	}
	
	
}