package org.project.modules.association.apriori.mr;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
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
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ReflectionUtils;
import org.project.modules.association.apriori.builder.FPGrowthBuilder;
import org.project.modules.association.apriori.data.Data;
import org.project.modules.association.apriori.data.Instance;
import org.project.modules.association.apriori.data.ItemSet;

@SuppressWarnings("deprecation")
public class FPGrowthMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(FPGrowthMR.class);
		
		job.setMapperClass(FPGrowthMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(FPGrowthReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
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
				System.out.println("error, please input two path. input and output");
				System.out.println("1. input path.");
				System.out.println("2. output path.");
				System.out.println("3. sort input path.");
				System.exit(2);
			}
//			configuration.set("mapred.job.queue.name", "q_hudong");
			configuration.set("sort.input.path", inputArgs[2]);
			
			Path sortPath = new Path(inputArgs[2]);
			DistributedCache.addCacheFile(sortPath.toUri(), configuration);
			
			Job job = Job.getInstance(configuration, "FPGrowth Algorithm");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class FPGrowthMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	private List<Map.Entry<String, Integer>> entries = null;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		URI[] uris = DistributedCache.getCacheFiles(conf);
		Map<String, Integer> map = new HashMap<String, Integer>();
		SequenceFile.Reader reader = null;
		try {
			Path path = new Path(uris[0]);
			FileSystem fs = FileSystem.get(conf);
			reader = new SequenceFile.Reader(fs, path, conf);
			Text key = (Text) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			IntWritable value = new IntWritable();
			while (reader.next(key, value)) {
				map.put(key.toString(), value.get());
				key = new Text();
				value = new IntWritable();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		entries = new ArrayList<Map.Entry<String, Integer>>(); 
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			entries.add(entry);
		}
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer tokenizer = new StringTokenizer(value.toString());
		tokenizer.nextToken();
		List<String> results = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] items = token.split(",");
			for (Map.Entry<String, Integer> entry : entries) {
				String eKey = entry.getKey();
				for (String item : items) {
					if (eKey.equals(item)) {
						results.add(eKey);
						break;
					}
				}
			}
		}
		String[] values = results.toArray(new String[0]);
		StringBuilder sb = new StringBuilder();
		for (String v : values) {
			sb.append(v).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		for (String v : values) {
			context.write(new Text(v), new Text(sb.toString()));
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class FPGrowthReducer extends Reducer<Text, Text, Text, IntWritable> {
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
	}
	
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Context context) throws IOException, InterruptedException {
		String keyItem = key.toString();
		System.out.println("key: " + keyItem);
		Data data = new Data();
		for (Text value : values) {
			Instance instance = new Instance();
			StringTokenizer tokenizer = new StringTokenizer(value.toString());
			String token = tokenizer.nextToken();
			String[] items = token.split(",");
			List<String> temp = new ArrayList<String>();
			for (String item : items) {
				if (keyItem.equals(item)) {
					break;
				}
				temp.add(item);
			}
			instance.setValues(temp.toArray(new String[0]));
			data.getInstances().add(instance);
		}
		context.write(new Text(keyItem), new IntWritable(data.getInstances().size()));
		FPGrowthBuilder fpBuilder = new FPGrowthBuilder();
		fpBuilder.build(data, null);
		List<List<ItemSet>> frequencies = fpBuilder.obtainFrequencyItemSet();
		for (List<ItemSet> frequency : frequencies) {
			for (ItemSet itemSet : frequency) {
				StringBuilder sb = new StringBuilder();
				for (String i : itemSet.getItems()) {
					sb.append(i).append(",");
				}
				sb.append(keyItem);
				context.write(new Text(sb.toString()), new IntWritable(itemSet.getSupport()));
			}
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
}