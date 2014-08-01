package org.project.modules.classifier.bayes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Example003 {

	private static Map<String[], Integer> datas = null;

	private static int total_word_number = 0;
	
	private static int total_category_number = 0;

	private static Set<String> filter_words = null;

	private static Set<Integer> filter_categories = null;
	
	private static Map<Integer, double[]> trainDatas = null;

	static {
		datas = new HashMap<String[], Integer>();
		datas.put(new String[]{"my", "dog", "has", "flea", "problems", "help", "please"}, 0);
		datas.put(new String[]{"maybe", "not", "take", "him", "to", "dog", "park", "stupid"}, 1);
		datas.put(new String[]{"my", "dalmation", "is", "so", "cute", "I", "love", "him"}, 0);
		datas.put(new String[]{"stop", "posting", "stupid", "worthless", "garbage"}, 1);
		datas.put(new String[]{"mr", "licks", "ate", "my", "steak", "how", "to", "stop", "him"}, 0);
		datas.put(new String[]{"quit", "buying", "worthless", "dog", "food", "stupid"}, 1);
		
		filter_words = new HashSet<String>();
		filter_categories = new HashSet<Integer>();

		for (Map.Entry<String[], Integer> entry : datas.entrySet()) {
			for (String word : entry.getKey()) {
				filter_words.add(word);
				total_word_number += 1;
			}
			filter_categories.add(entry.getValue());
			total_category_number += 1;
		}

		System.out.println("total_word_number number: " + total_word_number);
		System.out.println("total_category_number number: " + total_category_number);
		System.out.println("filter_words number: " + filter_words.size());
		System.out.println("filter_categories number: " + filter_categories.size());
		
	}
	
	public static int[] vector(String[] inputs) {
		int[] vec = new int[filter_words.size()];
		for (String input : inputs) {
			int index = 0;
			for (String word : filter_words) {
				if (input.equals(word)) vec[index] = 1;
				index++;
			}
		}
		return vec;
	}
	
	public static void train() {
		trainDatas = new HashMap<Integer, double[]>();
		trainDatas.put(0, new double[filter_words.size()]);
		trainDatas.put(1, new double[filter_words.size()]);
		double sum0 = 0, sum1 = 0;
		for (Map.Entry<String[], Integer> entry : datas.entrySet()) {
			int category = entry.getValue();
			double[] array = trainDatas.get(category);
			int[] vector = vector(entry.getKey());
			for (int i = 0, len = vector.length; i < len; i++) {
				array[i] += vector[i];
				if (category == 1) {
					sum1 += 1;
				} else {
					sum0 += 1;
				}
			}
		}
		
		for (Map.Entry<Integer, double[]> entry : trainDatas.entrySet()) {
			int category = entry.getKey();
			System.out.println("category: " + category);
			double[] values = entry.getValue();
			print(values);
			for (int i = 0, len = values.length; i < len; i++) {
				if (category == 1) {
					values[i] = values[i] / sum1;
				} else {
					values[i] = values[i] / sum0;
				}
			}
			print(values);
		}
	}
	
	public static Map<Integer, Double> classify(String[] inputs) {
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		int[] vector = vector(inputs);
		for (Map.Entry<Integer, double[]> entry : trainDatas.entrySet()) {
			double[] values = entry.getValue();
			double sum = 0;
			for (int i = 0, len = values.length; i < len; i++) {
				System.out.println(i + " vetcot : " + vector[i] + " values " + values[i]);
				sum += vector[i] * values[i];
			}
			result.put(entry.getKey(), sum);
		}
		return result;
	}
	
	public static void print(double[] array) {
		for (double a : array) {
			System.out.print(a + ",");
		}
		System.out.println();
	}
	
	public static void print(int[] array) {
		for (double a : array) {
			System.out.print(a + ",");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		train();
		String[] example =  new String[]{"love", "my", "dalmation", "stupid", "garbage"};
//		int[] vector = vector(example);
//		print(vector);
		for (Map.Entry<Integer, Double> entry : classify(example).entrySet()) {
			System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
		}
	}
}
