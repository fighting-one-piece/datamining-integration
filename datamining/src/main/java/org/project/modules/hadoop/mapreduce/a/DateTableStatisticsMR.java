package org.project.modules.hadoop.mapreduce.a;

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
import org.project.modules.hadoop.mapreduce.a.writable.DateTableWritable;

public class DateTableStatisticsMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(DateTableStatisticsMR.class);
		
		job.setMapperClass(DateTableStatisticsMapper.class);
		job.setMapOutputKeyClass(DateTableWritable.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(DateTableStatisticsReducer.class);
		job.setOutputKeyClass(DateTableWritable.class);
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
				System.out.println("error, please input two path. input and output");
				System.exit(2);
			}
			configuration.set("mapred.job.queue.name", "q_hudong");
			Job job = Job.getInstance(configuration, "CombineSmallFile");
			
			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class DateTableStatisticsMapper extends Mapper<LongWritable, Text, DateTableWritable, IntWritable> {
	
	private IntWritable one = new IntWritable(1);
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer token = new StringTokenizer(value.toString());
		String date = token.nextToken();
		String table = token.nextToken();
		context.write(new DateTableWritable(new Text(date), new Text(table)), one);
	}
}

class DateTableStatisticsReducer extends Reducer<DateTableWritable, IntWritable, DateTableWritable, IntWritable> {
	
	@Override
	protected void reduce(DateTableWritable key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable value : values) {
			sum += value.get();
		}
		context.write(key, new IntWritable(sum));
	}
}