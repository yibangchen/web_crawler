package edu.upenn.cis455.storage;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.*;
import static com.sleepycat.persist.model.Relationship.MANY_TO_MANY;
import static com.sleepycat.persist.model.DeleteAction.NULLIFY;

@Entity
public class XPath {
	
	@PrimaryKey
	private String xPath;
	
	@SecondaryKey(	relate = MANY_TO_MANY
					, relatedEntity = Document.class
					, onRelatedEntityDelete = NULLIFY)
	Set<String> matchedPaths = new HashSet<String>();
	
	public XPath(String path) {
		this.xPath = path;
	}
	
	public XPath() {
	}
	
	public String getXPath() {
		return xPath;
	}
	
	public boolean isUrlAdded(String url) {
		return matchedPaths.contains(url);
	}
	
	public void addUrl(String url) {
		matchedPaths.add(url);
	}
	
}
