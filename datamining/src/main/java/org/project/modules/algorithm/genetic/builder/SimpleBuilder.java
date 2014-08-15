package org.project.modules.algorithm.genetic.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.Document;
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
	public static double PT = 0.05;
	
	
	//加载文本集合
	private DocumentSet loadDocuments() {
		DocumentSet dataSet = null;
		try {
			String path = DocumentLoader.class.getClassLoader().getResource("测试").toURI().getPath();
			dataSet = DocumentLoader.loadDocSet(path);
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}
		return dataSet;
	}
	
	//建立初始化种群，随机选取总词数的2/3为初始词数
	private void buildInitialPopulation(DocumentSet dataSet) {
		Map<String, Integer> wordToCount = new HashMap<String, Integer>();
		for(Document doc : dataSet.getDocs()) {
			for (String word : doc.getWords()) {
				Integer count = wordToCount.get(word);
				wordToCount.put(word, null == count ? 1 : count + 1);
			}
		}
		dataSet.setWordToCount(wordToCount);
		for(Document doc : dataSet.getDocs()) {
			List<String> words = new ArrayList<String>();
			for (String word : doc.getWords()) {
				int count = wordToCount.get(word);
				if (count > 2) {
					words.add(word);
				}
			}
			doc.setWords(words.toArray(new String[0]));
		}
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
		for (int i = 0; i < LEN; i++) {
			rWords[i] = words.get(RandomUtils.nextInt(all_words_len));
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
	
	/**
	  * 计算染色体个体的适应度
	  * 适应度的目标函数采用fit = 1 / (f + p + 0.1)
	  * f 是文本个体与其他文本相似度的均值，这里相似度采用的是欧氏距离或者余弦距离
	  * p 是降维幅度的考察函数，是个体二进制编码中1的个数与总数的比值
	  * 0.1 是防止函数无意义，分母不为0
	 */
	public void calculateFit(DocumentSet dataSet) {
		double maxFit = 0;
		List<Document> docs = dataSet.getDocs();
		for (Document doc : docs) {
			double[] wordsVec = doc.getWordsVec();
			double sum = 0;
			for (Document odoc : docs) {
				double[] owordsVec = odoc.getWordsVec();
//				double distance = DistanceUtils.euclidean(wordsVec, owordsVec);
				double distance = DistanceUtils.cosine(wordsVec, owordsVec);
				System.out.println(distance);
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
	
	/**
	  * 选择算子阶段
	  * 这里主要涉及了几个概念：选择概率、积累概率、轮盘赌选择法
	  * 从父代群体中选取一些个体遗传到下一代群体，返回新群体
	 */
	public DocumentSet selection(DocumentSet dataSet) {
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
		DocumentSet newDataSet = new DocumentSet();
		int aIndex = 0;
		int nIndex = 0;
		while (nIndex < rlen && aIndex < rlen) {
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
		newDataSet.setWordToCount(dataSet.getWordToCount());
		return newDataSet;
	}
	
	/**
	  * 交叉算子阶段
	  * 满足交叉概率的情况下，对两个个体之间进行两点交叉
	  * 即随机两个点，在两点之间的点进行互换
	 */
	public void crossover(DocumentSet dataSet) {
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
	
	/**
	  * 变异算子阶段
	  * 满足变异概率的情况下，对个体随机位数进行变异
	 */
	public void mutation(DocumentSet dataSet) {
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
	
	/**
	  * 终止条件阶段
	  * 这里是自定义的终止条件函数
	  * (PostMaxFit - PreMaxFit) / PostMaxFit
	 */
	public double calculatePT(DocumentSet dataSet) {
		double post = dataSet.getPostMaxFit();
		double pre = dataSet.getPreMaxFit();
		if (post == 0) return 1;
		double pt = (post - pre) / post;
		System.out.println(post + ":" + pre + ":" + pt);
		return pt;
	}
	
	/**
	 * 结果统计 
	 */
	public void statistics(DocumentSet dataSet) {
		Map<String, Integer> wordToCount = dataSet.getWordToCount();
		List<Document> docs = dataSet.getDocs();
		String[] words = dataSet.getWords();
		Set<String> names = new HashSet<String>();
		for (Document doc : docs) {
			String name = doc.getName();
			if (names.contains(name)) {
				continue;
			}
			names.add(name);
			statisticsWord(doc, words, wordToCount);
		}
	}
	
	public void statisticsWord(Document doc, String[] words, Map<String, Integer> wordToCount) {
		System.out.print("doc name: " + doc.getName() + " -- ");
		System.out.print(" category: " + doc.getCategory() + " -- ");
		System.out.println("fit: " + doc.getFit() + " -- ");
		int wordNum = 0;
		double[] wordsVec = doc.getWordsVec();
		for (int i = 0, len = wordsVec.length; i < len; i++) {
			if (wordsVec[i] == 1) {
				wordNum += 1;
				System.out.print(words[i] + "(" +
						wordToCount.get(words[i]) + ")\t");
			}
		}
		System.out.println();
		System.out.println("statistics word num: " + wordNum);
	}

	public void build() {
		DocumentSet dataSet = loadDocuments();
		buildInitialPopulation(dataSet);
//		for (int i = 0; i < ITER; i++) {
		while (calculatePT(dataSet) >= PT) {
			calculateFit(dataSet);
			dataSet = selection(dataSet);
			crossover(dataSet);
			mutation(dataSet);
			System.out.println("--------------------------");
		}
		statistics(dataSet);
	}
	
	public static void main(String[] args) {
		new SimpleBuilder().build();
	}
}
