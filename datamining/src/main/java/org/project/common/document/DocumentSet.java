package org.project.common.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentSet {

	/** 数据集*/
	private List<Document> documents = null;
	/** 训练数据集*/
	private List<Document> trainDocuments = null;
	/** 测试数据集*/
	private List<Document> testDocuments = null;
	/** 属性*/
	private Map<String, Object> properties = null;
	
	public List<Document> getDocuments() {
		if (null == documents) {
			documents = new ArrayList<Document>();
		}
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public List<Document> getTrainDocuments() {
		if (null == trainDocuments) {
			trainDocuments = new ArrayList<Document>();
		}
		return trainDocuments;
	}

	public void setTrainDocuments(List<Document> trainDocuments) {
		this.trainDocuments = trainDocuments;
	}

	public List<Document> getTestDocuments() {
		if (null == testDocuments) {
			testDocuments = new ArrayList<Document>();
		}
		return testDocuments;
	}

	public void setTestDocuments(List<Document> testDocuments) {
		this.testDocuments = testDocuments;
	}
	
	public Map<String, Object> getProperties() {
		if (null == properties) {
			properties = new HashMap<String, Object>();
		}
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public void setProperty(String key, Object value) {
		getProperties().put(key, value);
	}

	public int docLength() {
		return documents.size();
	}
	
	/** 词集*/
	public static final String WORDS = "words";
	
	public String[] getWords() {
		return (String[]) getProperties().get(WORDS);
	}

	public void setWords(String[] words) {
		getProperties().put(WORDS, words);
	}
	
	/** 前后适应度*/
	public static final String MAX_FIT_PRE = "max_fit_pre";
	public static final String MAX_FIT_POST = "max_fit_post";

	public void setMaxFit(double value) {
		getProperties().put(MAX_FIT_PRE, getProperties().get(MAX_FIT_POST));
		getProperties().put(MAX_FIT_POST, value);
	}
	
	public double getPreMaxFit() {
		return (Double) getProperties().get(MAX_FIT_PRE);
	}
	
	public void setPreMaxFit(double value) {
		getProperties().put(MAX_FIT_PRE, value);
	}

	public double getPostMaxFit() {
		return (Double) getProperties().get(MAX_FIT_POST);
	}

	public void setPostMaxFit(double value) {
		getProperties().put(MAX_FIT_POST, value);
	}
	
	/** 全局特征选择*/
	public static final String FEATURE_SELECT = "feature_select";
	/** 局部特征选择*/
	public static final String FEATURE_SELECT_LOCAL = "feature_select_local";
	
	@SuppressWarnings("unchecked")
	public Map<String, Double> getFeatureSelect() {
		Map<String, Double> featureSelect = (Map<String, Double>) 
				getProperties().get(FEATURE_SELECT);
		if (null == featureSelect) {
			featureSelect = new HashMap<String, Double>();
			setFeatureSelect(featureSelect);		
		}
		return featureSelect;
	}

	public void setFeatureSelect(Map<String, Double> featureSelect) {
		getProperties().put(FEATURE_SELECT, featureSelect);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Double>> getFeatureSelectLocal() {
		Map<String, Map<String, Double>> featureSelect = (Map<String, Map<String, Double>>) 
				getProperties().get(FEATURE_SELECT_LOCAL);
		if (null == featureSelect) {
			featureSelect = new HashMap<String, Map<String, Double>>();
			setFeatureSelectLocal(featureSelect);		
		}
		return featureSelect;
	}

	public void setFeatureSelectLocal(Map<String, Map<String, Double>> featureSelect) {
		getProperties().put(FEATURE_SELECT_LOCAL, featureSelect);
	}
	
	/** 词数量映射*/
	public static final String WORD_TO_COUNT = "word_to_count"; 

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getWordToCount() {
		Map<String, Integer> wordToCount = (Map<String, Integer>) 
				getProperties().get(WORD_TO_COUNT);
		if (null == wordToCount) {
			wordToCount = new HashMap<String, Integer>();
			setWordToCount(wordToCount);
		}
		return wordToCount;
	}

	public void setWordToCount(Map<String, Integer> wordToCount) {
		getProperties().put(WORD_TO_COUNT, wordToCount);
	}

	/** 词索引映射*/
	public static final String WORD_TO_INDEX = "word_2_index"; 
	
	@SuppressWarnings("unchecked")
	public Map<String, Integer> getWordToIndex() {
		Map<String, Integer> wordToIndex = (Map<String, Integer>) 
				getProperties().get(WORD_TO_INDEX);
		if (null == wordToIndex) {
			wordToIndex = new HashMap<String, Integer>();
			setWordToIndex(wordToIndex);
		}
		return wordToIndex;
	}

	public void setWordToIndex(Map<String, Integer> wordToIndex) {
		getProperties().put(WORD_TO_INDEX, wordToIndex);
	}

	

}
