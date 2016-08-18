package test.edu.upenn.cis455;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import org.junit.Test;
import edu.upenn.cis455.crawler.HelperFunctions;

public class HelperFunctionTest extends TestCase {
	
	@Test
	public void testGetUrlContent(){
		assertNotNull("Check if can get content from URL", HelperFunctions.getUrlContents("http://www.google.com"));
	}
	
	@Test
	public void testGetUniqueId() {
		String Id1 = HelperFunctions.getUniqueId();
		assertNotNull("Check if able to get an ID", Id1);
		String Id2 = HelperFunctions.getUniqueId();
		assertNotEquals("Check if new Id is different", Id1, Id2);
	}

}
