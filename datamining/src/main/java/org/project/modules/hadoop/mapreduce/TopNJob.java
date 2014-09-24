package org.project.modules.hadoop.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.project.utils.HDFSUtils;
import org.project.utils.IdentityUtils;

public class TopNJob {
	
	public static Job wordCountJob(Configuration conf, String input,
			String output) throws Exception {
		Job job = Job.getInstance(conf);
		
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		
		job.setJarByClass(TopNJob.class);
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		return job;
	}
	
	public static Job topNJob(Configuration conf, String input, 
			String output) throws Exception {
		Job job = Job.getInstance(conf);
		
		preHandle(conf, input);
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		
		job.setJarByClass(TopNJob.class);
		
		job.setMapperClass(TopNMapper.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(KVWritable.class);

		job.setReducerClass(TopNReducer.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	
		return job;
	}
	
	@SuppressWarnings("deprecation")
	public static void preHandle(Configuration conf, String output) throws Exception {
		Path path = new Path(output);
		FileSystem fs = path.getFileSystem(conf);
		if (!fs.exists(path)) {
			System.out.println("path not exists : " + path);
			return;
		}
		for (FileStatus file : fs.listStatus(path)) {
			if (file.isDir() || file.getPath().getName().startsWith("_")) {
				HDFSUtils.delete(conf, file.getPath());
			}
		}
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("error parameters");
			System.exit(3);
		}
		JobControl jobControl = new JobControl("TopN");
		try {
			Configuration conf1 = new Configuration();
			ControlledJob wordCountJob = new ControlledJob(conf1);
			String output = HDFSUtils.HDFS_URL + "wordcount/temp/output/" + IdentityUtils.generateUUID();
			wordCountJob.setJob(wordCountJob(conf1, args[0], output));
			jobControl.addJob(wordCountJob);
		
			Configuration conf2 = new Configuration();
			conf2.set("topN", args[2]);
			ControlledJob topNJob = new ControlledJob(conf2);
			topNJob.setJob(topNJob(conf2, output, args[1]));
			topNJob.addDependingJob(wordCountJob);
			jobControl.addJob(topNJob);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread jcThread = new Thread(jobControl);
		jcThread.start();
		while(true) {
			if (jobControl.allFinished()) {
				System.out.println(jobControl.getSuccessfulJobList());
				jobControl.stop();
				break;
			}
			if (jobControl.getFailedJobList().size() > 0) {
				System.out.println(jobControl.getFailedJobList());
				jobControl.stop();
				break;
			}
		}
	}
}

class TopNMapper extends Mapper<LongWritable, Text, NullWritable, KVWritable> {
	
	private int topN = 0;
	
	private NullWritable nullKey = null;

	private TreeSet<KVWritable> kvs = null;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		topN = Integer.parseInt(conf.get("topN", "0"));
		nullKey = NullWritable.get();
		kvs = new TreeSet<KVWritable>(new Comparator<KVWritable>() {
			@Override
			public int compare(KVWritable o1, KVWritable o2) {
				return -o1.getKey().compareTo(o2.getKey());
			}
		});
	}
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer tokenizer = new StringTokenizer(value.toString());
		String wValue = tokenizer.nextToken();
		String wKey = tokenizer.nextToken();
		kvs.add(new KVWritable(wKey, wValue));
		if (kvs.size() > topN) {
			kvs.remove(kvs.first());
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		Iterator<KVWritable> iter = kvs.iterator();
		while (iter.hasNext()) {
			KVWritable kv = iter.next();
			System.out.println("k:" + kv.getKey() + "-v:" + kv.getValue());
			context.write(nullKey, kv);
			if ((topN--) <= 0) break;
		}
	}
}

class TopNReducer extends Reducer<NullWritable, KVWritable, IntWritable, Text> {
	
	private int topN = 0;
	
	private TreeSet<KVWritable> kvs = null;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		topN = Integer.parseInt(conf.get("topN", "0"));
		kvs = new TreeSet<KVWritable>(new Comparator<KVWritable>() {
			@Override
			public int compare(KVWritable o1, KVWritable o2) {
				return -o1.getKey().compareTo(o2.getKey());
			}
		});
	}
	
	@Override
	protected void reduce(NullWritable key, Iterable<KVWritable> values, Context context)
			throws IOException, InterruptedException {
		for (KVWritable kv : values) {
			kvs.add(kv);
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		Iterator<KVWritable> iter = kvs.iterator();
		while (iter.hasNext()) {
			KVWritable kv = iter.next();
			context.write(kv.getKey(), kv.getValue());
			if ((topN--) <= 0) break;
		}
	}
}

class KVWritable implements WritableComparable<KVWritable> {
	
	private IntWritable key = null;
	
	private Text value = null;
	
	public KVWritable() {
		key = new IntWritable();
		value = new Text();
	}
	
	public KVWritable(String k, String v) {
		key = new IntWritable(Integer.parseInt(k));
		value = new Text(v);
	}
	
	public IntWritable getKey() {
		return key;
	}
	
	public Text getValue() {
		return value;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		key.readFields(dataInput);
		value.readFields(dataInput);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		System.out.println("k:" + key + "-v:" + value);
		key.write(dataOutput);
		value.write(dataOutput);
	}

	@Override
	public int compareTo(KVWritable o) {
		return this.key.compareTo(o.key);
	}
	
}