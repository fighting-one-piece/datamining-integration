package org.project.modules.hadoop.mapreduce.a;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class PartitionerTestMR {

	public static void main(String[] args) throws Exception {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error, please input two path. input and output");
				System.exit(2);
			}
			Job job = Job.getInstance(configuration, "计算各省平均收入");
			
			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			job.setJarByClass(PartitionerTestMR.class);
			
			job.setNumReduceTasks(4);
			job.setMapperClass(ProvinceMap.class);
			job.setPartitionerClass(ProvincePartitioner.class);
			job.setCombinerClass(ProvinceCombiner.class);
			job.setReducerClass(ProvinceReduce.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(DoubleWritable.class);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

/**
 * 
 * @author sochin 提取省份 和 年产量 作为 key/value
 */
class ProvinceMap extends Mapper<Object, Text, Text, DoubleWritable> {
	private String line;

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		line = value.toString();
		String[] datas = line.split("\t");
		String province = datas[0];
		double revenue = Double.parseDouble(datas[1]);
		context.write(new Text(province), new DoubleWritable(revenue));
	}

}

/**
 * 
 * @author sochin 按照key 省份 进行分区，给不同的reduce处理
 */
class ProvincePartitioner extends Partitioner<Text, DoubleWritable> {

	//if (partition < 0 || partition >= partitions){
    //      throw new IOException("Illegal partition for " + key + " (" + partition + ")");
    //}
    //当前的partition为1，就是说partitions应该小于或等1才会出现异常了。
	
	@Override
	public int getPartition(Text key, DoubleWritable value, int numPartitions) {
		System.out.println("numPartitions：\t" + numPartitions + "key:\t" + key); 
		if (key.toString().equals("山东"))
			return 1 % numPartitions;
		if (key.toString().equals("河南"))
			return 2 % numPartitions;
		if (key.toString().equals("浙江"))
			return 3 % numPartitions;
		return 0;
	}

}

/**
 * 本地 计算各个省份 各自的收入总和
 * 
 * @author sochin
 * 
 */
class ProvinceCombiner extends
		Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	public void reduce(Text key, Iterable<DoubleWritable> value,
			Context context) throws IOException, InterruptedException {
		double revenueSum = 0.0;
		for (DoubleWritable v : value) {
			revenueSum += v.get();
		}
		context.write(key, new DoubleWritable(revenueSum));
	}
}

/**
 * 计算各省 平均年收入，输出到文件
 * 
 * @author sochin
 * 
 */
class ProvinceReduce extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	public void reduce(Text key, Iterable<DoubleWritable> value,
			Context context) throws IOException, InterruptedException {
		double revenueSum = 0.0, avgSum = 0.0;
		for (DoubleWritable v : value)
			revenueSum += v.get();
		avgSum = revenueSum / 3;
		context.write(key, new DoubleWritable(avgSum));
	}

}
