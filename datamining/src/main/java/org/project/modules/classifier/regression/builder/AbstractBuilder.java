package org.project.modules.classifier.regression.builder;

import org.project.modules.classifier.regression.data.DataSet;

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
	protected double classify(double[] data, double[] weights) {
		double predict = sigmoid(data, weights);
		return predict > 0.5 ? 1.0 : 0.0;
	}
	
	protected void show(double[] values) {
		for(double value : values) {
			System.out.println("value: " + value);
		}
	}
	
	//初始化数据集
	public abstract DataSet initDataSet();
	
	//生成回归系数
	public abstract double[] genWeights(DataSet dataSet);
	
	public void build() {
		DataSet dataSet = initDataSet();
		double[] weights = genWeights(dataSet);
		double[] categories = dataSet.obtainCategories();
		int index = 0, error = 0, right = 0;
		for (double[] data : dataSet.obtainDatas()) {
			double prediction = classify(data, weights);
			double category = categories[index++];
			System.out.print(" category: " + category);
			System.out.println(" prediction: " + prediction);
			if (category != prediction) {
				error += 1;
			} else {
				right += 1;
			}
		}
		System.out.println("error: " + error + " right: " + right);
	}
}
