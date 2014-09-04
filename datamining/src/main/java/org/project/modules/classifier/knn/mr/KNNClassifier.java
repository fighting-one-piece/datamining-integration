package org.project.modules.classifier.knn.mr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
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
import org.project.modules.classifier.knn.data.Point;
import org.project.modules.classifier.knn.mr.writable.PointWritable;
import org.project.utils.HDFSUtils;

public class KNNClassifier {
	
	private static void configureJob(Job job) {
		job.setJarByClass(KNNClassifier.class);
		
		job.setMapperClass(KNNMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(PointWritable.class);
		
		job.setReducerClass(KNNReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 4) {
				System.out.println("error, please input three path.");
				System.out.println("1 train set path.");
				System.out.println("2 test set path.");
				System.out.println("3 output path.");
				System.out.println("4 k value.");
				System.exit(2);
			}
			DistributedCache.addCacheFile(new Path(inputArgs[0]).toUri(), configuration);
			
			configuration.set("k", inputArgs[3]);
			Job job = new Job(configuration, "KNN Classifier");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[1]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[2]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
			long end = System.currentTimeMillis();
			System.out.println("spend time: " + (end - start) / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class KNNMapper extends Mapper<LongWritable, Text, Text, PointWritable> {
	
	private List<Point> trainPoints = new ArrayList<Point>();
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		FileSystem fs = FileSystem.get(conf);
		URI[] uris = DistributedCache.getCacheFiles(conf);
		Path[] paths = HDFSUtils.getPathFiles(fs, new Path(uris[0]));
		for(Path path : paths) {
			FSDataInputStream in = fs.open(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (null != line && !"".equals(line)) {
				String[] datas = line.split(" ");
				trainPoints.add(new Point(Double.parseDouble(datas[0]), 
						Double.parseDouble(datas[1]), datas[2]));
				line = reader.readLine();
			}
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] datas = line.split(" ");
		double x = Double.parseDouble(datas[0]);
		double y = Double.parseDouble(datas[1]);
		Point testPoint = new Point(x, y);
		String outputKey = x + "-" + y;
		for (Point trainPoint : trainPoints) {
			double distance = distance(testPoint, trainPoint);
			context.write(new Text(outputKey), new PointWritable(trainPoint, distance));
		}
	}
	
	public double distance(Point point1, Point point2) {
		return Math.sqrt(Math.pow((point1.getX() - point2.getX()), 2)
				+ Math.pow((point1.getY() - point2.getY()), 2));
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class KNNReducer extends Reducer<Text, PointWritable, Text, Text> {

	private int k = 0;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		k = Integer.parseInt(conf.get("k", "0"));
	}

	@Override
	protected void reduce(Text key, Iterable<PointWritable> values,
			Context context) throws IOException, InterruptedException {
		System.out.println(key);
		Map<String, Integer> map = new HashMap<String, Integer>();
		int index = 0;
		for (PointWritable point : values) {
			System.out.println("p: " + point.getX() + "-" + point.getY() + "-" + point.getDistance());
			String category = point.getCategory().toString();
			Integer count = map.get(category);
			map.put(category, null == count ? 1 : count + 1);
			if ((++index) == k) break;
		}
		List<Map.Entry<String, Integer>> list = 
				new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>(){
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		System.out.println("lv: " + list.get(0).getValue());
		context.write(key, new Text(list.get(0).getKey()));
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
	}

}