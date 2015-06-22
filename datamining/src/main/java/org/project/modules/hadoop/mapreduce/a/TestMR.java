package org.project.modules.hadoop.mapreduce.a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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

public class TestMR {

	private static void configureJob(Job job) {
		job.setJarByClass(TestMR.class);

		job.setMapperClass(TestMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(TestReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setInputFormatClass(TextInputFormat.class);
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
			Job job = Job.getInstance(configuration, "TestMR");

			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));

			configureJob(job);
			
//			ChainMapper.addMapper(job, klass, inputKeyClass, inputValueClass, outputKeyClass, outputValueClass, mapperConf);
//			ChainReducer.setReducer(job, klass, inputKeyClass, inputValueClass, outputKeyClass, outputValueClass, reducerConf);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


class TestMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	private static Text text1 = new Text();

	private Text text2 = new Text();
	
	private static int i = 0;
	
	private int j = 0;
	
	private A a2 = new A("no static");
	private static A a1 = new A("static");
	
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		text1 = value;
		text2 = value;
		i++;
		j++;
		System.out.println("map: " + value + " text1: " + text1);
		System.out.println("map: " + value + " text2: " + text2);
		System.out.println("map i: " + i);
		System.out.println("map j: " + j);
		a1.setS(value.toString());
		a2.setS(value.toString());
		System.out.println(getTitle("http://news.163.com/14/1211/18/AD746G3F00014JB5.html"));
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		System.out.println("cleanup text1: " + text1);
		System.out.println("cleanup text2: " + text2);
		System.out.println("cleanup i: " + i);
		System.out.println("cleanup j: " + j);
		System.out.println("cleanup a1: " + a1.getS());
		System.out.println("cleanup a2: " + a2.getS());
	}
	
	
	
	public String getTitle(String URL) {
		try {
			URL url = new URL(URL);
			URLConnection urlcon = url.openConnection();
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "gbk");
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(isr);
			String tmps = null;
			int start = -1;
			int end = -1;
			int startz = -1;
			while ((tmps = br.readLine()) != null) {
				if (startz >= 0) {
					sb.append(tmps);
				} else {
					// 开始找title
					start = tmps.indexOf("<title>");
					end = tmps.indexOf("/title>");
					// System.out.println(tmps);
					if (start >= 0) {
						// 找到
						if (end >= 0) {
							return tmps.substring(start + 7, end - 1);// 防止乱码
						}
						sb.append(tmps);
						startz = start;
						continue;
					}// 没找到
					continue;
				}
				// 找 /title
				end = sb.indexOf("/title>");
				if (end >= 0) {// 找到
					startz = sb.indexOf("<title>") + 7;
					return sb.substring(startz, end - 1);
				}
				// 没找到
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "";
	}
}

class TestReducer extends Reducer<Text, Text, Text, Text> {
	
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
//		System.out.println(getTitle("http://news.163.com/14/1211/08/AD61455P00014AEE.html"));
	}
}

class A {
	
	private String t = null;
	
	public A(String s) {
		System.out.println("construct A: " + s);
	}
	
	public void setS(String t) {
		System.out.println("t: " + t);
		this.t = t;
	}
	
	public String getS() {
		return t;
	}
}
