package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.*;

/**
 * This class stores user's credentials
 * @author Yibang Chen
 *
 */
@Entity
public class User {

	@PrimaryKey
	private String userId;
	
	@SecondaryKey(relate = Relationship.ONE_TO_ONE)
	private String userName;
	private String password;
	private String userType;
	private long lastLogin;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return userName;
	}
	public void setName(String userName) {
		this.userName = userName;
	}
	public long getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
}
