package org.project.modules.algorithm.lda.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;

public class LDABuilder {

	private int[][] docWordIndex = null;
	
	private int D = 0, T = 0, W = 0;
	
	private int[][] wordTopic = null;
	
	private float alpha = 0.5f, beta = 0.1f;
	
	private int[][] NDT = null;
	
	private int[][] NTW = null;
	
	private int[] NDTSum = null;
	
	private int[] NTWSum = null;
	
	private double[][] theta = null;

	private double[][] phi = null;
	
	private int ITER_NUMBER = 10;
	

	public DocumentSet initDocuments() {
		DocumentSet documentSet = null;
		try {
			String path = LDABuilder.class.getClassLoader().getResource("测试").toURI().getPath();
			documentSet = DocumentLoader.loadDocumentSet(path);
			Map<String, Integer> wordToCount = documentSet.getWordToCount();
			Map<String, Integer> wordToIndex = documentSet.getWordToIndex();
			List<Document> documents = documentSet.getDocuments();
			List<String> allWords = new ArrayList<String>();
			for (Document document : documents) {
				String[] words = document.getWords();
				int[] wordsIndex = new int[words.length];
				for (int i = 0, len = words.length; i < len; i++) {
					String word = words[i];
					if (wordToIndex.containsKey(word)) {
						wordToCount.put(word, wordToCount.get(word) + 1);
						wordsIndex[i] = wordToIndex.get(word);
					} else {
						int index = wordToIndex.size();
						wordToIndex.put(word, index);
						wordToCount.put(word, 1);
						wordsIndex[i] = index;
						allWords.add(word);
					}
				}
				document.setWordsIndex(wordsIndex);
			}
			documentSet.setWords(allWords.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentSet;
	}
	
	public void initParameters(DocumentSet documentSet) {
		D = documentSet.getDocuments().size();
		W = documentSet.getWordToCount().size();
		T = 10;
		NDT = new int[D][T];
		NTW = new int[T][W];
		NDTSum = new int[D];
		NTWSum = new int[T];
		theta = new double[D][T];
		phi = new double[T][W];
		
		List<Document> documents = documentSet.getDocuments();
		docWordIndex = new int[D][];
		for (int i = 0; i < D; i++) {
			int len = documents.get(i).getWordsIndex().length;
			docWordIndex[i] = new int[len];
			for (int j = 0; j < len; j++) {
				docWordIndex[i][j] = documents.get(i).getWordsIndex()[j];
			}
		}
		
		wordTopic = new int[D][];
		for (int i = 0; i < D; i++) {
			int len = documents.get(i).getWordsIndex().length;
			wordTopic[i] = new int[len];
			for (int j = 0; j < len; j++) {
				int initTopic = (int) (Math.random() * T);
				wordTopic[i][j] = initTopic;
				NDT[i][initTopic] += 1;
				NTW[initTopic][docWordIndex[i][j]] += 1;
				NTWSum[initTopic] += 1;
			}
			NDTSum[i] += len;
		}
		
	}
	
	public void inference(DocumentSet documentSet) {
		List<Document> documents = documentSet.getDocuments();
		for (int n = 0; n < ITER_NUMBER; n++) {
			updateEstimatedParameters();
			
			for (int i = 0; i < D; i++) {
				int len = documents.get(i).getWordsIndex().length;
				for (int j = 0; j < len; j++) {
					int newTopic = newTopic(i, j);
					wordTopic[i][j] = newTopic;
				}
			}
		}
	}
	
	private void updateEstimatedParameters() {
		for (int t = 0; t < T; t++) {
			for (int w = 0; w < W; w++) {
				phi[t][w] = (NTW[t][w] + beta) / (NTWSum[t] + W * beta);
			}
		}

		for (int d = 0; d < D; d++) {
			for (int t = 0; t < T; t++) {
				theta[d][t] = (NDT[d][t] + alpha) / (NDTSum[d] + T * alpha);
			}
		}
	}
	
	public int newTopic(int d, int w) {
		int oldTopic = wordTopic[d][w];
		NDT[d][oldTopic] -= 1;
		NTW[oldTopic][docWordIndex[d][w]] -= 1;
		NDTSum[d] -= 1;
		NTWSum[oldTopic] -= 1;
		
		double[] p = new double[T];
		for (int t = 0; t < T; t++) {
			p[t] = (NTW[t][docWordIndex[d][w]] + beta) / (NTWSum[t] + W * beta)
					* (NDT[d][t] + alpha) / (NDTSum[d] + T * alpha);
		}
		for (int t = 1; t < T; t++) {
			p[t] += p[t - 1];
		}
		double u = Math.random() * p[T - 1];
		int newTopic;
		for (newTopic = 0; newTopic < T; newTopic++) {
			if (u < p[newTopic]) {
				break;
			}
		}
		NDT[d][newTopic] += 1;
		NTW[newTopic][docWordIndex[d][w]] += 1;
		NDTSum[d] += 1;
		NTWSum[newTopic] += 1;
		return newTopic;
	}
	
	private void print(DocumentSet documentSet) {
		for (int t = 0; t < T; t++) {
			List<Integer> indexs = new ArrayList<Integer>();
			for (int w = 0; w < W; w++) {
				indexs.add(w);
			}
			Collections.sort(indexs, new WordComparator(phi[t]));
			System.out.println("topic: " + t);
			for (int i = 0; i < 40; i++) {
				String word = documentSet.getWords()[indexs.get(i)];
				double p = phi[t][indexs.get(i)];
				System.out.print(word + ":" + p + ",");
			}
			System.out.println();
		}
	}
	
	public void build() {
		DocumentSet documentSet = initDocuments();
		initParameters(documentSet);
		inference(documentSet);
		print(documentSet);
	}
	
	public static void main(String[] args) {
		new LDABuilder().build();
	}
}

class WordComparator implements Comparator<Integer> {
	
	public double[] sortProb;
	
	public WordComparator(double[] sortProb) {
		this.sortProb = sortProb;
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		if (sortProb[o1] > sortProb[o2])
			return -1;
		else if (sortProb[o1] < sortProb[o2])
			return 1;
		else
			return 0;
	}
	
}
