package edu.upenn.cis455.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.upenn.cis455.xpathengine.XPathEngine;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import edu.upenn.cis455.crawler.HelperFunctions;

/**
 * Servlet for handling the request coming in from the main servlet
 * @author cis455
 *
 */

@SuppressWarnings("serial")
public class XPathHandler extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		XPathEngine engine = new XPathEngineImpl();
		String xPaths = request.getParameter("xpath");
		String url = request.getParameter("url");
		PrintWriter writer = response.getWriter();
		String toWrite="";
		if(url!=null && xPaths!=null){
			
			String[] xPathTokens = xPaths.split(";");
			engine.setXPaths(xPathTokens);
			Document doc = getDocmentFromUrl(url);
			if(doc == null){
				doc = getUrlHtmlContent(url);
			}
			boolean[] xpathResults = engine.evaluate(doc);
			
			toWrite = getOutputDoc(url,xpathResults,xPathTokens,getServletContext().getResourceAsStream("/ServletAnswer.html"));
							
		}
		
		writer.write(toWrite);
		writer.close();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		System.out.println("test message");
		doPost(request, response);
	}
	
	/**
	 * Generate Document object from HTML document
	 * @param url
	 * @return Document
	 */
	private Document getUrlHtmlContent(String url){
	    String data = HelperFunctions.getUrlContents(url);
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		tidy.setShowWarnings(false);
		tidy.setShowErrors(0);
		Document doc = tidy.parseDOM(new ByteArrayInputStream(data.getBytes()), null);
		return doc;
	}
	
	/**
	 * Generates the output document as a String
	 * @param url
	 * @param results
	 * @param xPaths
	 * @param stream
	 * @return
	 */
	private  String getOutputDoc(String url, boolean[] results, String[] xPaths,InputStream stream){
		
		StringBuilder sb = new StringBuilder();
		try{
			String data = HelperFunctions.readFile(stream);
			data = data.replace("%%REPLACE URL HERE%%", url);
			data = data.replace("%%REPLACE TABLE HERE%%", HelperFunctions.generateTable(xPaths,results));
			return data;
		}catch(IOException ioe){
		}
					
		return sb.toString();
	}
	
	/**
	 * Convert the data from the URL into a w3c document object
	 * @param url
	 * @return Document
	 */
	private Document getDocmentFromUrl(String url){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try{ 
		    factory.setNamespaceAware(true);
		    builder = factory.newDocumentBuilder();
		    builder.setErrorHandler(new ErrorHandler() {
				
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					throw exception;
				}
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					throw exception;
				}
				@Override
				public void error(SAXParseException exception) throws SAXException {
					throw exception;					
				}
			});
		    Document doc = builder.parse(url);
		    
		    return doc;
		}catch(ParserConfigurationException pce){
			return null;
		}catch(IOException ioe){
			return null;
		}catch(SAXException sxe){
			return null;
		}
	}
	
}