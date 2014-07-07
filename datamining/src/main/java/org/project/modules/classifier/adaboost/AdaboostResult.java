package org.project.modules.classifier.adaboost;

import java.util.ArrayList;

/** adaboost算法的结果类，包括弱分类器的集合和每个弱分类器的权重*/
public class AdaboostResult {

	private ArrayList<ArrayList<Double>> weakClassifierSet;
	private ArrayList<Double> classifierWeightSet;

	public ArrayList<ArrayList<Double>> getWeakClassifierSet() {
		return weakClassifierSet;
	}

	public void setWeakClassifierSet(
			ArrayList<ArrayList<Double>> weakClassifierSet) {
		this.weakClassifierSet = weakClassifierSet;
	}

	public ArrayList<Double> getClassifierWeightSet() {
		return classifierWeightSet;
	}

	public void setClassifierWeightSet(ArrayList<Double> classifierWeightSet) {
		this.classifierWeightSet = classifierWeightSet;
	}

}
