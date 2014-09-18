package org.project.modules.classifier.decisiontree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeSprintBuilder;
import org.project.modules.classifier.decisiontree.builder.TreeBuilder;
import org.project.modules.classifier.decisiontree.builder.TreeC45Builder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataError;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.DataSet;
import org.project.modules.classifier.decisiontree.node.BranchNode;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.ShowUtils;

public class DecisionTreeTest {
	
	@Test
	public void splitTreeNode() {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_10.txt";
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		System.out.println("data attrs: " + data.getAttributes().length);
		TreeNode treeNode = (TreeNode) treeBuilder.build(data);
		Set<TreeNode> treeNodes = new HashSet<TreeNode>();
		TreeNodeHelper.splitTreeNode(treeNode, 25, 0, treeNodes);
		System.out.println(treeNodes.size());
		List<Object[]> results = new ArrayList<Object[]>();
		Set<String> attributes = new HashSet<String>();
		for (TreeNode node : treeNodes) {
			TreeNodeHelper.obtainAttributes(node, attributes);
		}
		String testFilePath = "d:\\trainset_extract_1.txt";
		Data testData = DataLoader.loadNoId(testFilePath);
		DataHandler.fill(testData.getInstances(), data.getAttributes(), 0);
		for (TreeNode node : treeNodes) {
			Object[] result = (Object[]) node.classify(testData);
			ShowUtils.printToConsole(result);
			results.add(result);
		}
		ShowUtils.printToConsole(DataHandler.vote(results));
		System.out.println("tree attrs: " + attributes.size());
	}
	
	@Test
	public void buildWithC45() {
//		String path = "d:\\trains14_id.txt";
		String path = "d:\\trainset_extract_10_l.txt";
		Data data = DataLoader.loadWithId(path);
		DataHandler.fill(data, 0);
//		DataHandler.computeFill(data, 1.0);
		Builder builder = new DecisionTreeC45Builder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		TreeNodeHelper.print(treeNode, 0, null);
//		String p = "d:\\trains14_id.txt";
		String p = "d:\\trainset_extract_1_l.txt";
		Data testData = DataLoader.loadWithId(p);
		DataHandler.fill(testData.getInstances(), data.getAttributes(), 0);
//		DataHandler.computeFill(testData, data, 1.0);
		Object[] results = (Object[]) treeNode.classify(testData);
		ShowUtils.printToConsole(results);
	}
	
	@Test
	public void buildWithC45Serial() throws Exception {
		String path = "d:\\trains14_id.txt";
		Data data = DataLoader.loadWithId(path);
		TreeBuilder builder = new TreeC45Builder();
		BranchNode treeNode = (BranchNode) builder.build(data);
		ShowUtils.printToConsole(treeNode.getValues());
		TreeNodeHelper.print(treeNode, 0, null);
	}
	
	@Test
	public void buildWithC45Serialize() throws Exception {
		String path = "d:\\trains14_id.txt";
		Data data = DataLoader.loadWithId(path);
		DataHandler.fill(data, 0);
		Builder builder = new DecisionTreeC45Builder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		OutputStream out = new FileOutputStream(new File("d:\\z.txt"));
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(treeNode);
		IOUtils.closeQuietly(oos);
		IOUtils.closeQuietly(out);

		InputStream in = new FileInputStream(new File("d:\\z.txt"));
		ObjectInputStream ois = new ObjectInputStream(in);
		TreeNode node = (TreeNode) ois.readObject();
		IOUtils.closeQuietly(ois);
		IOUtils.closeQuietly(in);
		
		String p = "d:\\trains14_id.txt";
		Data testData = DataLoader.loadWithId(p);
		DataHandler.fill(testData.getInstances(), data.getAttributes(), 0);
		Object[] results = (Object[]) node.classify(testData);
		ShowUtils.printToConsole(results);
	}
	
	@Test
	public void buildWithSprint() {
//		String path = "d:\\trainset_extract_1.txt";
//		String path = "d:\\trainset_extract_10.txt";
		String path = "d:\\trainset_extract_10_l.txt";
//		String path = "d:\\trains14_id.txt";
//		String path = "d:\\attribute_100_r_10.txt";
//		String path = "d:\\attribute_1000_r_10.txt";
//		String path = "d:\\attribute_100_r_100.txt";
//		String path = "d:\\attribute_1000_r_100.txt";
//		String path = "d:\\attribute_4432_r_100.txt";
//		Data data = DataLoader.load(path);
		Data data = DataLoader.loadWithId(path);
		DataHandler.fill(data, 1.0);
		Builder builder = new DecisionTreeSprintBuilder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		
//		TreeNodeHelper.print(treeNode, 0, null);
//		String p = "d:\\trainset_extract_1.txt";
		String p = "d:\\trainset_extract_1_l.txt";
//		String p = "d:\\trainset_extract_10.txt";
//		String p = "d:\\trains14_id.txt";
//		String p = "d:\\attribute_10_r_10.txt";
//		String p = "d:\\attribute_100_r_10.txt";
//		String p = "d:\\attribute_500_r_10.txt";
//		String p = "d:\\attribute_700_r_10.txt";
//		String p = "d:\\attribute_1000_r_10.txt";
//		String p = "d:\\attribute_2000_r_100.txt";
//		Data testData = DataLoader.load(p);
		Data testData = DataLoader.loadWithId(p);
		System.out.println("data attributes:　" + data.getAttributes().length);
		System.out.println("testdata attributes:　" + testData.getAttributes().length);
		DataHandler.fill(testData.getInstances(), data.getAttributes(), 1.0);
		Object[] results = (Object[]) treeNode.classifySprint(testData);
		ShowUtils.printToConsole(results);
		DataError dataError = new DataError(testData.getCategories(), results);
		dataError.report();
	}
	
	@Test
	public void buildWithSprintAndComputeFill() {
//		String path = "d:\\attribute_1000_r_10.txt";
		String path = "d:\\trainset_extract_10_l.txt";
//		String path = "d:\\trains14_id.txt";
//		Data data = DataLoader.load(path);
		Data data = DataLoader.loadWithId(path);
		System.out.println("data attributes:　" + data.getAttributes().length);
//		DataHandler.fill(data, 1.0);
		DataHandler.computeFill(data, 1.0);
		Builder builder = new DecisionTreeSprintBuilder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		TreeNodeHelper.print(treeNode, 0, null);
//		String p = "d:\\attribute_500_r_10.txt";
		String p = "d:\\trainset_extract_1_l.txt";
//		String p = "d:\\trains14_id.txt";
//		Data testData = DataLoader.load(p);
		Data testData = DataLoader.loadWithId(p);
		System.out.println("testdata attributes:　" + testData.getAttributes().length);
//		DataHandler.fill(testData.getInstances(), data.getAttributes(), 1.0);
		DataHandler.computeFill(testData, data, 1.0);
		Object[] results = (Object[]) treeNode.classifySprint(testData);
		ShowUtils.printToConsole(results);
		DataError dataError = new DataError(testData.getCategories(), results);
		dataError.report();
	}
	
	@Test
	public void buildWithSprintAndComputeFill1() {
		String path = "d:\\trainset_extract_10.txt";
		Data data = DataLoader.loadNoId(path);
		System.out.println("data attributes:　" + data.getAttributes().length);
//		DataHandler.fill(data, 1.0);
		DataHandler.computeFill(data, 1.0);
		Builder builder = new DecisionTreeSprintBuilder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		TreeNodeHelper.print(treeNode, 0, null);
		Object[] results = (Object[]) treeNode.classifySprint(data);
		ShowUtils.printToConsole(results);
		DataError dataError = new DataError(data.getCategories(), results);
		dataError.report();
	}
	
	@Test
	public void pruningREP() {
		String path = "d:\\trainset_1000.txt";
		Data data = DataLoader.loadNoId(path);
		DataSet dataSet = DataHandler.split(data, 3, 2, 1);
		Data trainData = dataSet.getTrainData();
		Data testData = dataSet.getTestData();
		
//		DataHandler.fill(trainData, 1.0);
		DataHandler.computeFill(trainData, 1.0);
		Builder builder = new DecisionTreeSprintBuilder();
		TreeNode treeNode = (TreeNode) builder.build(trainData);
		TreeNodeHelper.print(treeNode, 0, null);
		
//		treeNode.classifySprint(trainData);
//		TreeNodeHelper.print(treeNode, 0, null);
		
		decision(trainData, testData, treeNode);
		
//		TreeNodeHelper.pruningTreeNode(treeNode, trainData.obtainMaxCategory());
//		TreeNodeHelper.print(treeNode, 0, null);
//		
//		DataError dataError = decision(trainData, testData, treeNode);
	}
	
	private DataError decision(Data trainData, Data testData, TreeNode treeNode) {
//		DataHandler.fill(testData.getInstances(), trainData.getAttributes(), 1.0);
		DataHandler.computeFill(testData, trainData, 1.0);
		Object[] results = (Object[]) treeNode.classifySprint(testData);
		ShowUtils.printToConsole(results);
		DataError dataError = new DataError(testData.getCategories(), results);
		dataError.report();
		return dataError;
	}
}
