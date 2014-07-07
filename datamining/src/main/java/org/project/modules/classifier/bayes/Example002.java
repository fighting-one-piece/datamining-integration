package org.project.modules.classifier.bayes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 伯努利模型*/
public class Example002 {

	private static Map<String[], String> docs = null;

	private static int total_word_number = 0;

	private static Set<String> filter_words = null;

	private static Set<String> filter_categories = null;

	static {
		docs = new HashMap<String[], String>();
		docs.put(new String[] { "chinese", "beijing", "chinese" }, "yes");
		docs.put(new String[] { "chinese", "chinese", "shanghai" }, "yes");
		docs.put(new String[] { "chinese", "guangdong" }, "yes");
		docs.put(new String[] { "tokyo", "japan", "chinese" }, "no");

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
		System.out.println("filter_categories number: "
				+ filter_categories.size());
	}

	
}
