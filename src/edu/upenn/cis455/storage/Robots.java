package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.*;
//import edu.upenn.cis455.crawler.RobotObject;

/**
 * Entity for Robots information
 * @author cis455
 *
 */

@Entity
public class Robots {

	@PrimaryKey
	private String domainName;
	private RobotObject robot;
	private long lastModified;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public RobotObject getRobots() {
	 	return robot;
	}

	public void setRobots(RobotObject robot) {
	 	this.robot = robot;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastUpdatedDate) {
		this.lastModified = lastUpdatedDate;
	}
}