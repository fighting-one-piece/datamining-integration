package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
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
import org.project.modules.classifier.decisiontree.mr.writable.AttributeKVWritable;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeWritable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.FileUtils;
import org.project.utils.HDFSUtils;
import org.project.utils.IdentityUtils;
import org.project.utils.ShowUtils;

public class DecisionTreeSprintBJob extends AbstractJob {
	
	private Map<String, Map<Object, Integer>> attributeValueStatistics = null;
	
	private Map<String, Set<String>> attributeNameToValues = null;
	
	private Set<String> allAttributes = null;
	
	/** 数据拆分,大数据文件拆分为小数据文件，便于分配到各个节点开启Job*/
	private List<String> split(String input, String splitNum) {
		String output = HDFSUtils.HDFS_TEMP_INPUT_URL + IdentityUtils.generateUUID();
		String[] args = new String[]{input, output, splitNum};
		DataFileSplitMR.main(args);
		List<String> inputs = new ArrayList<String>();
		Path outputPath = new Path(output);
		try {
			FileSystem fs = outputPath.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, outputPath);
			for(Path path : paths) {
				System.out.println("split input path: " + path);
				InputStream in = fs.open(path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = reader.readLine();
				while (null != line && !"".equals(line)) {
					inputs.add(line);
					line = reader.readLine();
				}
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("inputs size: " + inputs.size());
		return inputs;
	}
	
	/** 初始化工作，主要是获取特征属性集以及属性值的统计，主要是为了填充默认值*/
	private void initialize(String input) {
		System.out.println("initialize start.");
		allAttributes = new HashSet<String>();
		attributeNameToValues = new HashMap<String, Set<String>>();
		attributeValueStatistics = new HashMap<String, Map<Object, Integer>>();
		String output = HDFSUtils.HDFS_TEMP_INPUT_URL + IdentityUtils.generateUUID();
		String[] args = new String[]{input, output};
		AttributeStatisticsMR.main(args);
		Path outputPath = new Path(output);
		SequenceFile.Reader reader = null;
		try {
			FileSystem fs = outputPath.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, outputPath);
			for(Path path : paths) {
				reader = new SequenceFile.Reader(fs, path, conf);
				AttributeKVWritable key = (AttributeKVWritable) 
						ReflectionUtils.newInstance(reader.getKeyClass(), conf);
				IntWritable value = new IntWritable();
				while (reader.next(key, value)) {
					String attributeName = key.getAttributeName();
					allAttributes.add(attributeName);
					Set<String> values = attributeNameToValues.get(attributeName);
					if (null == values) {
						values = new HashSet<String>();
						attributeNameToValues.put(attributeName, values);
					}
					String attributeValue = key.getAttributeValue();
					values.add(attributeValue);
					Map<Object, Integer> valueStatistics = 
							attributeValueStatistics.get(attributeName);
					if (null == valueStatistics) {
						valueStatistics = new HashMap<Object, Integer>();
						attributeValueStatistics.put(attributeName, valueStatistics);
					}
					valueStatistics.put(attributeValue, value.get());
					value = new IntWritable();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		System.out.println("initialize end.");
	}
	
	/** 预处理,主要是将分割后的小文件填充好默认值后在上传到HDFS上面*/
	private List<String> preHandle(List<String> inputs) throws IOException {
		List<String> fillInputs = new ArrayList<String>();
		for (String input : inputs) {
			Data data =null;
			try {
				Path inputPath = new Path(input);
				FileSystem fs = inputPath.getFileSystem(conf);
				FSDataInputStream fsInputStream = fs.open(inputPath);
				data = DataLoader.load(fsInputStream, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			DataHandler.computeFill(data.getInstances(), 
					allAttributes.toArray(new String[0]), 
					attributeValueStatistics, 1.0);
			OutputStream out = null;
			BufferedWriter writer = null;
			String outputDir = HDFSUtils.HDFS_TEMP_INPUT_URL + IdentityUtils.generateUUID();
			fillInputs.add(outputDir);
			String output = outputDir + File.separator + IdentityUtils.generateUUID();
			try {
				Path outputPath = new Path(output);
				FileSystem fs = outputPath.getFileSystem(conf);
				out = fs.create(outputPath);
				writer = new BufferedWriter(new OutputStreamWriter(out));
				StringBuilder sb = null;
				for (Instance instance : data.getInstances()) {
					sb = new StringBuilder();
					sb.append(instance.getId()).append("\t");
					sb.append(instance.getCategory()).append("\t");
					Map<String, Object> attrs = instance.getAttributes();
					for (Map.Entry<String, Object> entry : attrs.entrySet()) {
						sb.append(entry.getKey()).append(":");
						sb.append(entry.getValue()).append("\t");
					}
					writer.write(sb.toString());
					writer.newLine();
				}
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(out);
				IOUtils.closeQuietly(writer);
			}
		}
		return fillInputs;
	}
	
	/** 创建JOB*/
	private Job createJob(String jobName, String input, String output) {
		Configuration conf = new Configuration();
		conf.set("mapred.job.queue.name", "q_hudong");
		Job job = null;
		try {
			job = new Job(conf, jobName);
			
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			
			job.setJarByClass(DecisionTreeSprintBJob.class);
			
			job.setMapperClass(CalculateGiniMapper.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(AttributeWritable.class);
			
			job.setReducerClass(CalculateGiniReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(AttributeGiniWritable.class);
			
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(SequenceFileOutputFormat.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return job;
	}
	
	/** 根据HDFS上的输出路径选择最佳属性*/
	private AttributeGiniWritable chooseBestAttribute(String... outputs) {
		AttributeGiniWritable minSplitAttribute = null;
		double minSplitPointGini = 1.0;
		try {
			for (String output : outputs) {
				System.out.println("choose output: " + output);
				Path outputPath = new Path(output);
				FileSystem fs = outputPath.getFileSystem(conf);
				Path[] paths = HDFSUtils.getPathFiles(fs, outputPath);
				ShowUtils.print(paths);
				SequenceFile.Reader reader = null;
				for (Path path : paths) {
					reader = new SequenceFile.Reader(fs, path, conf);
					Text key = (Text) ReflectionUtils.newInstance(
							reader.getKeyClass(), conf);
					AttributeGiniWritable value = new AttributeGiniWritable();
					while (reader.next(key, value)) {
						double gini = value.getGini();
						System.out.println(value.getAttribute() + " : " + gini);
						if (gini <= minSplitPointGini) {
							minSplitPointGini = gini;
							minSplitAttribute = value;
						}
						value = new AttributeGiniWritable();
					}
					IOUtils.closeQuietly(reader);
				}
				System.out.println("delete hdfs file start: " + outputPath.toString());
				HDFSUtils.delete(conf, outputPath);
				System.out.println("delete hdfs file end: " + outputPath.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null == minSplitAttribute) {
			System.out.println("minSplitAttribute is null");
		}
		return minSplitAttribute;
	}
	
	private Data obtainData(String input) {
		Data data = null;
		Path inputPath = new Path(input);
		try {
			FileSystem fs = inputPath.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, inputPath);
			FSDataInputStream fsInputStream = fs.open(hdfsPaths[0]);
			data = DataLoader.load(fsInputStream, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/** 构建决策树*/
	private Object build(List<String> inputs) throws IOException {
		List<String> outputs = new ArrayList<String>();
		JobControl jobControl = new JobControl("CalculateGini");
		for (String input : inputs) {
			System.out.println("split path: " + input);
			String output = HDFSUtils.HDFS_TEMP_OUTPUT_URL + IdentityUtils.generateUUID();
			outputs.add(output);
			Configuration conf = new Configuration();
			ControlledJob controlledJob = new ControlledJob(conf);
			controlledJob.setJob(createJob(input, input, output));
			jobControl.addJob(controlledJob);
		}
		Thread jcThread = new Thread(jobControl);  
        jcThread.start();  
        while(true){  
            if(jobControl.allFinished()){  
//                System.out.println(jobControl.getSuccessfulJobList());  
                jobControl.stop();  
                AttributeGiniWritable bestAttr = chooseBestAttribute(
                		outputs.toArray(new String[0]));
                String attribute = bestAttr.getAttribute();
        		System.out.println("best attribute: " + attribute);
        		System.out.println("isCategory: " + bestAttr.isCategory());
        		if (bestAttr.isCategory()) {
        			return attribute;
        		}
        		TreeNode treeNode = new TreeNode(attribute);
        		Map<String, List<String>> splitToInputs = 
        				new HashMap<String, List<String>>();
        		for (String input : inputs) {
        			Data data = obtainData(input);
        			String splitPoint = bestAttr.getSplitPoint();
//        			Map<String, Set<String>> attrName2Values = 
//        					DataHandler.attributeValueStatistics(data.getInstances());
        			Set<String> attributeValues = attributeNameToValues.get(attribute);
        			System.out.println("attributeValues:");
        			ShowUtils.print(attributeValues);
        			if (attributeNameToValues.size() == 0 || null == attributeValues) {
        				continue;
        			}
        			attributeValues.remove(splitPoint);
        			StringBuilder sb = new StringBuilder();
        			for (String attributeValue : attributeValues) {
        				sb.append(attributeValue).append(",");
        			}
        			if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        			String[] names = new String[]{splitPoint, sb.toString()};
        			DataSplit dataSplit = DataHandler.split(new Data(
        					data.getInstances(), attribute, names));
        			for (DataSplitItem item : dataSplit.getItems()) {
        				if (item.getInstances().size() == 0) continue;
        				String path = item.getPath();
        				String name = path.substring(path.lastIndexOf(File.separator) + 1);
        				String hdfsPath = HDFSUtils.HDFS_TEMP_INPUT_URL + name;
        				HDFSUtils.copyFromLocalFile(conf, path, hdfsPath);
        				String split = item.getSplitPoint();
        				List<String> nextInputs = splitToInputs.get(split);
        				if (null == nextInputs) {
        					nextInputs = new ArrayList<String>();
        					splitToInputs.put(split, nextInputs);
        				}
        				nextInputs.add(hdfsPath);
        			}
        		}
        		for (Map.Entry<String, List<String>> entry : 
        			splitToInputs.entrySet()) {
        			treeNode.setChild(entry.getKey(), build(entry.getValue()));
        		}
        		return treeNode;
            }  
            if(jobControl.getFailedJobList().size() > 0){  
//                System.out.println(jobControl.getFailedJobList());  
                jobControl.stop();  
            }  
        }  
	}
	
	/** 分类样本集*/
	private void classify(TreeNode treeNode, String testSet, String output) {
		OutputStream out = null;
		BufferedWriter writer = null;
		try {
			Path testSetPath = new Path(testSet);
			FileSystem testFS = testSetPath.getFileSystem(conf);
			Path[] testHdfsPaths = HDFSUtils.getPathFiles(testFS, testSetPath);
			FSDataInputStream fsInputStream = testFS.open(testHdfsPaths[0]);
			Data testData = DataLoader.load(fsInputStream, true);
			
			DataHandler.computeFill(testData.getInstances(), 
					allAttributes.toArray(new String[0]), 
					attributeValueStatistics, 1.0);
			Object[] results = (Object[]) treeNode.classifySprint(testData);
			ShowUtils.print(results);
			DataError dataError = new DataError(testData.getCategories(), results);
			dataError.report();
			String path = FileUtils.obtainRandomTxtPath();
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
			if (inputArgs.length != 4) {
				System.out.println("error, please input three path.");
				System.out.println("1. trainset path.");
				System.out.println("2. testset path.");
				System.out.println("3. result output path.");
				System.out.println("4. data split number.");
				System.exit(2);
			}
			List<String> splitInputs = split(inputArgs[0], inputArgs[3]);
			initialize(inputArgs[0]);
			List<String> inputs = preHandle(splitInputs);
			TreeNode treeNode = (TreeNode) build(inputs);
			TreeNodeHelper.print(treeNode, 0, null);
			classify(treeNode, inputArgs[1], inputArgs[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DecisionTreeSprintBJob job = new DecisionTreeSprintBJob();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}

}
