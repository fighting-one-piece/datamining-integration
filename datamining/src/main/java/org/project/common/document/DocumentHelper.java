package org.project.common.document;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DocumentHelper {
	
	/**
	 * 文档中是否包含词
	 * @param document
	 * @param word
	 * @return
	 */
	public static boolean docHasWord(Document document, String word) {
		for (String temp : document.getWords()) {
			if (temp.equalsIgnoreCase(word)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 词向量化
	 * @param document
	 * @param words
	 * @return
	 */
	public static double[] docWordsVector(Document document, String[] words) {
		double[] vector = new double[words.length];
		Map<String, Integer> map = docWordsStatistics(document);
		int index = 0;
		for (String word : words) {
			Integer count = map.get(word);
			vector[index++] = null == count ? 0 : count;
		}
		return vector;
	}
	
	/**
	 * 文档词计算TFIDF后取前N
	 * @param document
	 * @param n
	 * @return
	 */
	public static String[] docTopNWords(Document document, int n) {
		String[] topWords = new String[n];
		Map<String, Double> tfidfWords = document.getTfidfWords();
		List<Map.Entry<String, Double>> list = 
				new ArrayList<Map.Entry<String, Double>>(tfidfWords.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		int index = 0;
		for (Map.Entry<String, Double> entry : list) {
			if (index == n) {
				break;
			}
			topWords[index++] = entry.getKey();
//			System.out.print(document.getName() + " : " + entry.getKey() + " : ");
			DecimalFormat df4  = new DecimalFormat("##.0000");
			System.out.println(df4.format(entry.getValue()));
		}
		return topWords;
	}
	
	public static String[] mapTopN(Map<String, Double> map, int n) {
		String[] topWords = new String[n];
		List<Map.Entry<String, Double>> list = 
				new ArrayList<Map.Entry<String, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		int index = 0;
		for (Map.Entry<String, Double> entry : list) {
			if (index == n) {
				break;
			}
			topWords[index++] = entry.getKey();
		}
		return topWords;
	}
	
	/**
	 * 文档中词统计
	 * @param document
	 * @return
	 */
	public static Map<String, Integer> docWordsStatistics(Document document) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String word : document.getWords()) {
			Integer count = map.get(word);
			map.put(word, null == count ? 1 : count + 1);
		}
		return map;
	}

	/**
	 * 词所在文档统计
	 * @param word
	 * @param documents
	 * @return
	 */
	public static int wordInDocsStatistics(String word, List<Document> documents) {
		int sum = 0;
		for (Document document : documents) {
			if (docHasWord(document, word)) {
				sum += 1;
			}
		}
		return sum;
	}
}
