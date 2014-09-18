package org.project.modules.classifier.regression.builder;

import java.net.URISyntaxException;
import java.util.List;

import org.project.modules.classifier.regression.data.DataResult;
import org.project.modules.classifier.regression.data.DataSet;
import org.project.modules.classifier.regression.data.DataSetHandler;

public abstract class AbstractBuilder {

	//Sigmoid函数计算
	protected double sigmoid(double value) {
		 return 1d / (1d + Math.exp(-value));  
	}
	
	//Sigmoid函数计算
	protected double sigmoid(double[] data, double[] weights) {
		double z = 0.0;
		for (int i = 0, len = data.length; i < len; i++) {
			z += data[i] * weights[i];
		}
		return sigmoid(z);
	}
	
	//分类预测数据
	protected int classify(double[] data, double[] weights) {
		double predict = sigmoid(data, weights);
		return predict > 0.5 ? 1 : 0;
	}
	
	//初始化数据集
	protected DataSet initDataSet() {
		DataSet dataSet = null;
		try {
			String path = AbstractBuilder.class.getClassLoader().getResource("trainset/regression.txt").toURI().getPath();
			dataSet = DataSetHandler.load(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataSet;
	}
	
	//初始化数据集
	protected DataSet initDataSet(String path) {
		return DataSetHandler.load(path);
	}
	
	//计算回归系数
	public abstract double[] calculateWeights(DataSet dataSet);
	
	public DataResult run() {
		return run(null);
	}
	
	public DataResult run(String path) {
		DataSet dataSet = null == path ? initDataSet() : initDataSet(path);
		double[] weights = calculateWeights(dataSet);
		List<Integer> categories = dataSet.getCategories();
		int index = 0;
		DataResult dataResult = new DataResult();
		for (double[] data : dataSet.getDatas()) {
			dataResult.getDatas().add(data);
			dataResult.getActualValues().add(categories.get(index++));
			dataResult.getPredictedValues().add(classify(data, weights));
		}
		dataResult.statistics();
		return dataResult;
	}
	
	public DataResult runByData(DataSet dataSet) {
		double[] weights = calculateWeights(dataSet);
		List<Integer> categories = dataSet.getCategories();
		int index = 0;
		DataResult dataResult = new DataResult();
		for (double[] data : dataSet.getDatas()) {
			dataResult.getDatas().add(data);
			dataResult.getActualValues().add(categories.get(index++));
			dataResult.getPredictedValues().add(classify(data, weights));
		}
		dataResult.statistics();
		return dataResult;
	}
	
}
