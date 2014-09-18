package org.project.modules.classifier.adaboost.builder;

import java.util.ArrayList;
import java.util.List;

import org.project.modules.classifier.adaboost.data.TrainResult;

/**
 * Adaboost分类器 
 */
public class AdaboostClassifier {

	public static final int CLASSIFIER_NUM = 40;
	
	/**
	 * 训练数据集，生成若干个弱分类器的训练结果
	 * @param datas
	 * @return
	 */
	public static List<TrainResult> train(List<double[]> datas) {
		List<Double> dataWeights = new ArrayList<Double>();
		for (int i = 0, len = datas.size(); i < len; i++) {
			dataWeights.add(1.0 / len);
		}
		List<TrainResult> trainResults = new ArrayList<TrainResult>();
		for (int n = 0; n < CLASSIFIER_NUM; n++) {
			TrainResult trainResult = WeakClassifier.train(datas, dataWeights);
			trainResults.add(trainResult);
			List<Double> classifyResult = trainResult.getClassifyResult();
			double sum = 0, alpha = trainResult.getClassifierWeight();
			for (int i = 0, len = datas.size(); i < len; i++) {
				double newWeight = dataWeights.get(i) * Math.pow(Math.E, (-1) * alpha * classifyResult.get(i));
				dataWeights.set(i, newWeight);
				sum += newWeight;
			}
			for (int i = 0, len = datas.size(); i < len; i++) {
				dataWeights.set(i, dataWeights.get(i) / sum);
			}
		}
		return trainResults;
	}
	
	public static List<TrainResult> train(List<double[]> datas, List<Double> categories) {
		return null;
	}
	
	/**
	 * 根据训练结果判断数据分类
	 * @param data
	 * @param trainResults
	 * @return
	 */
	public static double classify(double[] data, List<TrainResult> trainResults) {
		double result = 0;
		for (TrainResult trainResult : trainResults) {
			double classifierWeight = trainResult.getClassifierWeight();
			List<Double> featureWeight = trainResult.getFeatureWeight();
			for (int i = 0, len = data.length; i < len; i++) {
				result += data[i] * classifierWeight * featureWeight.get(i);
			}
			result += featureWeight.get(data.length);
		}
		return result > 0 ? 1 : -1;
	}
	
	/**
	 * 根据训练结果判断数据集分类
	 * @param datas
	 * @param trainResults
	 * @return
	 */
	public static List<Double> classify(List<double[]> datas, List<TrainResult> trainResults) {
		List<Double> results = new ArrayList<Double>();
		for (int i = 0, len = datas.size(); i < len; i++) {
			results.add(classify(datas.get(i), trainResults));
		}
		return results;
	}
	
}
