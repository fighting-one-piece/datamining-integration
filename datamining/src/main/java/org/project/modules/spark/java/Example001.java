package org.project.modules.spark.java;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class Example001 {

	public static void main(String[] args) {
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("example001");
		sparkConf.setMaster("spark://centos.host1:7077");
//		sparkConf.setSparkHome("D:\\develop\\data\\spark\\spark-1.0.0-bin-hadoop1");
//		sparkConf.set("SPARK_WORKER_CORES", "1");
//		String[] jars = new String[]{"D:\\develop\\slf4j\\slf4j-1.7.7\\slf4j-api-1.7.7.jar"
//				, "D:\\develop\\slf4j\\slf4j-1.7.7\\slf4j-log4j12-1.7.7.jar"};
//		sparkConf.setJars(jars);
		JavaSparkContext context = new JavaSparkContext(sparkConf);
//		JavaSparkContext.jarOfClass(Example001.class);
		JavaRDD<String> lines = context.textFile("hdfs://centos.host1:9000/user/hadoop/data/dt/004/input/tree_data.csv", 1);
		for (String line : lines.collect()) {
			System.out.println(line);
		}
		
		List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
		JavaRDD<Integer> distData = context.parallelize(data);
		for (int d : distData.collect()) {
			System.out.println(d);
		}
		
//		Seq<String> sargs = null;
//		SparkSubmitArguments arguments = new SparkSubmitArguments(sargs);
//		SparkSubmit.main(new String[]{});
		
		context.stop();
	}
}
