package org.project.modules.hadoop.mr.a;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class TableJoin {
	public static int time = 0;

	public static class Map extends Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] strs = line.split("\t");
			System.out.println("strs[0]: " + strs[0]);
			context.write(new Text(strs[1]), new Text("1" + "-" + strs[0]));// 输出左表
			context.write(new Text(strs[0]), new Text("2" + "-" + strs[1]));// 输出右表
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			if (time == 0) {// 输出表头
				context.write(new Text("grandchild"), new Text("grandparent"));
				time++;
			}
			int grandchildNum = 0;
			String grandchild[] = new String[20];
			int grandparentNum = 0;
			String grandparent[] = new String[20];

			Iterator<Text> iter = values.iterator();
			while (iter.hasNext()) {
				String record = iter.next().toString();
				System.out.println("record: " + record);
				String[] st = record.split("-");
				if (st[0].equals("1")) {
					grandchild[grandchildNum] = st[1];
					grandchildNum++;
				} else if (st[0].equals("2")) {
					grandparent[grandparentNum] = st[1];
					grandparentNum++;
				}
			}
			System.out.println(grandchildNum + ":" + grandparentNum);
			// grandchild和grandparent数组求笛卡尔积
			if (grandchildNum != 0 && grandparentNum != 0) {
				for (int m = 0; m < grandchildNum; m++) {
					for (int n = 0; n < grandparentNum; n++) {
						context.write(new Text(grandchild[m]), new Text(
								grandparent[n]));
					}
				}

			}

		}

	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage:...");
			System.exit(2);
		}
		Job job = new Job(conf, "single table join");
		job.setJarByClass(TableJoin.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : -1);
	}
}
