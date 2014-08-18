package org.project.modules.clustering.dbscan.data;

import java.util.Map;

public class DataPoint {
	
	private int clusterId = 0;

	private String category = null;

	private boolean isAccessed = false;
	
	private Map<String, Double> values = null;
	
	public DataPoint() {
		
	}
	
	public DataPoint(Map<String, Double> values) {
		super();
		this.values = values;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public Map<String, Double> getValues() {
		return values;
	}

	public void setValues(Map<String, Double> values) {
		this.values = values;
	}

	public boolean isAccessed() {
		return isAccessed;
	}

	public void setAccessed(boolean isAccessed) {
		this.isAccessed = isAccessed;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
