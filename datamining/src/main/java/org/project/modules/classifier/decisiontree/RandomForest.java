package org.project.modules.classifier.decisiontree;

import java.util.List;

import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.classifier.decisiontree.builder.ForestBuilder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.node.ForestNode;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.utils.ShowUtils;

public class RandomForest {
	
	private String trainFilePath = null;
	
	private String testFilePath = null;
	
	private Builder treeBuilder = null;

	private int treeNum = 0;
	
	private int attributeNum = 0;
	
	public RandomForest() {
		
	}
	
	public RandomForest(String trainFilePath, String testFilePath, 
			Builder treeBuilder, int treeNum, int attributeNum) {
		this.trainFilePath = trainFilePath;
		this.testFilePath = testFilePath;
		this.treeBuilder = treeBuilder;
		this.treeNum = treeNum;
		this.attributeNum = attributeNum;
	}
	
	public int getRandomNum() {
		return treeNum;
	}

	public void setRandomNum(int randomNum) {
		this.treeNum = randomNum;
	}

	public String getTrainFilePath() {
		return trainFilePath;
	}

	public void setTrainFilePath(String trainFilePath) {
		this.trainFilePath = trainFilePath;
	}

	public String getTestFilePath() {
		return testFilePath;
	}

	public void setTestFilePath(String testFilePath) {
		this.testFilePath = testFilePath;
	}

	public Builder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(Builder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	@SuppressWarnings("unchecked")
	public Object[] run() {
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		System.out.println("data attributes len: " + data.getAttributes().length);
		Builder forestBuilder = new ForestBuilder(treeNum, treeBuilder, attributeNum);
		List<TreeNode> treeNodes = (List<TreeNode>) forestBuilder.build(data);
		Data testData = DataLoader.loadNoId(testFilePath);
		System.out.println("testData attributes len: " + testData.getAttributes().length);
		DataHandler.fill(testData.getInstances(), data.getAttributes() , 0);
		ForestNode forestNode = new ForestNode(treeNodes);
		Object[] results = (Object[]) forestNode.classify(testData);
		ShowUtils.printToConsole(results);
		return results;
	}

	public static void main(String[] args) {
		int treeNum = 10;
		int attributeNum = 1000;
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_10.txt";
		String testFilePath = "d:\\trainset_extract_1.txt";
		RandomForest randomForest = new RandomForest(trainFilePath,
				testFilePath, treeBuilder, treeNum, attributeNum);
		randomForest.run();
	}
}
