package org.project.modules.hadoop.mr;

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
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MartrixVectorMultiplyMR {

	private static void configureJob(Job job) {
		job.setJarByClass(MartrixVectorMultiplyMR.class);

		job.setMapperClass(MartrixVectorMultiplyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(MartrixVectorMultiplyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setInputFormatClass(FileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(configuration, args)
					.getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out
						.println("error, please input two path. input and output");
				System.exit(2);
			}
			Job job = new Job(configuration, "Martrix Vector Multiply");

			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));

			configuration.set("rowNum", inputArgs[2]);
			configuration.set("colNum", inputArgs[3]);

			configureJob(job);

			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class MartrixVectorMultiplyMapper extends
		Mapper<LongWritable, Text, Text, IntWritable> {

	private Text lineNumber = new Text(); // 矩阵行序号
	private static int i = 0;
	private final static int[] vector = { 2, 3, 4 }; // 向量值

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer itr = new StringTokenizer(value.toString());
		int j = 0; // 向量序号
		lineNumber.set(i + "");
		while (itr.hasMoreTokens()) {
			int result = vector[j] * Integer.parseInt(itr.nextToken());
			IntWritable one = new IntWritable(result);
			context.write(lineNumber, one);
			j++;
		}
		i++;
	}
}

class MartrixVectorMultiplyReducer extends
		Reducer<Text, IntWritable, Text, IntWritable> {

	private IntWritable result = new IntWritable();

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable val : values) {
			sum += val.get();
		}
		result.set(sum);
		context.write(key, result);
	}
}