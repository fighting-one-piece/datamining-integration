package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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
import org.project.utils.IdentityUtils;

public class DataFileSplitMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(DataFileSplitMR.class);
		
		job.setMapperClass(DataFileSplitMapper.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(DataFileSplitReducer.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
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
				System.out.println("3. data split number.");
				System.exit(2);
			}
			configuration.set("mapred.job.queue.name", "q_hudong");
			configuration.set("data.split.number", inputArgs[2]);
			Job job = new Job(configuration, "Data Split");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class DataFileSplitMapper extends Mapper<LongWritable, Text, 
	LongWritable, Text> {
	
	private int splitNum = 0;
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		String dataSplitNum = conf.get("data.split.number", "100");
		this.splitNum = Integer.parseInt(dataSplitNum);
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		Long id = Long.parseLong(tokenizer.nextToken());
		context.write(new LongWritable(id % splitNum), value);
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class DataFileSplitReducer extends Reducer<LongWritable, Text, NullWritable, Text> {
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
	}
	
	@Override
	protected void reduce(LongWritable key, Iterable<Text> values,
			Context context) throws IOException, InterruptedException {
		String outputDir = HDFSUtils.HDFS_TEMP_INPUT_URL + IdentityUtils.generateUUID();
		String output = outputDir + File.separator + IdentityUtils.generateUUID();
		Path outputPath = new Path(output);
		Configuration conf = context.getConfiguration();
		FileSystem fs = outputPath.getFileSystem(conf);
		OutputStream out = fs.create(outputPath);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		for (Text value : values) {
			writer.write(value.toString());
			writer.newLine();
		}
		writer.flush();
		IOUtils.closeQuietly(out);
		IOUtils.closeQuietly(writer);
		context.write(NullWritable.get(), new Text(output));
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
}