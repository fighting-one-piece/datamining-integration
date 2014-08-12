package org.project.modules.algorithm.genetic.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.project.modules.algorithm.genetic.data.DataLoader;
import org.project.modules.algorithm.genetic.data.DataSet;
import org.project.modules.algorithm.genetic.data.Document;
import org.project.modules.algorithm.genetic.data.DocumentSimilarity;
import org.project.utils.DistanceUtils;

public class SimpleBuilder {
	
	private Logger logger = Logger.getLogger(SimpleBuilder.class);
	
	public static int LEN = 150;
	//交叉概率
	public static double PC = 0.5;
	//突变概率
	public static double PM = 0.01;
	
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
		String[] rWords = new String[LEN];
		for (int i = 0, len = words.size(); i < LEN; i++) {
			rWords[i] = words.get(random.nextInt(len));
		}
		for(Document doc : dataSet.getDocuments()) {
			double[] wordsVec = new double[LEN];
			Set<String> wordSet = doc.getWordSet();
			for (int i = 0, len = rWords.length; i < len; i++) {
				wordsVec[i] = wordSet.contains(rWords[i]) ? 1 : 0;
			}
 			doc.setWordsVec(wordsVec);
		}
	}
	
	public void fit(DataSet dataSet) {
		List<Document> docs = dataSet.getDocuments();
		for (Document doc : docs) {
			double[] wordsVec = doc.getWordsVec();
			double sum = 0;
			for (Document odoc : docs) {
				double[] owordsVec = odoc.getWordsVec();
				double distance = DistanceUtils.euclidean(wordsVec, owordsVec);
				DocumentSimilarity docSimilarity = new DocumentSimilarity();
				docSimilarity.setDocName1(doc.getName());
				docSimilarity.setDocName2(odoc.getName());
				docSimilarity.setVector1(wordsVec);
				docSimilarity.setVector2(owordsVec);
				docSimilarity.setDistance(distance);
				doc.getSimilarities().add(docSimilarity);
				sum += distance;
			}
			double f = sum / docs.size();
			System.out.println("f: " + f);
			double oneNum = 0;
			for (double wordVec : wordsVec) {
				if (wordVec == 1) {
					oneNum += 1;
				}
			}
			double p = oneNum / LEN;
			System.out.println("p: " + p);
			double fit = 1 / (f + p + 0.1);
			System.out.println("fit: " + fit);
			doc.setFit(fit);
		}
	}
	
	public void select() {
		
	}
	
	public void crossover() {
		
	}
	
	public void mutation() {
		
	}

	public void build() {
		DataSet dataSet = loadDocuments();
		buildInitialPopulation(dataSet);
		fit(dataSet);
	}
	
	public static void main(String[] args) {
		new SimpleBuilder().build();
	}
}
