package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ReflectionUtils;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataError;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.DataSplit;
import org.project.modules.classifier.decisiontree.data.DataSplitItem;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.mr.AbstractJob;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGiniWritable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.FileUtils;
import org.project.utils.HDFSUtils;
import org.project.utils.ShowUtils;

public class DecisionTreeSprintSJob extends AbstractJob {
	
	private Map<String, Set<String>> attrName2Values = null;
	
	/**
	 * 对数据集做预处理
	 * @param trainData
	 * @return
	 */
	public String prepare(Data trainData) {
		String path = FileUtils.obtainTmpTxtPath();
		DataHandler.writeData(path, trainData);
		System.out.println(path);
		String name = path.substring(path.lastIndexOf(File.separator) + 1);
		String hdfsPath = HDFSUtils.HDFS_TEMP_INPUT_URL + name;
		HDFSUtils.copyFromLocalFile(conf, path, hdfsPath);
		attrName2Values = new HashMap<String, Set<String>>();
		for (Instance instance : trainData.getInstances()) {
			for (Map.Entry<String, Object> entry : 
				instance.getAttributes().entrySet()) {
				String attrName = entry.getKey();
				Set<String> values = attrName2Values.get(attrName);
				if (null == values) {
					values = new HashSet<String>();
					attrName2Values.put(attrName, values);
				}
				values.add(String.valueOf(entry.getValue()));
			}
		}
		return hdfsPath;
	}
	
	/**
	 * 选择最佳属性
	 * @param output
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public AttributeGiniWritable chooseBestAttribute(String output) {
		AttributeGiniWritable minSplitAttribute = null;
		Path path = new Path(output);
		try {
			FileSystem fs = path.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, path);
			ShowUtils.printToConsole(paths);
			double minSplitPointGini = 1.0;
			SequenceFile.Reader reader = null;
			for (Path p : paths) {
				reader = new SequenceFile.Reader(fs, p, conf);
				Text key = (Text) ReflectionUtils.newInstance(
						reader.getKeyClass(), conf);
				AttributeGiniWritable value = new AttributeGiniWritable();
				while (reader.next(key, value)) {
					double gini = value.getGini();
					if (value.isCategory()) {
						System.out.println("attr: " + value.getAttribute());
						System.out.println("gini: " + gini);
					}
					if (gini <= minSplitPointGini) {
						minSplitPointGini = gini;
						minSplitAttribute = value;
					}
					value = new AttributeGiniWritable();
				}
				IOUtils.closeQuietly(reader);
			}
			System.out.println("output: " + path.toString());
			HDFSUtils.delete(conf, path);
			System.out.println("hdfs delete file : " + path.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return minSplitAttribute;
	}
	
	/**
	 * 构造决策树
	 * @param input
	 * @return
	 */
	public Object build(String input, Data trainData) {
		Object preHandleResult = preHandle(trainData);
		if (null != preHandleResult) return preHandleResult;
		String output = HDFSUtils.HDFS_TEMP_OUTPUT_URL;
		HDFSUtils.delete(conf, new Path(output));
		System.out.println("delete output path : " + output);
		String[] args = new String[]{input, output};
		CalculateSprintGiniMR.main(args);
		
		AttributeGiniWritable bestAttr = chooseBestAttribute(output);
		String attribute = bestAttr.getAttribute();
		System.out.println("best attribute: " + attribute);
		System.out.println("isCategory: " + bestAttr.isCategory());
		if (bestAttr.isCategory()) {
			return attribute;
		}
		TreeNode treeNode = new TreeNode(attribute);
		String splitPoint = bestAttr.getSplitPoint();
		String[] attributes = trainData.getAttributesExcept(attribute);
		Set<String> attributeValues = attrName2Values.get(attribute);
		attributeValues.remove(splitPoint);
		StringBuilder sb = new StringBuilder();
		for (String attributeValue : attributeValues) {
			sb.append(attributeValue).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		String[] names = new String[]{splitPoint, sb.toString()};
		
		DataSplit dataSplit = DataHandler.split(new Data(
				trainData.getInstances(), attribute, names));
		for (DataSplitItem item : dataSplit.getItems()) {
			String path = item.getPath();
			String name = path.substring(path.lastIndexOf(File.separator) + 1);
			String hdfsPath = HDFSUtils.HDFS_TEMP_INPUT_URL + name;
			HDFSUtils.copyFromLocalFile(conf, path, hdfsPath);
			treeNode.setChild(item.getSplitPoint(), build(hdfsPath, 
					new Data(attributes, item.getInstances())));
		}
		return treeNode;
	}
	
	private void classify(TreeNode treeNode, String trainSet, String testSet, String output) {
		OutputStream out = null;
		BufferedWriter writer = null;
		try {
			Path trainSetPath = new Path(trainSet);
			FileSystem trainFS = trainSetPath.getFileSystem(conf);
			Path[] trainHdfsPaths = HDFSUtils.getPathFiles(trainFS, trainSetPath);
			FSDataInputStream trainFSInputStream = trainFS.open(trainHdfsPaths[0]);
			Data trainData = DataLoader.load(trainFSInputStream, true);
			
			Path testSetPath = new Path(testSet);
			FileSystem testFS = testSetPath.getFileSystem(conf);
			Path[] testHdfsPaths = HDFSUtils.getPathFiles(testFS, testSetPath);
			FSDataInputStream fsInputStream = testFS.open(testHdfsPaths[0]);
			Data testData = DataLoader.load(fsInputStream, true);
			
//			DataHandler.fill(testData.getInstances(), data.getAttributes(), 1.0);
			DataHandler.computeFill(testData, trainData, 1.0);
			Object[] results = (Object[]) treeNode.classifySprint(testData);
			ShowUtils.printToConsole(results);
			DataError dataError = new DataError(testData.getCategories(), results);
			dataError.report();
			String path = FileUtils.obtainTmpTxtPath();
			out = new FileOutputStream(new File(path));
			writer = new BufferedWriter(new OutputStreamWriter(out));
			StringBuilder sb = null;
			for (int i = 0, len = results.length; i < len; i++) {
				sb = new StringBuilder();
				sb.append(i+1).append("\t").append(results[i]);
				writer.write(sb.toString());
				writer.newLine();
			}
			writer.flush();
			Path outputPath = new Path(output);
			FileSystem fs = outputPath.getFileSystem(conf);
			if (!fs.exists(outputPath)) {
				fs.mkdirs(outputPath);
			}
			String name = path.substring(path.lastIndexOf(File.separator) + 1);
			HDFSUtils.copyFromLocalFile(conf, path, output + 
					File.separator + name);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(writer);
		}
	}
	
	public void run(String[] args) {
		try {
			if (null == conf) conf = new Configuration();
			String[] inputArgs = new GenericOptionsParser(
					conf, args).getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error, please input three path.");
				System.out.println("1. trainset path.");
				System.out.println("2. testset path.");
				System.out.println("3. result output path.");
				System.exit(2);
			}
			Path input = new Path(inputArgs[0]);
			FileSystem fs = input.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, input);
			FSDataInputStream fsInputStream = fs.open(hdfsPaths[0]);
			Data trainData = DataLoader.load(fsInputStream, true);
			DataHandler.computeFill(trainData, 1.0);
			String hdfsInput = prepare(trainData);
			TreeNode treeNode = (TreeNode) build(hdfsInput, trainData);
			TreeNodeHelper.print(treeNode, 0, null);
			classify(treeNode, inputArgs[0], inputArgs[1], inputArgs[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DecisionTreeSprintSJob job = new DecisionTreeSprintSJob();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}

}
