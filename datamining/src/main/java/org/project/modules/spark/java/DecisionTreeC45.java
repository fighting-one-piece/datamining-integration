package org.project.modules.spark.java;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.configuration.Strategy;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.rdd.RDD;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeWritable;

import scala.Tuple2;

public class DecisionTreeC45 {
	
	@SuppressWarnings("unused")
	private static final Pattern SPACE = Pattern.compile(" ");

	@SuppressWarnings("serial")
	public static void main(String[] args) {
		if (args.length < 1) {
			System.exit(1);
		}
		
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("Decision Tree C4.5 Algorithm");
		JavaSparkContext context = new JavaSparkContext(sparkConf);
		
		JavaRDD<String> lines = context.textFile(args[0], 1);
		
		lines.flatMap(new FlatMapFunction<String, AttributeWritable>() {

			@Override
			public Iterable<AttributeWritable> call(String line) throws Exception {
				StringTokenizer tokenizer = new StringTokenizer(line);
				boolean isCategory = true;
				while (tokenizer.hasMoreTokens()) {
					isCategory = false;
					String attribute = tokenizer.nextToken();
					String[] entry = attribute.split(":");
					System.out.println(entry);
				}
				if (isCategory) {
				}
				return null;
			}
			
		});
		
		JavaPairRDD<String, AttributeWritable> attributes = 
				lines.mapToPair(new PairFunction<String, String, AttributeWritable>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Tuple2<String, AttributeWritable> call(String line)
					throws Exception {
				StringTokenizer tokenizer = new StringTokenizer(line);
				Long id = Long.parseLong(tokenizer.nextToken());
				String category = tokenizer.nextToken();
				boolean isCategory = true;
				Tuple2 tuple2 = null;
				while (tokenizer.hasMoreTokens()) {
					isCategory = false;
					String attribute = tokenizer.nextToken();
					String[] entry = attribute.split(":");
					tuple2 = new Tuple2(entry[0], new AttributeWritable(id, category, entry[1]));
				}
				if (isCategory) {
					tuple2 = new Tuple2(category, new AttributeWritable(id, category, category));
				}
				return tuple2;
			}
		});
		
		attributes.reduceByKey(new Function2<AttributeWritable, AttributeWritable, AttributeWritable>() {

			@Override
			public AttributeWritable call(AttributeWritable v1,
					AttributeWritable v2) throws Exception {
				return null;
			}
		});
		
		attributes.reduce(new Function2<Tuple2<String,AttributeWritable>, Tuple2<String,AttributeWritable>, Tuple2<String,AttributeWritable>>() {
			
			@Override
			public Tuple2<String, AttributeWritable> call(
					Tuple2<String, AttributeWritable> v1,
					Tuple2<String, AttributeWritable> v2) throws Exception {
				return null;
			}
		});
		
		Strategy strategy = null;
		DecisionTree decisionTree = new DecisionTree(strategy);
		RDD<LabeledPoint> input = null;
		DecisionTreeModel decisionTreeModel = decisionTree.train(input);
		decisionTreeModel.topNode();
		
	}
}
