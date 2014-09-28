package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeWritable;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGainWritable;

public class CalculateC45GainRatioMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(CalculateC45GainRatioMR.class);
		
		job.setMapperClass(CalculateC45GainRatioMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(AttributeWritable.class);

		job.setReducerClass(CalculateC45GainRatioReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(AttributeGainWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
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
			Job job = Job.getInstance(configuration, "Decision Tree");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class CalculateC45GainRatioMapper extends Mapper<LongWritable, Text, 
	Text, AttributeWritable> {
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		Long id = Long.parseLong(tokenizer.nextToken());
		String category = tokenizer.nextToken();
		boolean isCategory = true;
		while (tokenizer.hasMoreTokens()) {
			isCategory = false;
			String attribute = tokenizer.nextToken();
			String[] entry = attribute.split(":");
			context.write(new Text(entry[0]), new AttributeWritable(id, category, entry[1]));
		}
		if (isCategory) {
			context.write(new Text(category), new AttributeWritable(id, category, category));
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class CalculateC45GainRatioReducer extends Reducer<Text, AttributeWritable, Text, AttributeGainWritable> {
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
	}
	
	@Override
	protected void reduce(Text key, Iterable<AttributeWritable> values,
			Context context) throws IOException, InterruptedException {
		String attributeName = key.toString();
		double totalNum = 0.0;
		Map<String, Map<String, Integer>> attrValueSplits = 
				new HashMap<String, Map<String, Integer>>();
		Iterator<AttributeWritable> iterator = values.iterator();
		boolean isCategory = false;
		while (iterator.hasNext()) {
			AttributeWritable attribute = iterator.next();
			String attributeValue = attribute.getAttributeValue();
			if (attributeName.equals(attributeValue)) {
				isCategory = true;
				break;
			}
			Map<String, Integer> attrValueSplit = attrValueSplits.get(attributeValue);
			if (null == attrValueSplit) {
				attrValueSplit = new HashMap<String, Integer>();
				attrValueSplits.put(attributeValue, attrValueSplit);
			}
			String category = attribute.getCategory();
			Integer categoryNum = attrValueSplit.get(category);
			attrValueSplit.put(category, null == categoryNum ? 1 : categoryNum + 1);
			totalNum++;
		}
		if (isCategory) {
			System.out.println("is Category");
			int sum = 0;
			iterator = values.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				sum += 1;
			}
			System.out.println("sum: " + sum);
			context.write(key, new AttributeGainWritable(attributeName,
					sum, true, null));
		} else {
			double gainInfo = 0.0;
			double splitInfo = 0.0;
			for (Map<String, Integer> attrValueSplit : attrValueSplits.values()) {
				double totalCategoryNum = 0;
				for (Integer categoryNum : attrValueSplit.values()) {
					totalCategoryNum += categoryNum;
				}
				double entropy = 0.0;
				for (Integer categoryNum : attrValueSplit.values()) {
					double p = categoryNum / totalCategoryNum;
					entropy -= p * (Math.log(p) / Math.log(2));
				}
				double dj = totalCategoryNum / totalNum;
				gainInfo += dj * entropy;
				splitInfo -= dj * (Math.log(dj) / Math.log(2));
			}
			double gainRatio = splitInfo == 0.0 ? 0.0 : gainInfo / splitInfo;
			StringBuilder splitPoints = new StringBuilder();
			for (String attrValue : attrValueSplits.keySet()) {
				splitPoints.append(attrValue).append(",");
			}
			splitPoints.deleteCharAt(splitPoints.length() - 1);
			System.out.println("attribute: " + attributeName);
			System.out.println("gainRatio: " + gainRatio);
			System.out.println("splitPoints: " + splitPoints.toString());
			context.write(key, new AttributeGainWritable(attributeName,
					gainRatio, false, splitPoints.toString()));
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
}