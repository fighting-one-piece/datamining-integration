package org.project.modules.hadoop.mapreduce.a;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class TestMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(TestMR.class);
		
		job.setMapperClass(TestMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(PWritable.class);

		job.setReducerClass(TestReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
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
			Job job = Job.getInstance(configuration, "TestMR");
			
			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class TestMapper extends Mapper<LongWritable, Text, Text, PWritable> {
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] datas = line.split(" ");
		double x = Double.parseDouble(datas[0]);
		double y = Double.parseDouble(datas[1]);
		context.write(new Text(datas[2]), new PWritable(x, y, datas[2]));
	}
}

class TestReducer extends Reducer<Text, PWritable, Text, Text> {
	
	@Override
	protected void reduce(Text key, Iterable<PWritable> values, Context context)
			throws IOException, InterruptedException {
		System.out.println(key);
		List<PWritable> ps = new ArrayList<PWritable>();
		for (PWritable value : values) {
			System.out.println("v: " + value.getX()+"-"+value.getY()+"-"+value.getCategory());
			ps.add(value);
		}
		for (PWritable value : ps) {
			System.out.println("p: " + value.getX()+"-"+value.getY()+"-"+value.getCategory());
		}
	}
}

class PWritable implements Writable {
	
	private DoubleWritable x = null;
	
	private DoubleWritable y = null;
	
	private Text category = null;
	
	public PWritable() {
		this.x = new DoubleWritable();
		this.y = new DoubleWritable();
		this.category = new Text();
	}
	
	public PWritable(double x, double y, String category) {
		this.x = new DoubleWritable(x);
		this.y = new DoubleWritable(y);
		this.category = new Text(category);
	}

	public DoubleWritable getX() {
		return x;
	}

	public void setX(DoubleWritable x) {
		this.x = x;
	}

	public DoubleWritable getY() {
		return y;
	}

	public void setY(DoubleWritable y) {
		this.y = y;
	}

	public Text getCategory() {
		return category;
	}

	public void setCategory(Text category) {
		this.category = category;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		x.readFields(in);
		y.readFields(in);
		category.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		x.write(out);
		y.write(out);
		category.write(out);
	}

}