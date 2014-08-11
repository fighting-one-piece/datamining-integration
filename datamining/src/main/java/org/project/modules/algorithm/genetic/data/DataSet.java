package org.project.modules.algorithm.genetic.data;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

	private List<Document> documents = null;
	
	private List<DocumentSimilarity> similarities = null;

	public List<Document> getDocuments() {
		if (null == documents) {
			documents = new ArrayList<Document>();
		}
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public List<DocumentSimilarity> getSimilarities() {
		if (null == similarities) {
			similarities = new ArrayList<DocumentSimilarity>();
		}
		return similarities;
	}

	public void setSimilarities(List<DocumentSimilarity> similarities) {
		this.similarities = similarities;
	}

}
