package org.project.modules.hadoop.mr.a;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class PretreatmentDriver {

	public static void main(String[] args) throws Exception {
		/**
		 * JobConf：map/reduce的job配置类，向hadoop框架描述map-reduce执行的工作
		 * 构造方法：JobConf()、JobConf(Class exampleClass)、JobConf(Configuration
		 * conf)等
		 */
		JobConf conf = new JobConf(PretreatmentDriver.class);
		conf.setJobName("pretreatment"); // 设置一个用户定义的job名称
		conf.setOutputKeyClass(Text.class); // 为job的输出数据设置Key类
		conf.setOutputValueClass(Text.class); // 为job输出设置value类
		conf.setMapperClass(PretreatmentMap.class); // 为job设置Mapper类
		// conf.setCombinerClass(PretreatmentReduce.class); //为job设置Combiner类
		conf.setReducerClass(PretreatmentReduce.class); // 为job设置Reduce类
		conf.setInputFormat(TextInputFormat.class); // 为map-reduce任务设置InputFormat实现类
		conf.setOutputFormat(TextOutputFormat.class); // 为map-reduce任务设置OutputFormat实现类
		/**
		 * InputFormat描述map-reduce中对job的输入定义 setInputPaths():为map-reduce
		 * job设置路径数组作为输入列表 setInputPath()：为map-reduce job设置路径数组作为输出列表
		 */
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		JobClient.runJob(conf); // 运行一个job
	}
}

class PretreatmentMap extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {

	private Text key1 = new Text();
	private Text value1 = new Text();
	private Text key2 = new Text();
	private Text value2 = new Text();

	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		StringBuilder out = new StringBuilder("out:");
		StringBuilder in = new StringBuilder("in:");
		String line = value.toString();
		String result[] = line.split("\\s");
		StringBuilder from = new StringBuilder();
		StringBuilder to = new StringBuilder();

		from.append("<");
		from.append(result[0]);
		from.append(">");
		out.append(result[1]);
		key1.set(from.toString());
		value1.set(out.toString());
		// System.out.println(from+","+out);
		System.out.println(key1.toString() + "," + value1.toString());
		output.collect(key1, value1);

		to.append("<");
		to.append(result[1]);
		to.append(">");
		in.append(result[0]);
		key2.set(to.toString());
		value2.set(in.toString());
		// System.out.println(to+","+in);
		System.out.println(key2.toString() + "," + value2.toString());
		output.collect(key2, value2);

	}

}

class PretreatmentReduce extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {

	public void reduce(Text _key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// process values
		// System.out.println("Reduce!");
		StringBuilder in = new StringBuilder("in:");
		StringBuilder out = new StringBuilder("out:");

		while (values.hasNext()) {
			// System.out.println("pause");
			String value = values.next().toString();
			// System.out.println(value);
			String[] result = value.split("\\:");
			// System.out.println(result[0]+":"+result[1]);
			if (result[0].equals("in")) {
				in.append(result[1]);
				in.append(",");
			} else if (result[0].equals("out")) {
				out.append(result[1]);
				out.append(",");
			}
		}
		in.deleteCharAt(in.length() - 1);
		out.deleteCharAt(out.length() - 1);
		StringBuilder end = new StringBuilder();
		end.append(in.toString());
		end.append("|");
		end.append(out.toString());
		System.out.println(end.toString());
		output.collect(_key, new Text(end.toString()));
	}

}
