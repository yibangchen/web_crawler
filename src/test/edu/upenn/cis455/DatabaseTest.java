package test.edu.upenn.cis455;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.Test;
import com.sleepycat.persist.EntityStore;
import edu.upenn.cis455.storage.*;
import edu.upenn.cis455.crawler.HelperFunctions;

public class DatabaseTest {
	
	@Test
	public void testNewUser() {		
		EntityStore store = new DatabaseWrapper("dbdir").getStore();
		User entity = new User();
		entity.setName("test"+Math.random());
		entity.setUserId(HelperFunctions.getUniqueId());
		entity.setLastLogin(new Date().getTime());
		entity.setUserType("test");
		
		assertNotEquals("Check for username", null, entity.getName());
	}

	@Test
	public void testGetUser() {		
		EntityStore store = new DatabaseWrapper("dbdir").getStore();
		UserDA accessor = new UserDA(store);
		User entity = new User();
		entity.setName("test"+Math.random());
		entity.setUserId(HelperFunctions.getUniqueId());
		entity.setLastLogin(new Date().getTime());
		entity.setUserType("test");
		
		accessor.putEntity(entity);		
		User ent2 = accessor.fetchEntityFromSecondaryKey(entity.getName());
		
		assertNotEquals("Check for username", null, ent2.getName());
	}

}
