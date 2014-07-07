package org.project.modules.spark.java;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

import scala.Tuple2;

public class SparkNativeBayes {

	@SuppressWarnings({"serial" })
	public static void main(String[] args) {
		if (args.length != 2) {
			System.exit(1);
		}
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("Spark Native Bayes");
		JavaSparkContext context = new JavaSparkContext(sparkConf);
		JavaRDD<String> lines = context.textFile(args[0], 1);
		
		JavaRDD<LabeledPoint> training = lines
				.map(new Function<String, LabeledPoint>() {
					@Override
					public LabeledPoint call(String line) throws Exception {
						String[] splits = line.split(",");
						Double label = Double.parseDouble(splits[0]);
						double[] values = new double[splits.length - 1];
						for (int i = 1, len = splits.length; i < len; i++) {
							values[i - 1] = Double.parseDouble(splits[i]);
						}
						Vector features = new DenseVector(values);
						return new LabeledPoint(label, features);
					}
				});
		
		JavaRDD<LabeledPoint> test = lines
				.map(new Function<String, LabeledPoint>() {
					@Override
					public LabeledPoint call(String line) throws Exception {
						String[] splits = line.split(",");
						Double label = Double.parseDouble(splits[0]);
						double[] values = new double[splits.length - 1];
						for (int i = 1, len = splits.length; i < len; i++) {
							values[i - 1] = Double.parseDouble(splits[i]);
						}
						Vector features = new DenseVector(values);
						return new LabeledPoint(label, features);
					}
				});

		final NaiveBayesModel model = NaiveBayes.train(training.rdd(), 1.0);

		JavaRDD<Double> prediction = test
				.map(new Function<LabeledPoint, Double>() {
					@Override
					public Double call(LabeledPoint p) {
						return model.predict(p.features());
					}
				});
		
		JavaPairRDD<Double, Double> predictionAndLabel = prediction.zip(test
				.map(new Function<LabeledPoint, Double>() {
					@Override
					public Double call(LabeledPoint p) {
						return p.label();
					}
				}));
		
		predictionAndLabel.saveAsTextFile(args[1]);
		
		long count = predictionAndLabel.filter(
				new Function<Tuple2<Double, Double>, Boolean>() {
					@Override
					public Boolean call(Tuple2<Double, Double> pl) {
						return pl._1().equals(pl._2());
					}
				}).count();
		
		long all = test.count();
		
		System.out.println("all: " + all + " count: " + count);
		
		double correct_ratio =  Double.parseDouble(String.valueOf(count)) / all;
		
		System.out.println("correct ratio: " + correct_ratio);
	}
}
