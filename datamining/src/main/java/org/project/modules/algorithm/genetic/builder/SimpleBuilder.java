package org.project.modules.algorithm.genetic.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.project.modules.algorithm.genetic.data.DataLoader;
import org.project.modules.algorithm.genetic.data.DataSet;
import org.project.modules.algorithm.genetic.data.Document;
import org.project.modules.algorithm.genetic.data.DocumentHelper;

public class SimpleBuilder {
	
	private Logger logger = Logger.getLogger(SimpleBuilder.class);
	
	private DataSet loadDocuments() {
		DataSet dataSet = null;
		try {
			String path = DataLoader.class.getClassLoader().getResource("新闻").toURI().getPath();
			dataSet = DataLoader.load(path);
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}
		return dataSet;
	}
	
	private void buildInitialPopulation(DataSet dataSet) {
		Set<String> allWords = new HashSet<String>();
		for(Document doc : dataSet.getDocuments()) {
			allWords.addAll(doc.getWordSet());
		}
		Random random = new Random();
		List<String> words = new ArrayList<String>(allWords);
		String[] rWords = new String[100];
		for (int i = 0, len = words.size(); i < 100; i++) {
			rWords[i] = words.get(random.nextInt(len));
		}
		for(Document doc : dataSet.getDocuments()) {
			double[] wordsVec = new double[100];
			Set<String> wordSet = doc.getWordSet();
			for (int i = 0, len = rWords.length; i < len; i++) {
				wordsVec[i] = wordSet.contains(rWords[i]) ? 1 : 0;
			}
 			doc.setWordsVec(wordsVec);
		}
	}

	public void build() {
		DataSet dataSet = loadDocuments();
		buildInitialPopulation(dataSet);
	}
	
	public static void main(String[] args) {
		new SimpleBuilder().build();
	}
}
