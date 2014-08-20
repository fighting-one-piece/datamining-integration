package org.project.common.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentSet {

	private List<Document> documents = null;
	
	private Map<String, Double> selectedFeatures = null;
	
	private String[] words = null;
	
	private double preMaxFit = 0;
	
	private double postMaxFit = 0;
	
	private Map<String, Integer> wordToCount = null;

	public List<Document> getDocuments() {
		if (null == documents) {
			documents = new ArrayList<Document>();
		}
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public int docLength() {
		return documents.size();
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

	public Map<String, Integer> getWordToCount() {
		if (null == wordToCount) {
			 wordToCount = new HashMap<String, Integer>();
		}
		return wordToCount;
	}

	public void setWordToCount(Map<String, Integer> wordToCount) {
		this.wordToCount = wordToCount;
	}

	public Map<String, Double> getSelectedFeatures() {
		if (null == selectedFeatures) {
			selectedFeatures = new HashMap<String, Double>();
		}
		return selectedFeatures;
	}

	public void setSelectedFeatures(Map<String, Double> selectedFeatures) {
		this.selectedFeatures = selectedFeatures;
	}
	
	

}
