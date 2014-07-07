package org.project.modules.classifier.regression.builder;

import org.project.modules.classifier.regression.data.DataSet;
import org.project.modules.classifier.regression.data.DataSetHandler;

//梯度上升算法
public class GradientAscentBuilder extends AbstractBuilder {
	
	private int iteratorNum = 500;
	
	public DataSet initialize() {
		return DataSetHandler.load("d:\\regression.txt");
	}
	
	public double[] genWeights(DataSet dataSet) {
		double[][] datas = dataSet.obtainDatas();
		double[] categories = dataSet.obtainCategories();
		double[] errors = new double[datas.length];
		double[] weights = new double[]{1.0, 1.0};
		double alpha = 0.001;
		for (int i = 0; i < iteratorNum; i++) {
			for (int j = 0, len1 = datas.length; j < len1; j++) {
				double h = sigmoid(datas[j], weights);
				errors[j] = categories[j] - h;
			}
			for (int k = 0, len2 = datas[0].length; k < len2; k++) {
				for (int j = 0, len1 = datas.length; j < len1; j++) {
					weights[k] += alpha * errors[j] * datas[j][k];
				}
			}
			show(weights);
		}
		return weights;
	}
	
	public static void main(String[] args) {
		GradientAscentBuilder builder = new GradientAscentBuilder();
		builder.build();
	}
}
