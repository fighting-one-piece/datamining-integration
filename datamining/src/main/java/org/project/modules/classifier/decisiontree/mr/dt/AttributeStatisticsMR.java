package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeKVWritable;

public class AttributeStatisticsMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(AttributeStatisticsMR.class);
		
		job.setMapperClass(AttributeStatisticsMapper.class);
		job.setMapOutputKeyClass(AttributeKVWritable.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(AttributeStatisticsReducer.class);
		job.setOutputKeyClass(AttributeKVWritable.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error");
				System.out.println("error, please input two path. input and output");
				System.out.println("1. input path.");
				System.out.println("2. output path.");
				System.exit(2);
			}
			configuration.set("mapred.job.queue.name", "q_hudong");
			Job job = Job.getInstance(configuration, "Attribute Statistics");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class AttributeStatisticsMapper extends Mapper<LongWritable, Text, 
	AttributeKVWritable, IntWritable> {
	
	private IntWritable one = new IntWritable(1);
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer tokenizer = new StringTokenizer(value.toString());
		tokenizer.nextToken();
		tokenizer.nextToken();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] entry = token.split(":");
			context.write(new AttributeKVWritable(entry[0], entry[1]), one);
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class AttributeStatisticsReducer extends Reducer<AttributeKVWritable, IntWritable, 
	AttributeKVWritable, IntWritable> {
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
	}
	
	@Override
	protected void reduce(AttributeKVWritable key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable value : values) {
			sum += value.get();
		}
		context.write(key, new IntWritable(sum));
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
}