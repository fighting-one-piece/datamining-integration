package org.project.modules.classifier.regression.builder;

import org.project.modules.classifier.regression.data.DataResult;
import org.project.modules.classifier.regression.data.DataSet;

public class AloneGradientAscentBuilder extends AbstractBuilder {
	
	private int iteratorNum = 500;
	
	private double[][] datas = null;
	
	private double[][] tdatas = null;
	
	private double[] categories = null;
	
	private double[] weights = null;
	
	private double[] errors = null;
	
	@Override
	public DataSet initDataSet() {
		return null;
	}
	
	public void initData() {
		datas = new double[10][2];
		datas[0] = new double[]{0.423363, 11.054677};
		datas[1] = new double[]{0.406704, 7.067335};
		datas[2] = new double[]{0.667394, 12.741452};
		datas[3] = new double[]{0.569411, 9.548755};
		datas[4] = new double[]{0.850433, 6.920334};
		datas[5] = new double[]{1.347183, 13.175500};
		datas[6] = new double[]{1.176813, 3.167020};
		datas[7] = new double[]{0.931635, 1.589505};
		datas[8] = new double[]{1.014459, 5.754399};
		datas[9] = new double[]{1.985298, 3.230619};
		tdatas = new double[2][2];
		tdatas[0] = new double[]{1.388610, 9.341997}; 
		tdatas[1] = new double[]{0.126117, 0.922311}; 
		categories = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0};
		weights = new double[]{1.0, 1.0};
		errors = new double[10];
	}
	
	private void genWeights() {
		double alpha = 0.01;
		for (int i = 0; i < iteratorNum; i++) {
			for (int j = 0, len1 = datas.length; j < len1; j++) {
//				double z = 0.0;
//				for (int k = 0, len2 = datas[0].length; k < len2; k++) {
//					z += datas[j][k] * weights[k];
//				}
//				double h = sigmoid(z);
				double h = sigmoid(datas[j], weights);
				errors[j] = categories[j] - h;
			}
			for (int k = 0, len2 = datas[0].length; k < len2; k++) {
				for (int j = 0, len1 = datas.length; j < len1; j++) {
					weights[k] += alpha * datas[j][k] * errors[j];
				}
			}
			for (double weight : weights) {
				System.out.println("weight: " + weight);
			}
		}
	}
	
	public DataResult run() {
		initDataSet();
		genWeights();
		for (double[] tdata : tdatas) {
			System.out.println(classify(tdata, weights));
		}
		return null;
	}
	
	public static void main(String[] args) {
		new AloneGradientAscentBuilder().run();
	}

	@Override
	public double[] calculateWeights(DataSet dataSet) {
		return null;
	}
}
