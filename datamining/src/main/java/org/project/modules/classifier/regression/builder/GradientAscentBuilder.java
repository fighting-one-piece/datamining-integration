package org.project.modules.classifier.regression.builder;

import java.util.List;

import org.project.modules.classifier.regression.data.DataSet;

//梯度上升算法
public class GradientAscentBuilder extends AbstractBuilder {
	
	private int ITER_NUM = 100;
	
	public double[] calculateWeights(DataSet dataSet) {
		double[][] datas = dataSet.getDatas();
		List<Integer> categories = dataSet.getCategories();
		double[] errors = new double[datas.length];
		double[] weights = new double[]{1.0, 1.0};
		double alpha = 0.001;
		for (int i = 0; i < ITER_NUM; i++) {
			for (int j = 0, len1 = datas.length; j < len1; j++) {
				double h = sigmoid(datas[j], weights);
				errors[j] = categories.get(j) - h;
			}
			for (int k = 0, len2 = datas[0].length; k < len2; k++) {
				for (int j = 0, len1 = datas.length; j < len1; j++) {
					weights[k] += alpha * errors[j] * datas[j][k];
				}
			}
		}
		return weights;
	}
	
	public static void main(String[] args) {
		new GradientAscentBuilder().run();
	}
	
}
