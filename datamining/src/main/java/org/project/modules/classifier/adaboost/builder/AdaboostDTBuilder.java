package org.project.modules.classifier.adaboost.builder;

import java.net.URISyntaxException;

import org.project.modules.classifier.decisiontree.DecisionTree;
import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.utils.ShowUtils;

public class AdaboostDTBuilder {
	
	public static String TRAIN_PATH = null;
	
	public static String TEST_PATH = null;
	
	static {
		try {
			TRAIN_PATH = AdaboostDTBuilder.class.getClassLoader().getResource("trainset/decisiontree.txt").toURI().getPath();
			TEST_PATH = AdaboostDTBuilder.class.getClassLoader().getResource("testset/decisiontree.txt").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Builder treeBuilder = new DecisionTreeC45Builder();
		DecisionTree decisionTree = new DecisionTree(TRAIN_PATH, TEST_PATH, treeBuilder);
		Object[] results = decisionTree.run();
		ShowUtils.printToConsole(results);
	}
	
}
