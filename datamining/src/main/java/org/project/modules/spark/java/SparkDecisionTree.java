package org.project.modules.spark.java;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.configuration.Algo;
import org.apache.spark.mllib.tree.configuration.QuantileStrategy;
import org.apache.spark.mllib.tree.configuration.Strategy;
import org.apache.spark.mllib.tree.impurity.Gini;
import org.apache.spark.mllib.tree.impurity.Impurity;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.rdd.RDD;

import scala.Tuple2;

public class SparkDecisionTree {

	@SuppressWarnings("serial")
	public static void main(String[] args) {
		if (args.length != 2) {
			System.exit(1);
		}
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("Spark Decision Tree");
		JavaSparkContext context = new JavaSparkContext(sparkConf);
		JavaRDD<String> lines = context.textFile(args[0], 1);
		
		JavaRDD<LabeledPoint> labeledPoints = lines
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
		
		RDD<LabeledPoint> rddLabeledPoints = JavaRDD.toRDD(labeledPoints);
		JavaRDD<LabeledPoint> labeledPoints1 = rddLabeledPoints.toJavaRDD();
		System.out.println("------");
		System.out.println("JavaRDD: " + labeledPoints.take(1).getClass());
		System.out.println("JavaRDD: " + labeledPoints.take(1).get(0));
		System.out.println("JavaRDD: " + labeledPoints.take(1).get(0).getClass());
		System.out.println("RDD: " + rddLabeledPoints.take(1).getClass());
		System.out.println("JavaRDD1: " + labeledPoints1.take(1).getClass());
		System.out.println("JavaRDD1: " + labeledPoints1.take(1).get(0));
		System.out.println("JavaRDD1: " + labeledPoints1.take(1).get(0).getClass());
		System.out.println("------");
		
		Strategy strategy = new Strategy(Algo.Classification(), new Impurity() {
			@Override
			public double calculate(double arg0, double arg1, double arg2) {
				return Gini.calculate(arg0, arg1, arg2);
			}

			@Override
			public double calculate(double arg0, double arg1) {
				return Gini.calculate(arg0, arg1);
			}
		}, 5, 100, QuantileStrategy.Sort(), null, 256);
		DecisionTree decisionTree = new DecisionTree(strategy);
		final DecisionTreeModel decisionTreeModel = decisionTree.train(rddLabeledPoints);
		System.out.println("id: " + decisionTreeModel.topNode().id());
		System.out.println("isLeaf: " + decisionTreeModel.topNode().isLeaf());

		JavaRDD<Tuple2<Double, Double>> predictions = labeledPoints
				.map(new Function<LabeledPoint, Tuple2<Double, Double>>() {
					@Override
					public Tuple2<Double, Double> call(LabeledPoint labeledPoint)
							throws Exception {
						double predict = decisionTreeModel.predict(labeledPoint
								.features());
						return new Tuple2<Double, Double>(labeledPoint.label(),
								predict);
					}
				});

		predictions.saveAsTextFile(args[1]);

		JavaRDD<Tuple2<Double, Double>> errors = predictions
				.filter(new Function<Tuple2<Double, Double>, Boolean>() {
					@Override
					public Boolean call(Tuple2<Double, Double> v1)
							throws Exception {
						return v1._1() != v1._2();
					}
				});
		
		double error_count = Double.parseDouble(String.valueOf(errors.count()));
		double all_count = Double.parseDouble(String.valueOf(labeledPoints.count()));
		System.out.println("statistics all: " + all_count + "error: " + error_count);
		double error_ratio = error_count / all_count;
		System.out.println("error ratio: " + error_ratio);
		context.stop();
	}
}
