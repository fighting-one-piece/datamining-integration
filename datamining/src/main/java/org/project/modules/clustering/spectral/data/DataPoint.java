package org.project.modules.clustering.spectral.data;

public class DataPoint {

	private String category = null;
	
	private double[] values = null;

	public DataPoint() {
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
