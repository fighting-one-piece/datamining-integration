package org.project.modules.classifier.decisiontree.mr.rf;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.project.utils.HDFSUtils;
import org.project.utils.ShowUtils;

@SuppressWarnings("deprecation")
public class RandomForestClassifyMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(RandomForestClassifyMR.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(RandomForestClassifyMapper.class);
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
			
			Job job = new Job(configuration, "Random Forest");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class RandomForestClassifyMapper extends Mapper<LongWritable, Text, IntWritable, Text> {

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
	
	@SuppressWarnings({ "resource", "deprecation" })
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		Configuration conf = context.getConfiguration();
		FileSystem fs = FileSystem.get(conf);
		URI[] uris = DistributedCache.getCacheFiles(conf);
		
		Path path = new Path(uris[0]);
		Path[] seqFilePaths = HDFSUtils.getPathFiles(fs, path);
		
		Map<String, Set<TreeNode>> map = new HashMap<String, Set<TreeNode>>();
		
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, seqFilePaths[0], conf);
		Text key = (Text) ReflectionUtils.newInstance(reader.getKeyClass(), conf); 
		TreeNodeWritable value = new TreeNodeWritable();
		while (reader.next(key, value)) {
			String indentity = new String(key.getBytes()).substring(0, 1);
			Set<TreeNode> treeNodes = map.get(indentity);
			if (null == treeNodes) {
				treeNodes = new HashSet<TreeNode>();
				map.put(indentity, treeNodes);
			}
			treeNodes.add(value.getTreeNode());
			value = new TreeNodeWritable();
		}
		Data data = new Data(attributes.toArray(new String[0]), instances);
		DataHandler.fill(data, 0);
		List<Object[]> finalResults = new ArrayList<Object[]>();
		for (Set<TreeNode> treeNodes : map.values()) {
			List<Object[]> results = new ArrayList<Object[]>();
			for (TreeNode treeNode : treeNodes) {
				Object[] result = (Object[]) treeNode.classify(data);
				results.add(result);
			}
			Object[] finalResult = DataHandler.vote(results);
			ShowUtils.printToConsole(finalResult);
			finalResults.add(finalResult);
		}
		Object[] result = DataHandler.vote(finalResults);
		for (int i = 0, len = result.length; i < len; i++) {
			context.write(new IntWritable(i), new Text(String.valueOf(result[i])));
		}
	}
	
}
