package org.project.modules.classifier.regression.builder;

import org.project.modules.classifier.regression.data.DataSet;
import org.project.modules.classifier.regression.data.DataSetHandler;

//随机梯度上升算法
public class RandomGradientAscentBuilder extends AbstractBuilder {
	
	public DataSet initialize() {
		return DataSetHandler.load("d:\\regression.txt");
	}
	
	public double[] genWeights(DataSet dataSet) {
		double[][] datas = dataSet.obtainDatas();
		double[] categories = dataSet.obtainCategories();
		double[] weights = new double[]{1.0, 1.0};
		double alpha = 0.01;
		for (int i = 0, len = datas.length; i < len; i++) {
			double h = sigmoid(datas[i], weights);
			double error = categories[i] - h;
			for (int j = 0, len1 = weights.length; j < len1; j++) {
				weights[j] += alpha * error * datas[i][j]; 
			}
		}
		show(weights);
		return weights;
	}
	
	public static void main(String[] args) {
		RandomGradientAscentBuilder builder = new RandomGradientAscentBuilder();
		builder.build();
	}

}
