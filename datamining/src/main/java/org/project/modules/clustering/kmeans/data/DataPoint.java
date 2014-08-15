package org.project.modules.clustering.kmeans.data;

public class DataPoint {

	private double[] values = null;
	
	private String category = null;
	
	public DataPoint() {
	}
	
	public DataPoint(double[] values) {
		this.values = values;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
}
