package org.project.modules.clustering.kmeans.data;

import java.util.Map;

public class DataPoint {

	private String category = null;

	private Map<String, Double> values = null;
	
	public DataPoint() {
	}
	
	public DataPoint(Map<String, Double> values) {
		this.values = values;
	}

	public Map<String, Double> getValues() {
		return values;
	}

	public void setValues(Map<String, Double> values) {
		this.values = values;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
}
