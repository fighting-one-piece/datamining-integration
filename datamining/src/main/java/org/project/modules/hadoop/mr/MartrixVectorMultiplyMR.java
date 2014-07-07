package org.project.modules.hadoop.mr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
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
		job.setMapOutputValueClass(BytesWritable.class);

		job.setReducerClass(MartrixVectorMultiplyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(BytesWritable.class);
		
		job.setInputFormatClass(FileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
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

class MartrixVectorMultiplyMapper extends Mapper<NullWritable, BytesWritable, Text, BytesWritable> {
	
	@Override
	protected void map(NullWritable key, BytesWritable value, Context context)
			throws IOException, InterruptedException {
		String name = context.getConfiguration().get("map.input.file.name");
		context.write(new Text(name), value);
	}
}

class MartrixVectorMultiplyReducer extends Reducer<Text, BytesWritable, Text, BytesWritable> {
	
	@Override
	protected void reduce(Text key, Iterable<BytesWritable> values, Context context)
			throws IOException, InterruptedException {
		for (BytesWritable value : values) {
			context.write(key, value);
		}
	}
}