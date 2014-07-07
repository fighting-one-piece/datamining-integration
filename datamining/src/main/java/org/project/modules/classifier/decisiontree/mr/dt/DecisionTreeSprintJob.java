package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.DataSplit;
import org.project.modules.classifier.decisiontree.data.DataSplitItem;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.mr.AbstractJob;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGiniWritable;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeWritable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.HDFSUtils;
import org.project.utils.IdentityUtils;
import org.project.utils.ShowUtils;

public class DecisionTreeSprintJob extends AbstractJob {
	
	private Map<String, Map<String, Set<String>>> map = null;
	
//	private Map<String, Set<String>> attributeToValues = null;
	
//	private Set<String> allAttributes = null;
	
	/** 数据拆分*/
	private List<String> split(String input) {
		List<String> inputs = new ArrayList<String>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			Path inputPath = new Path(input);
			FileSystem fs = inputPath.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, inputPath);
			in = fs.open(hdfsPaths[0]);
			reader = new BufferedReader(new InputStreamReader(in));
			List<String> lines = new ArrayList<String>();
			int index = 0;
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				lines.add(line);
				if ((index++) == 13) {
					inputs.add(writeToHDFS(lines));
					lines = new ArrayList<String>();
					index = 0;
				}
				line = reader.readLine();
			}
			if (lines.size() > 0) {
				inputs.add(writeToHDFS(lines));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return inputs;
	}
	
	private String writeToHDFS(List<String> lines) {
		List<Instance> instances = new ArrayList<Instance>();
		Set<String> attributes = new HashSet<String>();
		for (String line : lines) {
			Instance instance = DataHandler.extractWithId(line, attributes);
			instances.add(instance);
		}
		Data data = new Data(attributes.toArray(new String[0]), instances);
		DataHandler.computeFill(data, 1.0);
		OutputStream out = null;
		BufferedWriter writer = null;
		String outputDir = HDFSUtils.HDFS_TEMP_INPUT_URL + IdentityUtils.generateUUID();
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
		addPathToAttributeValueStatisticsMap(output, instances);
		return outputDir;
	}
	
	private void addPathToAttributeValueStatisticsMap(String path, List<Instance> instances) {
		if (null == map) {
			map = new HashMap<String, Map<String, Set<String>>>();
		}
		Map<String, Set<String>> attributeValueStatistics = map.get(path);
		if (null == attributeValueStatistics) {
			attributeValueStatistics = new HashMap<String, Set<String>>();
			map.put(path, attributeValueStatistics);
		}
		for (Instance instance : instances) {
			Map<String, Object> children = instance.getAttributes();
			for (Map.Entry<String, Object> entry : children.entrySet()) {
				String attributeName = entry.getKey();
				Set<String> values = attributeValueStatistics.get(attributeName);
				if (null == values) {
					values = new HashSet<String>();
					attributeValueStatistics.put(attributeName, values);
				}
				values.add(String.valueOf(entry.getValue()));
			}
		}
	}
	
	private Job createJob(String jobName, String input, String output) {
		Configuration conf = new Configuration();
		Job job = null;
		try {
			job = new Job(conf, jobName);
			
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			
			job.setJarByClass(DecisionTreeSprintJob.class);
			
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
	
	private Data obtainData(String input) throws IOException {
		Path inputPath = new Path(input);
		FileSystem fs = inputPath.getFileSystem(conf);
		Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, inputPath);
		FSDataInputStream fsInputStream = fs.open(hdfsPaths[0]);
		Data data = DataLoader.load(fsInputStream, true);
		DataHandler.computeFill(data, 1.0);
		return data;
	}
	
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
                System.out.println(jobControl.getSuccessfulJobList());  
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
        			Map<String, Set<String>> attrName2Values = 
        					DataHandler.attributeValueStatistics(data.getInstances());
        			Set<String> attributeValues = attrName2Values.get(attribute);
        			System.out.println("attributeValues:");
        			ShowUtils.print(attributeValues);
        			if (attrName2Values.size() == 0 || null == attributeValues) {
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
                System.out.println(jobControl.getFailedJobList());  
                jobControl.stop();  
            }  
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
			List<String> inputs = split(inputArgs[0]);
			TreeNode treeNode = (TreeNode) build(inputs);
			TreeNodeHelper.print(treeNode, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DecisionTreeSprintJob job = new DecisionTreeSprintJob();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}

}

