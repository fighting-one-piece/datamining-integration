package org.project.modules.classifier.adaboost.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.project.modules.classifier.regression.builder.GradientAscentBuilder;
import org.project.modules.classifier.regression.data.DataResult;
import org.project.modules.classifier.regression.data.DataSet;
import org.project.modules.classifier.regression.data.DataSetHandler;

public class AdaboostLRBuilder {

	public static String path = null;
	
	private List<Double> classifierWeights = new ArrayList<Double>();
	
	private List<List<Double>> sampleWeights = new ArrayList<List<Double>>();
	
	static {
		try {
			path = AdaboostLRBuilder.class.getClassLoader().getResource("trainset/regression.txt").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		DataSet dataSet = DataSetHandler.load(path);
		if (sampleWeights.size() == 0) {
			List<Double> weights = new ArrayList<Double>();
			for (int i = 0, len = dataSet.getDatas().length; i < len; i++) {
				weights.add(1.0 / len);
			}
			sampleWeights.add(weights);
		} else {
			
		}
		GradientAscentBuilder gradientAscent = new GradientAscentBuilder();
		DataResult dataResult = gradientAscent.runByData(dataSet);
		int errorNum = dataResult.getErrorNum();
		double errorRate = new Double(errorNum) / dataResult.getTotal();
		System.out.println("errorRate: " + errorRate);
		double alpha = 0.5 * Math.log((1 - errorRate) / errorRate);
		System.out.println("alpha: " + alpha);
		classifierWeights.add(alpha);
	}
	
	public static void main(String[] args) {
		new AdaboostLRBuilder().run();
	}
}
