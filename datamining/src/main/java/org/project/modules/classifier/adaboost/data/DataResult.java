package org.project.modules.classifier.adaboost.data;

import java.util.ArrayList;
import java.util.List;

public class DataResult {

	private List<double[]> datas = null;
	
	private List<Double> actualValues = null;
	
	private List<Double> predictedValues = null;
	
	private int rightNum = 0;

	private int errorNum = 0;
	
	public List<double[]> getDatas() {
		if (datas == null) {
			datas = new ArrayList<double[]>();
		}
		return datas;
	}

	public void setDatas(List<double[]> datas) {
		this.datas = datas;
	}

	public List<Double> getActualValues() {
		if (null == actualValues) {
			actualValues = new ArrayList<Double>();
		}
		return actualValues;
	}

	public void setActualValues(List<Double> actualValues) {
		this.actualValues = actualValues;
	}

	public List<Double> getPredictedValues() {
		if (null == predictedValues) {
			predictedValues = new ArrayList<Double>();
		}
		return predictedValues;
	}

	public void setPredictedValues(List<Double> predictedValues) {
		this.predictedValues = predictedValues;
	}
	
	public int getRightNum() {
		return rightNum;
	}

	public void setRightNum(int rightNum) {
		this.rightNum = rightNum;
	}

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}
	
	public int getTotal() {
		return rightNum + errorNum;
	}

	public void statistics() {
		int rightNum = 0, errorNum = 0;
		for (int i = 0, len = actualValues.size(); i < len; i++) {
			double[] data = datas.get(i);
			for (double d : data) {
				System.out.print(d + "    ");
			}
			double actualValue = actualValues.get(i);
			double predictedValue = predictedValues.get(i);
			System.out.println(actualValue + "    " + predictedValue);
			if (actualValue == predictedValue) {
				rightNum++;
			} else {
				errorNum++;
			}
		}
		System.out.println("statistics right: " + rightNum + " error: " + errorNum);
		setRightNum(rightNum);
		setErrorNum(errorNum);
	}
	
}
