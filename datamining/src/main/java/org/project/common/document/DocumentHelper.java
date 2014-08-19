package org.project.common.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		Map<String, Integer> map = wordsInDocStatistics(document);
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
	public static String[] topNWordsInDoc(Document document, int n) {
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
//			DecimalFormat df4  = new DecimalFormat("##.0000");
//			System.out.println(df4.format(entry.getValue()));
		}
		return topWords;
	}
	
	/**
	 * 文档中词统计
	 * @param document
	 * @return
	 */
	public static Map<String, Integer> wordsInDocStatistics(Document document) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String word : document.getWords()) {
			Integer count = map.get(word);
			map.put(word, null == count ? 1 : count + 1);
		}
		return map;
	}
	
	/**
	 * 文档集中词统计
	 * @param documents
	 * @return
	 */
	public static Map<String, Integer> wordsInDocsStatistics(List<Document> documents) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Document document : documents) {
			for (String word : document.getWords()) {
				Integer count = map.get(word);
				map.put(word, null == count ? 1 : count + 1);
			}
		}
		return map;
	}
	
	/**
	 * 文档集中包含指定类型的文档集
	 * @param category
	 * @param documents
	 * @return
	 */
	public static List<Document> categoryInDocs(String category, List<Document> documents) {
		List<Document> docs = new ArrayList<Document>();
		for (Document document : documents) {
			if (category.equalsIgnoreCase(document.getCategory())) {
				docs.add(document);
			}
		}
		return docs;
	}
	
	/**
	 * 文档集中包含指定类型的文档集统计
	 * @param category
	 * @param documents
	 * @return
	 */
	public static int categoryInDocsStatistics(String category, List<Document> documents) {
		int sum = 0;
		for (Document document : documents) {
			if (category.equalsIgnoreCase(document.getCategory())) {
				sum += 1;
			}
		}
		return sum;
	}
	
	/**
	 * 文档集中类型
	 * @param documents
	 * @return
	 */
	public static Set<String> categoriesInDocs(List<Document> documents) {
		Set<String> categories = new HashSet<String>();
		for (Document document : documents) {
			categories.add(document.getCategory());
		}
		return categories;
	}
	
	/**
	 * 文档集中类型数量统计
	 * @param document
	 * @return
	 */
	public static Map<String, Integer> categoriesInDocsStatistics(List<Document> documents) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Document document : documents) {
			String category = document.getCategory();
			Integer count = map.get(category);
			map.put(category, null == count ? 1 : count + 1);
		}
		return map;
	}
	
	/**
	 * 词所在文档集
	 * @param word
	 * @param documents
	 * @return
	 */
	public static List<Document> wordInDocs(String word, List<Document> documents) {
		List<Document> docs = new ArrayList<Document>();
		for (Document document : documents) {
			if (docHasWord(document, word)) {
				docs.add(document);
			}
		}
		return docs;
	}

	/**
	 * 词所在文档数量统计
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
	
	/**
	 * 词不在文档集
	 * @param word
	 * @param documents
	 * @return
	 */
	public static List<Document> wordNotInDocs(String word, List<Document> documents) {
		List<Document> docs = new ArrayList<Document>();
		for (Document document : documents) {
			if (!docHasWord(document, word)) {
				docs.add(document);
			}
		}
		return docs;
	}
	
	/**
	 * 词不在文档数量统计
	 * @param word
	 * @param documents
	 * @return
	 */
	public static int wordNotInDocsStatistics(String word, List<Document> documents) {
		int sum = 0;
		for (Document document : documents) {
			if (!docHasWord(document, word)) {
				sum += 1;
			}
		}
		return sum;
	}
	
	/**
	 * 文档集中各个类型词统计
	 * @param documents
	 * @return
	 */
	public static Map<String, Map<String, Integer>> wordsInCategoriesStatistics(List<Document> documents) {
		Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
		for (Document document : documents) {
			String category = document.getCategory();
			Map<String, Integer> cmap = map.get(category);
			if (null == cmap) {
				cmap = new HashMap<String, Integer>();
				map.put(category, cmap);
			}
			for (String word : document.getWords()) {
				Integer count = cmap.get(word);
				cmap.put(word, null == count ? 1 : count + 1);
			}
		}
		return map;
	}
	
	/**
	 * 文档集中各个类型文档统计
	 * @param documents
	 * @return
	 */
	public static Map<String, List<Document>> docsInCategoriesStatistics(List<Document> documents) {
		Map<String, List<Document>> map = new HashMap<String, List<Document>>();
		for (Document document : documents) {
			String category = document.getCategory();
			List<Document> docs = map.get(category);
			if (null == docs) {
				docs = new ArrayList<Document>();
				map.put(category, docs);
			}
			docs.add(document);
		}
		return map;
	}
	
	
	
}
