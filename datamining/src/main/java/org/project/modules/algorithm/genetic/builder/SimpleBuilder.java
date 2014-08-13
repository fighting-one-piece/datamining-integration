package org.project.modules.algorithm.genetic.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.project.modules.algorithm.genetic.data.DataLoader;
import org.project.modules.algorithm.genetic.data.DataSet;
import org.project.modules.algorithm.genetic.data.Document;
import org.project.utils.DistanceUtils;
import org.project.utils.RandomUtils;

public class SimpleBuilder {
	
	private Logger logger = Logger.getLogger(SimpleBuilder.class);
	
	//迭代次数
	public static int ITER = 5;
	//染色体长度
	public static int LEN = 150;
	//交叉概率
	public static double PC = 0.5;
	//突变概率
	public static double PM = 0.01;
	//终止概率
	public static double PT = 0.012;
	
	
	//加载文本集合
	private DataSet loadDocuments() {
		DataSet dataSet = null;
		try {
			String path = DataLoader.class.getClassLoader().getResource("测试").toURI().getPath();
			dataSet = DataLoader.load(path);
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}
		return dataSet;
	}
	
	//建立初始化种群
	private void buildInitialPopulation(DataSet dataSet) {
		Set<String> allWords = new HashSet<String>();
		for(Document doc : dataSet.getDocs()) {
			allWords.addAll(doc.getWordSet());
		}
		List<String> words = new ArrayList<String>(allWords);
		int all_words_len = allWords.size();
		System.out.println("all word len: " + all_words_len);
		LEN = all_words_len * 2 / 3;
		System.out.println("init word len: " + LEN);
		String[] rWords = new String[LEN];
		for (int i = 0, len = words.size(); i < LEN; i++) {
			rWords[i] = words.get(RandomUtils.nextInt(len));
		}
		dataSet.setWords(rWords);
		for(Document doc : dataSet.getDocs()) {
			double[] wordsVec = new double[LEN];
			Set<String> wordSet = doc.getWordSet();
			for (int i = 0, len = rWords.length; i < len; i++) {
				wordsVec[i] = wordSet.contains(rWords[i]) ? 1 : 0;
			}
 			doc.setWordsVec(wordsVec);
		}
	}
	
	//计算适应度
	public void calculateFit(DataSet dataSet) {
		double maxFit = 0;
		List<Document> docs = dataSet.getDocs();
		for (Document doc : docs) {
			double[] wordsVec = doc.getWordsVec();
			double sum = 0;
			for (Document odoc : docs) {
				double[] owordsVec = odoc.getWordsVec();
				double distance = DistanceUtils.euclidean(wordsVec, owordsVec);
				sum += distance;
			}
			double f = sum / docs.size();
			double oneNum = 0;
			for (double wordVec : wordsVec) {
				if (wordVec == 1) oneNum += 1;
			}
			double p = oneNum / LEN;
			double fit = 1 / (f + p + 0.1);
			doc.setFit(fit);
			if (fit > maxFit) maxFit = fit;
		}
		if (maxFit > 0) dataSet.setMaxFit(maxFit);
	}
	
	//选择算子
	public DataSet selection(DataSet dataSet) {
		List<Document> docs = dataSet.getDocs();
		double fitSum = 0;
		for (Document doc : docs) {
			fitSum += doc.getFit();
		}
		double accumulationP = 0;
		for (Document doc : docs) {
			double selectionP = doc.getFit() / fitSum;
			doc.setSelectionP(selectionP);
			accumulationP += selectionP;
			doc.setAccumulationP(accumulationP);
		}
		int rlen = docs.size();
		double[] rnum = new double[rlen];
		for (int i = 0; i < rlen; i++) {
			rnum[i] = RandomUtils.nextDouble();
		}
		Arrays.sort(rnum);
		DataSet newDataSet = new DataSet();
		int aIndex = 0;
		int nIndex = 0;
		while (nIndex < rlen) {
			if (rnum[nIndex] < docs.get(aIndex).getAccumulationP()) {
				newDataSet.getDocs().add(docs.get(aIndex));
				nIndex += 1;
			} else {
				aIndex += 1;
			}
		}
		newDataSet.setWords(dataSet.getWords());
		newDataSet.setPostMaxFit(dataSet.getPostMaxFit());
		newDataSet.setPreMaxFit(dataSet.getPreMaxFit());
		return newDataSet;
	}
	
	//交叉算子
	public void crossover(DataSet dataSet) {
		List<Document> docs = dataSet.getDocs();
		for (int i = 0, len = docs.size(); i < len; i = i + 2) {
			if (RandomUtils.nextDouble() > PC || i >= len) {
				continue;
			}
			double[] a = docs.get(i).getWordsVec();
			double[] b = docs.get(i + 1).getWordsVec();
			int x = RandomUtils.nextInt(LEN);
			int y = RandomUtils.nextInt(LEN);
			int start = x > y ? y : x;
			int end = x > y ? x : y;
			System.out.println("start: " + start + " end: " + end);
			for (; start < end; start++) {
				a[start] = a[start] + b[start];
				b[start] = a[start] - b[start];
				a[start] = a[start] - b[start];
			}
		}
	}
	
	//变异算子
	public void mutation(DataSet dataSet) {
		List<Document> docs = dataSet.getDocs();
		for (Document doc : docs) {
			if (RandomUtils.nextDouble() > PM) {
				continue; 
			}
			System.out.println("mutation occur");
			double[] wordsVec = doc.getWordsVec();
			for (int i = 0; i < 5; i++) {
				int index = RandomUtils.nextInt(LEN);
				wordsVec[index] = wordsVec[index] == 1 ? 0 : 1;
			}
		}
	}
	
	//计算终止概率
	public double calculatePT(DataSet dataSet) {
		double post = dataSet.getPostMaxFit();
		double pre = dataSet.getPreMaxFit();
		if (post == 0) return 1;
		System.out.println(post + ":" + pre);
		System.out.println("pt: " + (post - pre) / post);
		return (post - pre) / post;
	}
	
	public void print(DataSet dataSet) {
		List<Document> docs = dataSet.getDocs();
		String[] words = dataSet.getWords();
		Set<String> names = new HashSet<String>();
		for (Document doc : docs) {
			String name = doc.getName();
			if (names.contains(name)) {
				continue;
			}
			names.add(name);
			System.out.println(name + " : " + doc.getCategory());
			int sum = 0;
			double[] wordsVec = doc.getWordsVec();
			for (int i = 0, len = wordsVec.length; i < len; i++) {
				if (wordsVec[i] == 1) {
					System.out.print(words[i] + "\t");
					sum += 1;
				}
			}
			System.out.println();
			System.out.println("total word: " + sum);
		}
	}

	public void build() {
		DataSet dataSet = loadDocuments();
		buildInitialPopulation(dataSet);
//		for (int i = 0; i < ITER; i++) {
		while (calculatePT(dataSet) >= PT) {
			calculateFit(dataSet);
			dataSet = selection(dataSet);
			crossover(dataSet);
			mutation(dataSet);
			System.out.println("--------------------------");
		}
		print(dataSet);
	}
	
	public static void main(String[] args) {
		new SimpleBuilder().build();
	}
}
