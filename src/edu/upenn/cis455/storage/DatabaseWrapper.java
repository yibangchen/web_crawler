package edu.upenn.cis455.storage;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

/**
 * Class which is wrapper class for database
 * @author cis455
 *
 */
public class DatabaseWrapper {
		
	private static String envDirectory = 
			System.getProperty("user.home")
			+ File.separator+"crawlerdb";
	
	private static Environment myEnv;
	private static EntityStore myStore;
	
	public DatabaseWrapper(String dir){
		envDirectory = dir;
		generateEnvironment();
	}
	
	private static void generateEnvironment(){
			
		try{
			if(!new File(envDirectory).exists()){
				new File(envDirectory).mkdirs();
		}
		
		EnvironmentConfig envConfig = new EnvironmentConfig();
		StoreConfig config = new StoreConfig();
		
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(true);
		
		config.setAllowCreate(true);
		config.setTransactional(true);
	
		
		myEnv = new Environment(new File(envDirectory), envConfig);
		myStore = new EntityStore(myEnv, "Crawler_Entity_Store", config);
		}catch(DatabaseException e){
			System.err.println("closeEnv: myStore: " + 
					e.toString());
		}
	}
	
	public static boolean closeEnvironment(){
		try{
			if(myStore!=null){
				myStore.close();
			}

			if(myEnv!=null){
				myEnv.close();
				return true;
			
			}else{
				return false;
			}
			
		}catch(DatabaseException e){
			System.err.println("closeEnv: myStore: " + 
					e.toString());
		}catch(Exception e){
			System.err.println("closeEnv: myStore: " + 
					e.toString());
		}
		return false;
		
	}

	public static Environment getMyEnv() {
		if(myEnv == null) generateEnvironment();
		return myEnv;
	}

	public static EntityStore getStore() {
		if(myStore==null) generateEnvironment();
		return myStore;
	}	
}