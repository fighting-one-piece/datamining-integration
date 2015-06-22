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

	/** 文档数、主题数、单词数*/
	private int D = 0, T = 0, W = 0;
	/** Dirichlet parameter alpha (document--topic associations) */ 
	/** Dirichlet parameter beta (topic--word associations) */  
	private float alpha = 0.5f, beta = 0.1f;
	/** 单词索引数组*/
	private int[][] docWordIndex = null;
    /** 单词主题数组*/
	private int[][] wordTopic = null;
	/** 文档主题数组*/
	private int[][] NDT = null;
	/** 主题单词数组*/
	private int[][] NTW = null;
	
	private int[] NDTSum = null;
	
	private int[] NTWSum = null;
	
	private double[][] thetaSum = null;

	private double[][] phiSum = null;
	/** 迭代次数*/
	private int ITER_NUMBER = 1000;
	/** 主题数目*/
	private int TOPIC_NUMBER = 10;
	
	private int SAMPLE_LAG = 10;
	
	private int THRESHOLD = 200;
	
	private int STATISTICS = 0;

	public DocumentSet initDocuments() {
		DocumentSet documentSet = null;
		try {
			String path = LDABuilder.class.getClassLoader().getResource("测试").toURI().getPath();
			documentSet = DocumentLoader.loadDocumentSetByThread(path);
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
		T = TOPIC_NUMBER;
		NDT = new int[D][T];
		NTW = new int[T][W];
		NDTSum = new int[D];
		NTWSum = new int[T];
		thetaSum = new double[D][T];
		phiSum = new double[T][W];
		
		List<Document> documents = documentSet.getDocuments();
		docWordIndex = new int[D][];
		for (int d = 0; d < D; d++) {
			int len = documents.get(d).getWordsIndex().length;
			docWordIndex[d] = new int[len];
			for (int w = 0; w < len; w++) {
				docWordIndex[d][w] = documents.get(d).getWordsIndex()[w];
			}
		}
		
		wordTopic = new int[D][];
		for (int d = 0; d < D; d++) {
			int len = documents.get(d).getWordsIndex().length;
			wordTopic[d] = new int[len];
			for (int w = 0; w < len; w++) {
				int initTopic = (int) (Math.random() * T);
				wordTopic[d][w] = initTopic;
				NDT[d][initTopic] += 1;
				NTW[initTopic][docWordIndex[d][w]] += 1;
				NTWSum[initTopic] += 1;
			}
			NDTSum[d] = len;
		}
		
	}
	
	public void inference(DocumentSet documentSet) {
		List<Document> documents = documentSet.getDocuments();
		for (int n = 0; n < ITER_NUMBER; n++) {
			
			for (int i = 0; i < D; i++) {
				int len = documents.get(i).getWordsIndex().length;
				for (int j = 0; j < len; j++) {
					int newTopic = newTopic(i, j);
					wordTopic[i][j] = newTopic;
				}
			}
            //如果当前迭代轮数已经超过 THRESHOLD的限制，并且正好达到SAMPLE_LAG间隔  
            //则当前的这个状态是要计入总的输出参数的，否则的话忽略当前状态，继续sample  
            if ((n > THRESHOLD) && (SAMPLE_LAG > 0) && (n % SAMPLE_LAG == 0)) {  
            	updateEstimatedParameters();
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
					* ((NDT[d][t] + alpha) / (NDTSum[d] + T * alpha));
		}
		for (int t = 1; t < T; t++) {
			p[t] += p[t - 1];
		}
		double rp = Math.random() * p[T - 1];
		int newTopic;
		for (newTopic = 0; newTopic < T; newTopic++) {
			if (rp < p[newTopic]) {
				break;
			}
		}
		NDT[d][newTopic] += 1;
		NTW[newTopic][docWordIndex[d][w]] += 1;
		NDTSum[d] += 1;
		NTWSum[newTopic] += 1;
		return newTopic;
	}
	
	private void updateEstimatedParameters() {
		for (int d = 0; d < D; d++) {
			for (int t = 0; t < T; t++) {
				thetaSum[d][t] += (NDT[d][t] + alpha) / (NDTSum[d] + T * alpha);
			}
		}
		for (int t = 0; t < T; t++) {
			for (int w = 0; w < W; w++) {
				phiSum[t][w] += (NTW[t][w] + beta) / (NTWSum[t] + W * beta);
			}
		}
		STATISTICS++;
	}
	
	public double[][] getTheta() {  
        double[][] theta = new double[D][T];  
        if (SAMPLE_LAG > 0) {  
            for (int d = 0; d < D; d++) {  
                for (int t = 0; t < T; t++) {  
                    theta[d][t] = thetaSum[d][t] / STATISTICS;  
                }  
            }  
        } else {  
        	for (int d = 0; d < D; d++) {  
                for (int t = 0; t < T; t++) {  
                    theta[d][t] = (NDT[d][t] + alpha) / (NDTSum[d] + T * alpha);  
                }  
            }  
        }  
        return theta;  
    }  
	
	public double[][] getPhi() {  
        double[][] phi = new double[T][W];  
        if (SAMPLE_LAG > 0) {  
            for (int t = 0; t < T; t++) {  
                for (int w = 0; w < W; w++) {  
                    phi[t][w] = phiSum[t][w] / STATISTICS;  
                }  
            }  
        } else {  
        	for (int t = 0; t < T; t++) {  
                for (int w = 0; w < W; w++) {  
                    phi[t][w] = (NTW[t][w] + beta) / (NTWSum[t] + W * beta);  
                }  
            }  
        }  
        return phi;  
    }  
	
	private void print(DocumentSet documentSet) {
		for (int t = 0; t < T; t++) {
			List<Integer> indexs = new ArrayList<Integer>();
			for (int w = 0; w < W; w++) {
				indexs.add(w);
			}
			Collections.sort(indexs, new WordComparator(getPhi()[t]));
			System.out.println("topic: " + t);
			for (int i = 0; i < 40; i++) {
				String word = documentSet.getWords()[indexs.get(i)];
				double p = getPhi()[t][indexs.get(i)];
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
		System.exit(0);
	}
}

class WordComparator implements Comparator<Integer> {
	
	public double[] probabilities;
	
	public WordComparator(double[] probabilities) {
		this.probabilities = probabilities;
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		if (probabilities[o1] > probabilities[o2])
			return -1;
		else if (probabilities[o1] < probabilities[o2])
			return 1;
		else
			return 0;
	}
	
}
