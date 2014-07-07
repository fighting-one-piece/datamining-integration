package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ReflectionUtils;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.mr.writable.TreeNodeWritable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.HDFSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTreeClassifyMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(DecisionTreeClassifyMR.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(DecisionTreeClassifyMapper.class);
		job.setNumReduceTasks(0); 
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error");
				System.exit(2);
			}
			
			DistributedCache.addCacheFile(
					new Path(inputArgs[2]).toUri(), configuration);
			
			Job job = new Job(configuration, "Decision Tree");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
//			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class DecisionTreeClassifyMapper extends Mapper<LongWritable, Text, 
	IntWritable, Text> {
	
	private static final Logger log = LoggerFactory.getLogger(DecisionTreeClassifyMapper.class);
	
	private List<Instance> instances = new ArrayList<Instance>();
	
	private Set<String> attributes = new HashSet<String>();
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		instances.add(DataHandler.extract(line, attributes));
	}
	
	@SuppressWarnings("resource")
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		Data data = new Data(attributes.toArray(new String[0]), instances);

		Configuration conf = context.getConfiguration();
		FileSystem fs = FileSystem.get(conf);
		URI[] uris = DistributedCache.getCacheFiles(conf);
		
		Path path = new Path(uris[0]);
		Path[] seqFilePaths = HDFSUtils.getPathFiles(fs, path);
		
		List<Object[]> results = new ArrayList<Object[]>();
		Set<String> attrs = new HashSet<String>();
		
		Set<TreeNode> treeNodes = new HashSet<TreeNode>();
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, seqFilePaths[0], conf);
		LongWritable key = (LongWritable) ReflectionUtils.newInstance(reader.getKeyClass(), conf); 
		TreeNodeWritable value = new TreeNodeWritable();
		while (reader.next(key, value)) {
			TreeNode treeNode = value.getTreeNode();
			if (null  == treeNode) continue;
			TreeNodeHelper.obtainAttributes(treeNode, attrs);
			treeNodes.add(treeNode);
			value = new TreeNodeWritable();
		}
		System.out.println("data len: " + data.getAttributes().length);
		System.out.println("tree len: " + attrs.size());
		DataHandler.fill(data.getInstances(), attrs.toArray(new String[0]), 0);
		for (TreeNode treeNode : treeNodes) {
			Object[] result = (Object[]) treeNode.classify(data);
			results.add(result);
		}
		Object[] finalResult = DataHandler.vote(results);
		for (int i = 0, len = finalResult.length; i < len; i++) {
			context.write(new IntWritable(i), new Text(String.valueOf(finalResult[i])));
		}
		log.info("DecisionTreeClassifyMapper cleanup finish");
	}
}