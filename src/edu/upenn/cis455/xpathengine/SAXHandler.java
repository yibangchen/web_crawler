package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

public class SAXHandler extends DefaultHandler{
	
	int level = 0;
	boolean[] queryMatches;
	boolean[] queryFails;
	Stack<String> xPathStack;
	// key = name; value = list of XPathObj of same name
	private Map<String, List<XPathObj>> candidates;
	
	/**
	 * The constructor
	 * @param targets : list of target XPathObj to handle
	 */
	public SAXHandler(List<XPathObj> targets) {
		//set default: all XPathObj NOT matched
		queryMatches = new boolean[targets.size()];
		Arrays.fill(queryMatches, false);
		//set default: all XPathObj failed
		queryFails = new boolean[targets.size()];
		Arrays.fill(queryFails, true);
		
		xPathStack = new Stack<String>();
		candidates = new HashMap<String, List<XPathObj>>();
		this.initiateCandidates(targets);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		System.out.println("Start Element: " + qName);
		level++;
		if (! candidates.containsKey(qName)) return;
		
		boolean attTest = false;
		List<XPathObj> objList = candidates.get(qName);
		for (XPathObj obj : objList) {
			if (obj.getLevel() == level) {
				for (String att : obj.getAttKeys()) {
					if (! obj.getAttVal(att).equals(atts.getValue(att)))
						attTest = true;
				}
				
				if (attTest == false) {
					this.xPathStack.push(obj.getName());
					if (xPathStack.isEmpty()) {
						List<XPathObj> sucList = obj.getSuccessorList();
						for (XPathObj suc: sucList) {
							List<XPathObj> temp = candidates.get(suc.getName());
							if (temp == null) temp = new ArrayList<XPathObj>();
							temp.add(suc);
							candidates.put(suc.getName(), temp);
						}
					} else {
						queryMatches[obj.getQueryId()] = true;
					}
				}
			}
		}
	}
	
	public void endElement(String uri, String localName, String qName) {
		System.out.println("End Element: " + qName);
		
		this.xPathStack.pop();
		List<XPathObj> objList = candidates.get(qName);
		if (objList != null) {
			for (XPathObj obj: objList) {
				if (obj.getLevel() == level) {
					objList.remove(obj);
				}
			}
			candidates.put(qName, objList);
		}
		
		level--;
	}
	
	public void character(char[] ch, int start, int length) {
		String value = String.copyValueOf(ch, start, length).trim();
		String name = xPathStack.peek(); // The last pushed XPath object
		List<XPathObj> objList = candidates.get(name);
		
		for (XPathObj obj: objList) {
			if (obj.getLevel() == level) {
				// test text
				if (! value.isEmpty()) {
					if (! obj.getText().equals(value)) {
						queryFails[obj.getQueryId()] = false;
					}
				}
				// test container
				for (int i = 0; i <obj.getSize(); i++) {
					if (! value.matches(".*"+obj.getContains(i)+".*")) {
						queryFails[obj.getQueryId()] = false;
					}
				}
			}
		}
		
	}
	
	//************ below are helper functions ************
	/**
	 * This method fills the candidates with targets
	 */
	private void initiateCandidates(List<XPathObj> targets) {
		for (XPathObj obj : targets) {
			List<XPathObj> objList = candidates.get(obj.getName());
			if (objList == null)
				objList = new ArrayList<XPathObj>();
			objList.add(obj);
			candidates.put(obj.getName(), objList);
		}
	}
	

}
