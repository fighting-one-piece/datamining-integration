package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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
import org.project.modules.classifier.decisiontree.mr.AbstractJob;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGainWritable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.FileUtils;
import org.project.utils.HDFSUtils;
import org.project.utils.ShowUtils;

public class DecisionTreeC45Job extends AbstractJob {
	
	/** 对数据集做准备工作，主要就是将填充好默认值的数据集再次传到HDFS上*/
	public String prepare(Data trainData) {
		String path = FileUtils.obtainTmpTxtPath();
		DataHandler.writeData(path, trainData);
		System.out.println(path);
		String name = path.substring(path.lastIndexOf(File.separator) + 1);
		String hdfsPath = HDFSUtils.HDFS_TEMP_INPUT_URL + name;
		HDFSUtils.copyFromLocalFile(conf, path, hdfsPath);
		return hdfsPath;
	}
	
	/** 选择最佳属性，读取MapReduce计算后产生的文件，取增益率最大*/
	@SuppressWarnings("deprecation")
	public AttributeGainWritable chooseBestAttribute(String output) {
		AttributeGainWritable maxAttribute = null;
		Path path = new Path(output);
		try {
			FileSystem fs = path.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, path);
			ShowUtils.printToConsole(paths);
			double maxGainRatio = 0.0;
			SequenceFile.Reader reader = null;
			for (Path p : paths) {
				reader = new SequenceFile.Reader(fs, p, conf);
				Text key = (Text) ReflectionUtils.newInstance(
						reader.getKeyClass(), conf);
				AttributeGainWritable value = new AttributeGainWritable();
				while (reader.next(key, value)) {
					double gainRatio = value.getGainRatio();
					if (gainRatio >= maxGainRatio) {
						maxGainRatio = gainRatio;
						maxAttribute = value;
					}
					value = new AttributeGainWritable();
				}
				IOUtils.closeQuietly(reader);
			}
			System.out.println("output: " + path.toString());
			HDFSUtils.delete(conf, path);
			System.out.println("hdfs delete file : " + path.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return maxAttribute;
	}
	
	/** 构造决策树 */
	public Object build(String input, Data data) {
		Object preHandleResult = preHandle(data);
		if (null != preHandleResult) return preHandleResult;
		String output = HDFSUtils.HDFS_TEMP_OUTPUT_URL;
		HDFSUtils.delete(conf, new Path(output));
		System.out.println("delete output path : " + output);
		String[] paths = new String[]{input, output};
		//通过MapReduce计算增益率
		CalculateC45GainRatioMR.main(paths);
		
		AttributeGainWritable bestAttr = chooseBestAttribute(output);
		String attribute = bestAttr.getAttribute();
		System.out.println("best attribute: " + attribute);
		System.out.println("isCategory: " + bestAttr.isCategory());
		if (bestAttr.isCategory()) {
			return attribute;
		}
		String[] splitPoints = bestAttr.obtainSplitPoints();
		System.out.print("splitPoints: ");
		ShowUtils.printToConsole(splitPoints);
		TreeNode treeNode = new TreeNode(attribute);
		String[] attributes = data.getAttributesExcept(attribute);
		
		//分割数据集，并将分割后的数据集传到HDFS上
		DataSplit dataSplit = DataHandler.split(new Data(
				data.getInstances(), attribute, splitPoints));
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
	
	/** 分类，根据决策树节点判断测试样本集的类型，并将结果上传到HDFS上*/
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
			
			DataHandler.fill(testData.getInstances(), trainData.getAttributes(), 0);
			Object[] results = (Object[]) treeNode.classify(testData);
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
			/** 填充缺失属性的默认值*/
			DataHandler.fill(trainData, 0);
			String hdfsInput = prepare(trainData);
			TreeNode treeNode = (TreeNode) build(hdfsInput, trainData);
			TreeNodeHelper.print(treeNode, 0, null);
			classify(treeNode, inputArgs[0], inputArgs[1], inputArgs[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DecisionTreeC45Job job = new DecisionTreeC45Job();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}

}
