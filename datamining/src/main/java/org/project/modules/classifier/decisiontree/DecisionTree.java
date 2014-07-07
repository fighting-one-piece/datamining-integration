package org.project.modules.classifier.decisiontree;

import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.utils.ShowUtils;

public class DecisionTree {
	
	private String trainFilePath = null;
	
	private String testFilePath = null;
	
	private Builder treeBuilder = null;
	
	public DecisionTree() {
		
	}
	
	public DecisionTree(String trainFilePath, String testFilePath, Builder treeBuilder) {
		this.trainFilePath = trainFilePath;
		this.testFilePath = testFilePath;
		this.treeBuilder = treeBuilder;
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

	public void run() {
		Data data = DataLoader.load(trainFilePath);
		DataHandler.fill(data, 0);
		TreeNode treeNode = (TreeNode) treeBuilder.build(data);
		Data testData = DataLoader.load(testFilePath);
		DataHandler.fill(testData.getInstances(), data.getAttributes() , 0);
		Object[] results = (Object[]) treeNode.classify(testData);
		ShowUtils.print(results);
	}

	public static void main(String[] args) {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_10.txt";
		String testFilePath = "d:\\trainset_extract_1.txt";
		DecisionTree decisionTree = new DecisionTree(trainFilePath, 
				testFilePath, treeBuilder);
		decisionTree.run();
	}
}
