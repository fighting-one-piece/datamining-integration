package org.project.modules.classifier.bayes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 多项式模型*/
public class Example001 {

	private static Map<String[], String> docs = null;
	
	private static int total_word_number = 0;
	
	private static Set<String> filter_words = null;
	
	private static Set<String> filter_categories = null;
	
	static {
		docs = new HashMap<String[], String>();
		docs.put(new String[]{"chinese", "beijing", "chinese"}, "yes");
		docs.put(new String[]{"chinese", "chinese", "shanghai"}, "yes");
		docs.put(new String[]{"chinese", "guangdong"}, "yes");
		docs.put(new String[]{"tokyo", "japan", "chinese"}, "no");
		
		filter_words = new HashSet<String>();
		filter_categories = new HashSet<String>();
		
		for (Map.Entry<String[], String> entry : docs.entrySet()) {
			for (String word : entry.getKey()) {
				filter_words.add(word);
				total_word_number += 1;
			}
			filter_categories.add(entry.getValue());
		}
		
		System.out.println("total_word_number number: " + total_word_number);
		System.out.println("filter_words number: " + filter_words.size());
		System.out.println("filter_categories number: " + filter_categories.size());
	}
	
	public static double obtainTotalNumByCategory(String category) {
		double number = 0;
		for (Map.Entry<String[], String> entry : docs.entrySet()) {
			number = category.equals(entry.getValue()) ? number + entry.getKey().length : number;
		}
		return number;
	}
	
	public static double obtainTotalNumByCategoryAndWord(String category, String word) {
		double number = 0;
		for (Map.Entry<String[], String> entry : docs.entrySet()) {
			if (!category.equals(entry.getValue())) {
				continue;
			}
			for (String w : entry.getKey()) {
				number = word.equals(w) ? number + 1 : number;
			}
		}
		return number;
	}
	
	public static Map<String, Double> classifyByWords(String[] words) {
		Map<String, Double> result = new HashMap<String, Double>();
		for (String category : filter_categories) {
			double c = obtainTotalNumByCategory(category) / total_word_number;
			for (String word : words) {
				double a = obtainTotalNumByCategoryAndWord(category, word) + 1;
				double b = obtainTotalNumByCategory(category) + filter_words.size();
				System.out.println("a: " + a + " b: " + b + " c: " + c);
				c = c == 0 ? (a / b) : c * (a / b);
			}
			System.out.println("category: " + category + " c: " + c);
			result.put(category, c);
		}
		return result;
	}
	
	public static void main(String[] args) {
		String[] words = new String[]{"chinese", "chinese", "japan", "japan", "tokyo"};
		
		Map<String, Double> result = classifyByWords(words);
		for (Map.Entry<String, Double> entry : result.entrySet()) {
			System.out.println("key: " + entry.getKey() + " value: " +entry.getValue());
		}
	}
}
