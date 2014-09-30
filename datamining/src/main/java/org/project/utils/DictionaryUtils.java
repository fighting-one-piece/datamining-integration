package org.project.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class DictionaryUtils {
	
	private static Logger LOG = Logger.getLogger(DictionaryUtils.class);

	//停用词
	private static Set<String> stopWords = null;
	//褒义词 commendatory
	private static Map<String, Double> commendatoryWords = null;
	//贬义词 derogratory
	private static Map<String, Double> derogratoryWords = null;
	//否定词 privative
	private static Map<String, Double> negativeWords = null;
	//副词
	private static Map<String, Double> adverbsWords = null;
	//反问词
	private static Map<String, Double> rhetoricalWords = null;
	//感叹词
	private static Map<String, Double> interjectionWords = null;
	
	static {
		stopWords = initStopDictionary("dic/stopwords/baidu.dic");
		commendatoryWords = initDictionary("dic/emotion/褒义词.txt");
		derogratoryWords = initDictionary("dic/emotion/贬义词.txt");
		negativeWords = initDictionary("dic/emotion/否定词.txt");
		adverbsWords = initDictionary("dic/emotion/副词.txt");
		rhetoricalWords = initDictionary("dic/emotion/反问词.txt");
		interjectionWords = initDictionary("dic/emotion/感叹词.txt");
	}
	
	private DictionaryUtils() {
		
	}
	
	public static Set<String> initStopDictionary(String path) {
		Set<String> words = new HashSet<String>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = DictionaryUtils.class.getClassLoader().getResourceAsStream(path);
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line && !"".equals(line)) {
				words.add(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
		}
		return words;
	}
	
	public static Map<String, Double> initDictionary(String path) {
		Map<String, Double> words = new HashMap<String, Double>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = DictionaryUtils.class.getClassLoader().getResourceAsStream(path);
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line && !"".equals(line)) {
				if (line.indexOf("|") != -1) {
					String[] splits = line.split("\\|");
					words.put(splits[0], Double.parseDouble(splits[1]));
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
		}
		return words;
	}

	public static Set<String> getStopWords() {
		return stopWords;
	}

	public static Map<String, Double> getCommendatoryWords() {
		return commendatoryWords;
	}

	public static Map<String, Double> getDerogratoryWords() {
		return derogratoryWords;
	}

	public static Map<String, Double> getNegativeWords() {
		return negativeWords;
	}

	public static Map<String, Double> getAdverbsWords() {
		return adverbsWords;
	}

	public static Map<String, Double> getRhetoricalWords() {
		return rhetoricalWords;
	}

	public static Map<String, Double> getInterjectionWords() {
		return interjectionWords;
	}
	
}
