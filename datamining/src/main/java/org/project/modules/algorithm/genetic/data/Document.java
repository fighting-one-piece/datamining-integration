package org.project.modules.algorithm.genetic.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 文档文章*/
public class Document {
	/** 文件名称*/
	private String name = null;
	/** 文章所属类型*/
	private String category = null;
	/** 文章分词*/
	private String[] words = null;
	/** 文章分词向量*/
	private double[] wordsVec = null;
	/** 词语计算TFIDF*/
	private Map<String, Double> tfidfWords = null;
	/** 文章相似度*/
	private List<DocumentSimilarity> similarities = null;
	/** 适应率*/
	private double fit = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String[] getWords() {
		return words;
	}
	
	public Set<String> getWordSet() {
		Set<String> wordSet = new HashSet<String>();
		wordSet.addAll(Arrays.asList(words));
		return wordSet;
	}

	public void setWords(String[] words) {
		this.words = words;
	}
	
	public double[] getWordsVec() {
		return wordsVec;
	}

	public void setWordsVec(double[] wordsVec) {
		this.wordsVec = wordsVec;
	}
	
	public Map<String, Double> getTfidfWords() {
		if (null == tfidfWords) {
			tfidfWords = new HashMap<String, Double>();
		}
		return tfidfWords;
	}

	public void setTfidfWords(Map<String, Double> tfidfWords) {
		this.tfidfWords = tfidfWords;
	}
	
	public List<DocumentSimilarity> getSimilarities() {
		if (null == similarities) {
			similarities = new ArrayList<DocumentSimilarity>();
		}
		return similarities;
	}

	public void setSimilarities(List<DocumentSimilarity> similarities) {
		this.similarities = similarities;
	}

	public double getFit() {
		return fit;
	}

	public void setFit(double fit) {
		this.fit = fit;
	}
	
	

}
