package edu.upenn.cis455.xpathengine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.List;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XPathEngineImpl implements XPathEngine {
	
	private String xmlUri;
	private String[] queries;
	private String[] paths;
	
	public XPathEngineImpl() {	  
	    // Do NOT add arguments to the constructor!!
	}
		
	public void setXPaths(String[] s) {
		if (s == null) return;
		queries = s;
	}
	
	public boolean isValid(int i) {
		String xPath = queries[i].trim();
		if (xPath.length() == 0) 	return false;
		if (xPath.charAt(0) != '/') return false;
		
		xPath = xPath.substring(xPath.indexOf('/') + 1);
		if (! checkStep(xPath)) 	return false;
		return true;
	}
		
	public boolean[] evaluate(Document d) { 
	    List<XPathObj> tags = new ArrayList<XPathObj>();
    	for (int i = 0; i < paths.length; i++)
    		tags.add(getXPath(i));
		
    	SAXHandler handler = new SAXHandler(tags);
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	InputStream xmlBytes = new ByteArrayInputStream(xmlUri.getBytes());
    	try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(xmlBytes, handler);
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	for (int i = 0; i < handler.queryFails.length; i++) {
    		handler.queryMatches[i] = handler.queryMatches[i] && handler.queryFails[i];
    	}
    	
		return handler.queryMatches; 
	}
	
	private boolean checkStep (String xPath) {
		String regexPattern = "\\s*\\w+\\s*.*";
		
		if (xPath.matches(regexPattern))		
			return false;
		
		if (	xPath.contains("[") && (! checkPredicate(xPath)))
			return false;
		
		if (xPath.contains("/")) {
			int ind = xPath.indexOf('/') + 1;
			if (	ind != xPath.length() && (!checkStep(xPath.substring(ind))))
				return false;
		}
		
		return true;
	}
	
	private boolean checkPredicate(String xPath) {
		char ch;
		int stepCt = 0;
		String value = "";
		
		for (int i = xPath.indexOf('[') +1; i< xPath.length(); i++) {
			ch = xPath.charAt(i);
			value += ch;
			if (ch == '[') stepCt++;
			if (ch == ']') {
				if (--stepCt == 0) {
					int len = value.length()-2;
					if (! evaluateTest(value.substring(0, len))) return false;
					else break;
				}
			}
		}
		
		return true;
	}
	
	private boolean evaluateTest(String xPath) {
		String textPattern = "\\s*text()\\s*=\\s*\"\\s*.*\\s*\"\\s*";
		String containPattern = "\\s*contains[(]\\s*text[(][)]\\s*[,]\\s*[\"]\\s*.*\\s*[\"]\\s*[)]\\s*";
		String attributePattern = "\\s*@\\w+\\s*=\\s*\"\\s*.*\\s*\"";
		
		if (	xPath.matches(textPattern) ||
				xPath.matches(containPattern) ||
				xPath.matches(attributePattern) ||
				checkStep(xPath))
			return true;
		
		return false;
	}
	
	private XPathObj getXPath(int queryId) {
		String xPath = paths[queryId];
		Stack<XPathObj> pathStack = new Stack<XPathObj>();
		
		XPathObj head = null;
		XPathObj curr = null;
		XPathObj prev = null;
		
		int objIndex = 0;
		int ind = 1;
		char ch; //the current character
		String name = "";
		boolean isFirst = true;
		
		while (ind < xPath.length()) {
			ch = xPath.charAt(ind);
			
			if (ch == '/') {
				if (! name.trim().isEmpty()) {
					curr = new XPathObj(queryId, objIndex, name.trim());
					if (isFirst){
						head = curr;
						isFirst = false;
					} else {
						prev.addSuccessor(curr);
					}
					prev = curr;
				}
				objIndex++;
				name = "";
				ind++;
				continue;
			} else if (ch == '[') {
				String attrPattern = "\\s*@\\w+\\s*=\\s*\"\\s*.*\\s*\".*";
				String attrGroup = "\\s*@(\\w+)\\s*=\\s*\"(.*?)\"(.*)";
				String textPattern = "\\s*text[(][)]\\s*=\\s*\"\\s*.*\\s*\".*";
				String textGroup = "\\s*text[(][)]\\s*=\\s*\"(.*?)\"(.*)";
				String containsPattern = "\\s*contains[(]\\s*text[(][)]\\s*[,]\\s*\"\\s*.*\\s*\"\\s*[)].*";
				String containsGroup = "\\s*contains[(]\\s*text[(][)]\\s*[,]\\s*\"(.*?)\"\\s*[)](.*)";
				String trimmedPath;
				
				if (! name.trim().isEmpty()) {
					curr = new XPathObj(queryId, objIndex, name.trim());
					if (isFirst) {
						head = curr;
						isFirst = false;
					} else {
						prev.addSuccessor(curr);
					}
					prev = curr;
				}
				ind++;
				
				trimmedPath = xPath.substring(ind, xPath.length());
				if (trimmedPath.matches(attrPattern)){
					Pattern p = Pattern.compile(attrGroup);
					Matcher m = p.matcher(trimmedPath);
					if (m.matches()) {
						String attName = m.group(1);
						String attVal = m.group(2);
						ind += m.start(3);
						curr.addAttribute(attName, attVal);
					}
				}
				
				trimmedPath = xPath.substring(ind, xPath.length());
				if (trimmedPath.matches(textPattern)){
					Pattern p = Pattern.compile(textGroup);
					Matcher m = p.matcher(trimmedPath);
					if (m.matches()) {
						String text = m.group(1);
						ind += m.start(2);
						curr.setText(text);
					}
				}
				
				trimmedPath = xPath.substring(ind, xPath.length());
				if (trimmedPath.matches(containsPattern)){
					Pattern p = Pattern.compile(containsGroup);
					Matcher m = p.matcher(trimmedPath);
					if (m.matches()) {
						String value = m.group(1);
						ind += m.start(2);
						curr.addContains(value);
					}
				}
				
				pathStack.push(curr);
				objIndex++;
				name = "";
				continue;
			} else if (ch == ']') {
				if (! name.trim().isEmpty()) {
					curr = new XPathObj(queryId, objIndex, name.trim());
					prev.addSuccessor(curr);
				}
				curr = pathStack.pop();
				objIndex = curr.getLevel();
				name = "";
				ind++;
				continue;
			}
			
			name += ch;
			ind++;
		}
		
		return head;
	}

	public void setXmlUri(String xml) {
		this.xmlUri = xml;
	}
}
