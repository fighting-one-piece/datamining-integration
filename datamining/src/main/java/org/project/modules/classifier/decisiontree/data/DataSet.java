package org.project.modules.classifier.decisiontree.data;

public class DataSet {

	private Data trainData = null;
	
	private Data testData = null;
	
	public DataSet() {
		
	}
	
	public DataSet(Data trainData, Data testData) {
		super();
		this.trainData = trainData;
		this.testData = testData;
	}

	public Data getTrainData() {
		return trainData;
	}

	public void setTrainData(Data trainData) {
		this.trainData = trainData;
	}

	public Data getTestData() {
		return testData;
	}

	public void setTestData(Data testData) {
		this.testData = testData;
	}
	
	
}
