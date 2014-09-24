package org.project.modules.hadoop.mapreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
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
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.utils.HDFSUtils;

public class MatrixVectorMultiplyMR {

	private static void configureJob(Job job) {
		job.setJarByClass(MatrixVectorMultiplyMR.class);

		job.setMapperClass(MatrixVectorMultiplyMapper.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(MatrixVectorMultiplyReducer.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(IntWritable.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(configuration, args)
					.getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error, please input three path.");
				System.out.println("1、 matrix file path.");
				System.out.println("2、 vector file path.");
				System.out.println("3、 output file path.");
				System.exit(1);
			}
			Job job = Job.getInstance(configuration, "Matrix Vector Multiply");

			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[2]));

			configuration.set("vector", getVector(inputArgs[1], configuration));

			configureJob(job);

			System.out.println(job.waitForCompletion(true) ? 0 : 1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getVector(String input, Configuration conf) {
		FSDataInputStream in = null;
		BufferedReader reader = null;
		Path path = new Path(input);
		try {
			FileSystem fs = path.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, path);
			in = fs.open(paths[0]);
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			if (null != line && !"".equals(line)) {
				return line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return null;
	}
}

class MatrixVectorMultiplyMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

	private int line = 0;
	private int[] vector = null;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		String vectorStr = conf.get("vector");
		String[] vectorArray = vectorStr.split(",");
		vector = new int[vectorArray.length];
		int index = 0;
		for (String v : vectorArray) {
			vector[index++] = Integer.parseInt(v);
		}
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer tokenizer = new StringTokenizer(value.toString(), ",");
		int index = 0;
		while(tokenizer.hasMoreTokens()) {
			int result = vector[index] * Integer.parseInt(tokenizer.nextToken());
			context.write(new IntWritable(line), new IntWritable(result));
			index++;
		}
		line++;
	}
}

class MatrixVectorMultiplyReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

	@Override
	protected void reduce(IntWritable key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable value : values) {
			sum += value.get();
		}
		context.write(key, new IntWritable(sum));
	}
}