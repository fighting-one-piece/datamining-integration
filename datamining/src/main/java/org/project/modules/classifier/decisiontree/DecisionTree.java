package org.project.modules.classifier.decisiontree;

import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.utils.ShowUtils;

public class DecisionTree {
	
	private String trainSetPath = null;
	
	private String testSetPath = null;
	
	private Builder treeBuilder = null;
	
	private String[] attributes = null;
	
	private TreeNode treeNode = null;
	
	public DecisionTree() {
		
	}
	
	public DecisionTree(String trainSetPath, String testSetPath, Builder treeBuilder) {
		this.trainSetPath = trainSetPath;
		this.testSetPath = testSetPath;
		this.treeBuilder = treeBuilder;
	}
	
	public String getTrainSetPath() {
		return trainSetPath;
	}

	public void setTrainSetPath(String trainSetPath) {
		this.trainSetPath = trainSetPath;
	}

	public String getTestSetPath() {
		return testSetPath;
	}

	public void setTestSetPath(String testFilePath) {
		this.testSetPath = testFilePath;
	}

	public Builder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(Builder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}
	
	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}

	public TreeNode getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(TreeNode treeNode) {
		this.treeNode = treeNode;
	}

	public void buildTreeNode() {
		Data data = DataLoader.loadWithId(trainSetPath);
		setAttributes(data.getAttributes());
		TreeNode treeNode = (TreeNode) treeBuilder.build(data);
		setTreeNode(treeNode);
	}
	
	public void buildTreeNode(Data data) {
		setAttributes(data.getAttributes());
		TreeNode treeNode = (TreeNode) treeBuilder.build(data);
		setTreeNode(treeNode);
	}
	
	public Object[] classify() {
		Data data = DataLoader.loadNoId(testSetPath);
		Object[] results = (Object[]) treeNode.classify(data);
		return results;
	}
	
	public Object[] classify(Data data) {
		Object[] results = (Object[]) treeNode.classify(data);
		return results;
	}

	public Object[] run() {
		if (null == trainSetPath) {
			throw new RuntimeException("训练集路径为空");
		}
		if (null == testSetPath) {
			throw new RuntimeException("测试集路径为空");
		}
		if (null == treeBuilder) {
			throw new RuntimeException("构造器为空");
		}
		buildTreeNode();
		return classify();
	}

	public static void main(String[] args) throws Exception {
		String trainSetPath = DecisionTree.class.getClassLoader().getResource("trainset/decisiontree.txt").toURI().getPath();
		String testSetPath = DecisionTree.class.getClassLoader().getResource("testset/decisiontree.txt").toURI().getPath();
		Builder treeBuilder = new DecisionTreeC45Builder();
		DecisionTree decisionTree = new DecisionTree(trainSetPath, testSetPath, treeBuilder);
		ShowUtils.printToConsole(decisionTree.run());
	}
	
}
