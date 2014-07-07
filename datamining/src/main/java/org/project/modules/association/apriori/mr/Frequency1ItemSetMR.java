package org.project.modules.association.apriori.mr;

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

public class Frequency1ItemSetMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(Frequency1ItemSetMR.class);
		
		job.setMapperClass(Frequency1ItemSetMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(Frequency1ItemSetReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
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
				System.out.println("3. min support.");
				System.exit(2);
			}
//			configuration.set("mapred.job.queue.name", "q_hudong");
			configuration.set("minSupport", inputArgs[2]);
			Job job = new Job(configuration, "Frequency 1 ItemSet Statistics");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Frequency1ItemSetMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	
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
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] items = token.split(",");
			for (String item : items) {
				context.write(new Text(item), one);
			}
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class Frequency1ItemSetReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	
	private int minSupport = 0;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		minSupport = Integer.parseInt(conf.get("minSupport", "0"));
	}
	
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable value : values) {
			sum += value.get();
		}
		if (sum >= minSupport) {
			context.write(key, new IntWritable(sum));
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
}