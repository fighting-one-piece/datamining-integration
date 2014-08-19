package org.project.modules.classifier.bayes.data;

import java.util.ArrayList;
import java.util.List;

import org.project.common.document.Document;

public class DataSet {

	/** 训练数据*/
	private List<Document> trainData = null;
	/** 测试数据*/
	private List<Document> testData = null;
	
	public List<Document> getTrainData() {
		if (null == trainData) {
			trainData = new ArrayList<Document>();
		}
		return trainData;
	}
	
	public void setTrainData(List<Document> trainData) {
		this.trainData = trainData;
	}
	
	public List<Document> getTestData() {
		if (null == testData) {
			testData = new ArrayList<Document>();
		}
		return testData;
	}
	
	public void setTestData(List<Document> testData) {
		this.testData = testData;
	}
	
	
	
}
