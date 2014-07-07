package org.project.modules.classifier.regression.data;

public class Instance {

	private double feature1 = 0.0;
	
	private double feature2 = 0.0;
	
	private double category = 0;
	
	private double weight = 0.0;
	
	private double error = 0.0;

	public double getFeature1() {
		return feature1;
	}

	public void setFeature1(double feature1) {
		this.feature1 = feature1;
	}

	public double getFeature2() {
		return feature2;
	}

	public void setFeature2(double feature2) {
		this.feature2 = feature2;
	}

	public double getCategory() {
		return category;
	}

	public void setCategory(double category) {
		this.category = category;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}
	
	
}
