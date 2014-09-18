package org.project.modules.classifier.adaboost.builder;

import java.util.ArrayList;
import java.util.List;

import org.project.modules.classifier.adaboost.data.TrainResult;

/**
 * 弱分类器
 */
public class WeakClassifier {
	
	public static final int NUMBER = 100;
	
	/**
	 * 训练数据集
	 * @param datas
	 * @param dataWeights
	 * @return 训练结果
	 */
	public static TrainResult train(List<double[]> datas, List<Double> dataWeights) {
		TrainResult trainResult = new TrainResult();
		List<Double> featureWeight = perceptron(datas, dataWeights);
		trainResult.setFeatureWeight(featureWeight);
		int dimension = datas.get(0).length;
		int right = 0, error = 0;
		for (int i = 0, len = datas.size(); i < len; i++) {
			double result = 0;
			for (int j = 0; j < dimension - 1; j++) {
				result += datas.get(i)[j] * featureWeight.get(j);
			}
			result += featureWeight.get(dimension - 1);
			if (result >= 0) {
				right++;
				trainResult.getClassifyResult().add(1d);
			} else {
				error++;
				trainResult.getClassifyResult().add(-1d);
			}
		}
		System.out.println("total: " + datas.size() + " right: " + right);
		double errorRate = new Double(error) / datas.size();
		double alpha = Math.log((1 - errorRate) / errorRate);
		trainResult.setClassifierWeight(alpha);
		return trainResult;
	}
	
	/**
	 * 传感器算法迭代计算特征权重
	 * @param datas
	 * @param dataWeights
	 * @return
	 */
	public static List<Double> perceptron(List<double[]> datas, List<Double> dataWeights) {
		int dimension = datas.get(0).length;
		List<Double> featureWeights = new ArrayList<Double>();
		for (int i = 0; i < dimension; i++) {
			featureWeights.add(1d);
		}
		for (int n = 0; n < NUMBER; n++) {
			for (int i = 0, len = datas.size(); i < len; i++) {
				double result = 0;
				for (int j = 0; j < dimension; j++) {
					result += datas.get(i)[j] * featureWeights.get(j);
				}
				if (result <= 0) {
 					for (int k = 0; k < dimension; k++) {
						double newWeight = featureWeights.get(k) + datas.get(i)[k] * dataWeights.get(i);
						featureWeights.set(k, newWeight);
					}
				} 
			}
		}
		return featureWeights;
	}
	
}
