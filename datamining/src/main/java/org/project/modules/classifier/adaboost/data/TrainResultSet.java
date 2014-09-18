package org.project.modules.classifier.adaboost.data;

import java.util.ArrayList;
import java.util.List;

public class TrainResultSet {
	
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
	
	public void show() {
		for (int i = 0, len = classifierWeights.size(); i < len; i++) {
			System.out.print(classifierWeights.get(i) + "\t");
			for (double value : featureWeights.get(i)) {
				System.out.print(value + "\t");
			}
			System.out.println();
		}
	}
	
}
