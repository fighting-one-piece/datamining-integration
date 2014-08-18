package org.project.modules.clustering.dbscan.data;

public class DataPoint {
	
	private int clusterId = 0;

	private double[] values = null;
	
	private boolean isAccessed = false;
	
	private String category = null;
	
	public DataPoint() {
		
	}
	
	public DataPoint(double[] values) {
		super();
		this.values = values;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
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

	public boolean equals(DataPoint o) {
		for (int i = 0, len = values.length; i < len; i++) {
			double[] ovalues = o.getValues();
			if (values[i] != ovalues[i]) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(category).append(" ");
		for (double value : values) {
			sb.append(value).append(" ");
		}
		return sb.toString();
	}
}
