package org.project.modules.classifier.adaboost.data;

import java.util.ArrayList;
import java.util.List;

public class TrainResult {
	
	private List<Double> featureWeight = null;
	
	private double classifierWeight = 0.0;
	
	private List<Double> classifyResult = null;

	private List<List<Double>> featureWeights = null;
	
	private List<Double> classifierWeights = null;

	public List<List<Double>> getFeatureWeights() {
		if (null == featureWeights) {
			featureWeights = new ArrayList<List<Double>>();
		}
		return featureWeights;
	}

	public void setFeatureWeights(List<List<Double>> featureWeights) {
		this.featureWeights = featureWeights;
	}

	public List<Double> getClassifierWeights() {
		if (null == classifierWeights) {
			classifierWeights = new ArrayList<Double>();
		}
		return classifierWeights;
	}

	public void setClassifierWeights(List<Double> classifierWeights) {
		this.classifierWeights = classifierWeights;
	}

	public List<Double> getFeatureWeight() {
		if (null == featureWeight) {
			featureWeight = new ArrayList<Double>();
		}
		return featureWeight;
	}

	public void setFeatureWeight(List<Double> featureWeight) {
		this.featureWeight = featureWeight;
	}

	public double getClassifierWeight() {
		return classifierWeight;
	}

	public void setClassifierWeight(double classifierWeight) {
		this.classifierWeight = classifierWeight;
	}

	public List<Double> getClassifyResult() {
		if (null == classifyResult) {
			classifyResult = new ArrayList<Double>();
		}
		return classifyResult;
	}

	public void setClassifyResult(List<Double> classifyResult) {
		this.classifyResult = classifyResult;
	}
	
	
}
