package org.project.modules.association.similar.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NewsGroups {

	private Map<String, Set<String>> category2docs = null;
	
	private Map<String, String[]> doc2words = null;

	public Map<String, Set<String>> getCategory2docs() {
		if (null == category2docs) {
			category2docs = new HashMap<String, Set<String>>();
		}
		return category2docs;
	}

	public void setCategory2docs(Map<String, Set<String>> category2docs) {
		this.category2docs = category2docs;
	}

	public Map<String, String[]> getDoc2words() {
		if (null == doc2words) {
			doc2words = new HashMap<String, String[]>();
		}
		return doc2words;
	}

	public void setDoc2words(Map<String, String[]> doc2words) {
		this.doc2words = doc2words;
	}
	
	
}
