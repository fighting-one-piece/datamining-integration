package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGiniWritable;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeWritable;

public class CalculateGiniMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(CalculateGiniMR.class);
		
		job.setMapperClass(CalculateGiniMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(AttributeWritable.class);
		
		job.setReducerClass(CalculateGiniReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(AttributeGiniWritable.class);
		
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
			configuration.set("mapred.job.queue.name", "q_hudong");
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

class CalculateGiniMapper extends Mapper<LongWritable, Text, Text, AttributeWritable> {

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
			context.write(new Text(entry[0]), new AttributeWritable(id,
					category, entry[1]));
		}
		if (isCategory) {
			context.write(new Text(category), new AttributeWritable(id,
					category, category));
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
	}
}

class CalculateGiniReducer extends Reducer<Text, AttributeWritable, Text, AttributeGiniWritable> {

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
	}

	@Override
	protected void reduce(Text key, Iterable<AttributeWritable> values,
			Context context) throws IOException, InterruptedException {
		String attributeName = key.toString();
		double totalNum = 0.0;
		Map<String, Map<String, Integer>> attrValueSplits = new HashMap<String, Map<String, Integer>>();
		Set<String> splitPoints = new HashSet<String>();
		Iterator<AttributeWritable> iterator = values.iterator();
		boolean isCategory = false;
		while (iterator.hasNext()) {
			AttributeWritable attribute = iterator.next();
			String attributeValue = attribute.getAttributeValue();
			if (attributeName.equals(attributeValue)) {
				isCategory = true;
				break;
			}
			splitPoints.add(attributeValue);
			Map<String, Integer> attrValueSplit = attrValueSplits
					.get(attributeValue);
			if (null == attrValueSplit) {
				attrValueSplit = new HashMap<String, Integer>();
				attrValueSplits.put(attributeValue, attrValueSplit);
			}
			String category = attribute.getCategory();
			Integer categoryNum = attrValueSplit.get(category);
			attrValueSplit.put(category, null == categoryNum ? 1
					: categoryNum + 1);
			totalNum++;
		}
		if (isCategory) {
			System.out.println("is Category");
			double initValue = 1.0;
			iterator = values.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				initValue = initValue / 2;
			}
			context.write(key, new AttributeGiniWritable(attributeName,
					initValue, true, null));
		} else {
			String minSplitPoint = null;
			double minSplitPointGini = 1.0;
			for (String splitPoint : splitPoints) {
				double splitPointGini = 0.0;
				double splitAboveNum = 0.0;
				double splitBelowNum = 0.0;
				Map<String, Integer> attrBelowSplit = new HashMap<String, Integer>();
				for (Map.Entry<String, Map<String, Integer>> entry : attrValueSplits
						.entrySet()) {
					String attrValue = entry.getKey();
					Map<String, Integer> attrValueSplit = entry.getValue();
					if (splitPoint.equals(attrValue)) {
						for (Integer v : attrValueSplit.values()) {
							splitAboveNum += v;
						}
						double aboveGini = 1.0;
						for (Integer v : attrValueSplit.values()) {
							aboveGini -= Math.pow((v / splitAboveNum), 2);
						}
						splitPointGini += (splitAboveNum / totalNum)
								* aboveGini;
					} else {
						for (Map.Entry<String, Integer> e : attrValueSplit
								.entrySet()) {
							String k = e.getKey();
							Integer v = e.getValue();
							Integer count = attrBelowSplit.get(k);
							attrBelowSplit.put(k, null == count ? v : v + count);
							splitBelowNum += e.getValue();
						}
					}
				}
				double belowGini = 1.0;
				for (Integer v : attrBelowSplit.values()) {
					belowGini -= Math.pow((v / splitBelowNum), 2);
				}
				splitPointGini += (splitBelowNum / totalNum) * belowGini;
				if (minSplitPointGini > splitPointGini) {
					minSplitPointGini = splitPointGini;
					minSplitPoint = splitPoint;
				}
			}
			context.write(key, new AttributeGiniWritable(key.toString(),
					minSplitPointGini, false, minSplitPoint));
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
	}

}