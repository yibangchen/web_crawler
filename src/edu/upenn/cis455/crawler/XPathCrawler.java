package edu.upenn.cis455.crawler;

import java.io.File;

public class XPathCrawler {
	
	public static void main(String[] args) throws Exception{

		if(args.length != 3) exit();
		else{
			ArgsParser parser = new ArgsParser(args);			
			if(!parser.areArgsValid()) exit();
			else new CrawlerLauncher(parser.dbDir,parser.urlString,parser.maxsize).launchCrawler();
		}
	}
	
	/**
	 * This method commits the exit action in case of bad arguments
	 */
	private static void exit(){
		System.out.println("Invalid argument!");
		System.out.println("Usage:\n"+
				"java -jar crawler.jar [Start URL] [Database Dir] [Max Docuemnt Size]\n");
		System.exit(1);
	}
	
	/**
	 * This inner class parses the passed in arguments and check validity
	 * @author user
	 *
	 */
	public static class ArgsParser {
		String dbDir;
		String urlString;
		long maxsize;
		String[] args;	

		public ArgsParser(String[] args){
			this.args = args;		
		}
		
		public boolean areArgsValid(){
			try{			
				File file = new File(this.args[1]);
				file.getCanonicalPath();			
				this.urlString = this.args[0]; 
				this.maxsize = Long.parseLong(this.args[2])*1000000;
				this.dbDir = this.args[1];				
				return true;
			}catch(Exception e){
				return false;	
			}
		}
	}
}