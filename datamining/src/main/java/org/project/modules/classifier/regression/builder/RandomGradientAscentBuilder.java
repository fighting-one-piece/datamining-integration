package org.project.modules.classifier.regression.builder;

import java.util.List;

import org.project.modules.classifier.regression.data.DataSet;

//随机梯度上升算法
public class RandomGradientAscentBuilder extends AbstractBuilder {
	
	public double[] calculateWeights(DataSet dataSet) {
		double[][] datas = dataSet.getDatas();
		List<Integer> categories = dataSet.getCategories();
		double[] weights = new double[]{1.0, 1.0};
		double alpha = 0.01;
		for (int i = 0, len = datas.length; i < len; i++) {
			double h = sigmoid(datas[i], weights);
			double error = categories.get(i) - h;
			for (int j = 0, len1 = weights.length; j < len1; j++) {
				weights[j] += alpha * error * datas[i][j]; 
			}
		}
		return weights;
	}
	
	public static void main(String[] args) {
		new RandomGradientAscentBuilder().run();
	}

}
