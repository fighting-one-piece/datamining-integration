package org.project.modules.classifier.adaboost.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.project.modules.classifier.adaboost.data.DataResult;
import org.project.modules.classifier.adaboost.data.DataSet;
import org.project.modules.classifier.adaboost.data.TrainResult;

public class AdaboostBuilder {
	
	public static DataSet initTrainSet() {
		DataSet dataSet = null;
		try {
			String path = AdaboostBuilder.class.getClassLoader().getResource("trainset/adaboost1.txt").toURI().getPath();
			dataSet = initTrainSet(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataSet;
	}
	
	public static DataSet initTrainSet(String path) {
		DataSet dataSet = new DataSet();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(path));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				double[] data = new double[3];
				data[0] = Double.parseDouble(tokenizer.nextToken());
				data[1] = Double.parseDouble(tokenizer.nextToken());
				String txt = tokenizer.nextToken();
				data[2] = Double.parseDouble(txt);
				dataSet.getDatas().add(data);
				dataSet.getCategories().add(Double.parseDouble(txt));
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return dataSet;
	}
	
	public static DataSet initTestSet() {
		DataSet dataSet = null;
		try {
			String path = AdaboostBuilder.class.getClassLoader().getResource("testset/adaboost1.txt").toURI().getPath();
			dataSet = initTestSet(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataSet;
	}
	
	public static DataSet initTestSet(String path) {
		DataSet dataSet = new DataSet();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(path));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				double[] data = new double[2];
				data[0] = Double.parseDouble(tokenizer.nextToken());
				data[1] = Double.parseDouble(tokenizer.nextToken());
				dataSet.getDatas().add(data);
				dataSet.getCategories().add(Double.parseDouble(tokenizer.nextToken()));
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return dataSet;
	}
	
	public void run() {
			DataSet trainSet = initTrainSet();
			List<TrainResult> trainResults = AdaboostClassifier.train(trainSet.getDatas());
			DataSet testSet = initTestSet();
			List<Double> predictedValues = AdaboostClassifier.classify(testSet.getDatas(), trainResults);
			DataResult dataResult = new DataResult();
			dataResult.setDatas(testSet.getDatas());
			dataResult.setActualValues(testSet.getCategories());
			dataResult.setPredictedValues(predictedValues);
			dataResult.statistics();
	}
	
	public static void main(String[] args) {
		new AdaboostBuilder().run();
	}
	
}
