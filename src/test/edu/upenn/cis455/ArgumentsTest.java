package test.edu.upenn.cis455;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.upenn.cis455.crawler.*;

public class ArgumentsTest {
	
	@Test
	public void validateArguments() {
		String[] args = {"https://www.bbc.com"};
		XPathCrawler.ArgsParser launch = new XPathCrawler.ArgsParser(args);
		assertEquals("Valid arguments", false, launch.areArgsValid());
	}
	
	@Test
	public void validateArguments1() {
		String[] args = {"bbc.com","C://Documents", "64"};
		XPathCrawler.ArgsParser launch = new XPathCrawler.ArgsParser(args);
		assertEquals("Valid arguments", true, launch.areArgsValid());
	}
}
