package edu.upenn.cis455.storage;

import java.io.File;

import com.sleepycat.je.DatabaseException;
//import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.*;


public class BerkeleyDBWrapper {
	
	private static BerkeleyDBWrapper wrapper;

	private Environment myEnv = null;
	private EnvironmentConfig myEnvConfig = null;
	private EntityStore myStore = null;	
//	private String storeName;
	
	public static PrimaryIndex<String, Document> docIndex;
	public static PrimaryIndex<String, Channel> channelIndex;
	public static PrimaryIndex<String, XPath> xPathIndex;
	public static SecondaryIndex<String, String, XPath> xPathUrlRef;
	public static SecondaryIndex<String, String, Channel> channelXPathRef;
	
	// private constructor to enforce singleton implementation
	private BerkeleyDBWrapper (String dbDir, String storeName) {
		openEnvironment(dbDir, storeName);
		
		docIndex = myStore.getPrimaryIndex(String.class, Document.class);
		xPathIndex = myStore.getPrimaryIndex(String.class, XPath.class);
	}
	
	public static BerkeleyDBWrapper getInstance(String envPath, String nameOfStore) {
		if (wrapper == null)
			wrapper = new BerkeleyDBWrapper(envPath, nameOfStore);
		return wrapper;
	}
	
	public static BerkeleyDBWrapper getInstance() {
		return wrapper;
	}
	
	private void openEnvironment(String dbDir, String storeName) {
		
		File dir = new File(dbDir);
		if (dir != null)
			System.out.println("Database created");
		
		myEnvConfig = new EnvironmentConfig();
		myEnvConfig.setAllowCreate(true);
		myEnvConfig.setTransactional(true);
		
		myEnv = new Environment(dir, myEnvConfig);
		
		StoreConfig myStoreConfig = new StoreConfig();
		myStoreConfig.setAllowCreate(true);
		myStoreConfig.setTransactional(true);
		
		myStore = new EntityStore(myEnv, storeName, myStoreConfig);
	}
	
	private void closeEnvironment() {
		System.out.println("*******Closing environment and entity store...");
		if (myStore != null) {
			try {
				myStore.close();
			} catch (DatabaseException e) {
				System.err.println("closeEnv: myStore: " + 
						e.toString());
			}
		}
		
		if (myEnv != null) {
			try {
				myEnv.close();
			} catch(DatabaseException e) {
				System.err.println("closeEnv: myStore: " + 
						e.toString());
			}
		}
	}
	
	
	public void XPathToUrl() {
		
	}
	
	
	
	

}
