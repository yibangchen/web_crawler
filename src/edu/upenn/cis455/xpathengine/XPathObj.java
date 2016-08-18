package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class XPathObj {
	
	private int queryId;
	private int level;
	private String name;
	private String text;
	
	private HashMap<String, String> attributes;
	private ArrayList<String> container;
	private ArrayList<XPathObj> successorList;
	
	public XPathObj(int queryId, int level, String name){
		this.queryId = queryId;
		this.level = level;
		this.name = name;
		this.attributes = new HashMap<String, String>();
		this.container = new ArrayList<String>();
		this.successorList = new ArrayList<XPathObj>();
	}
	
	public void addSuccessor(XPathObj next) {
		successorList.add(next);
	}
	
	//********* Below are getter and setter methods *********

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getQueryId() {
		return queryId;
	}

	public ArrayList<String> getContainer() {
		return container;
	}
	
	public int getSize() {
		return container.size();
	}
	
	public String getContains(int index) {
		return container.get(index);
	}
	
	public void addContains(String value){
		container.add(value);
	}

	public Set<String> getAttKeys() {
		return attributes.keySet();
	}
	
	public String getAttVal(String key) {
		return attributes.get(key);
	}
	
	public void addAttribute(String name, String value){
		attributes.put(name, value);
	}
	
	public ArrayList<XPathObj> getSuccessorList() {
		return successorList;
	}

}
