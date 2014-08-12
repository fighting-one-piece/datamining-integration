package org.project.modules.algorithm.genetic.data;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

	private List<Document> docs = null;
	
	private String[] words = null;
	
	private double preMaxFit = 0;
	
	private double postMaxFit = 0;

	public List<Document> getDocs() {
		if (null == docs) {
			docs = new ArrayList<Document>();
		}
		return docs;
	}

	public void setDocs(List<Document> docs) {
		this.docs = docs;
	}
	
	public int docLength() {
		return docs.size();
	}
	
	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public void setMaxFit(double maxFit) {
		preMaxFit = postMaxFit;
		postMaxFit = maxFit;
	}
	
	public double getPreMaxFit() {
		return preMaxFit;
	}
	
	public double getPostMaxFit() {
		return postMaxFit;
	}

	public void setPreMaxFit(double preMaxFit) {
		this.preMaxFit = preMaxFit;
	}

	public void setPostMaxFit(double postMaxFit) {
		this.postMaxFit = postMaxFit;
	}
	
	

}
