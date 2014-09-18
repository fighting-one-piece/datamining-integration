package org.project.modules.classifier.adaboost.builder;

import java.util.ArrayList;
import java.util.List;

public class LRClassifier {

	public static final int ITER_NUM = 100;

	public static List<Double> train(List<double[]> datas, List<Double> categories) {
		int dimension = datas.get(0).length;
		List<Double> featureWeights = new ArrayList<Double>();
		for (int i = 0; i < dimension; i++) {
			featureWeights.add(1d);
		}
		double[] errors = new double[datas.size()];
		double alpha = 0.001;
		for (int i = 0; i < ITER_NUM; i++) {
			for (int j = 0, len1 = datas.size(); j < len1; j++) {
				double h = sigmoid(datas.get(j), featureWeights);
				errors[j] = categories.get(j) - h;
			}
			for (int k = 0; k < dimension; k++) {
				for (int j = 0, len1 = datas.size(); j < len1; j++) {
					double value = featureWeights.get(k) + alpha * errors[j] * datas.get(j)[k];
					featureWeights.set(k, value);
				}
			}
		}
		return featureWeights;
	}
	
	private static double sigmoid(double value) {
		 return 1d / (1d + Math.exp(-value));  
	}
	
	private static double sigmoid(double[] data, List<Double> weights) {
		double z = 0.0;
		for (int i = 0, len = data.length; i < len; i++) {
			z += data[i] * weights.get(i);
		}
		return sigmoid(z);
	}
	
}
