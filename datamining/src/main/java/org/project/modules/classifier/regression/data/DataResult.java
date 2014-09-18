package org.project.modules.classifier.regression.data;

import java.util.ArrayList;
import java.util.List;

public class DataResult {

	private List<double[]> datas = null;
	
	private List<Integer> actualValues = null;
	
	private List<Integer> predictedValues = null;
	
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

	public List<Integer> getActualValues() {
		if (null == actualValues) {
			actualValues = new ArrayList<Integer>();
		}
		return actualValues;
	}

	public void setActualValues(List<Integer> actualValues) {
		this.actualValues = actualValues;
	}

	public List<Integer> getPredictedValues() {
		if (null == predictedValues) {
			predictedValues = new ArrayList<Integer>();
		}
		return predictedValues;
	}

	public void setPredictedValues(List<Integer> predictedValues) {
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
			int actualValue = actualValues.get(i);
			int predictedValue = predictedValues.get(i);
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
