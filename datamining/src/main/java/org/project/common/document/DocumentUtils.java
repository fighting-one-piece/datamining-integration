package org.project.common.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.project.common.distance.CosineDistance;
import org.project.common.distance.IDistance;

public class DocumentUtils {
	
	/**
	 * 计算TF
	 * @param documents
	 */
	public static void calculateTF(List<Document> documents) {
		for (Document document : documents) {
			Map<String, Double> tfWords = document.getTfWords();
			for (String word : document.getWords()) {
				Double count = tfWords.get(word);
				tfWords.put(word, null == count ? 1 : count + 1);
			}
			System.out.println("doc " + document.getName() + " calculate tf finish");
		}
	}
	
	/**
	 * 计算TFIDF
	 * TF计算是词频除以总词数
	 * @param documents
	 */
	public static void calculateTFIDF_0(List<Document> documents) {
		int docTotalCount = documents.size();
		for (Document document : documents) {
			Map<String, Double> tfidfWords = document.getTfidfWords();
			int wordTotalCount = document.getWords().length;
			Map<String, Integer> docWords = DocumentHelper.wordsInDocStatistics(document);
			for (String word : docWords.keySet()) {
				double wordCount = docWords.get(word);
				double tf = wordCount / wordTotalCount;
				double docCount = DocumentHelper.wordInDocsStatistics(word, documents) + 1;
				double idf = Math.log(docTotalCount / docCount);
				double tfidf = tf * idf;
				tfidfWords.put(word, tfidf);
			}
			System.out.println("doc " + document.getName() + " calculate tfidf finish");
		}
	}
	
	/**
	 * 计算TFIDF
	 * TF计算是词频除以词频最高数
	 * @param documents
	 */
	public static void calculateTFIDF_1(List<Document> documents) {
		int docTotalCount = documents.size();
		for (Document document : documents) {
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
			if (list.size() == 0) continue; 
			double wordTotalCount = list.get(0).getValue();
			Map<String, Integer> docWords = DocumentHelper.wordsInDocStatistics(document);
			for (String word : docWords.keySet()) {
				double wordCount = docWords.get(word);
				double tf = wordCount / wordTotalCount;
				double docCount = DocumentHelper.wordInDocsStatistics(word, documents) + 1;
				double idf = Math.log(docTotalCount / docCount);
				double tfidf = tf * idf;
				tfidfWords.put(word, tfidf);
			}
			System.out.println("doc " + document.getName() + " calculate tfidf finish");
		}
	}
	
	/**
	 * 计算相似度
	 * @param documents
	 */
	public static void calculateSimilarity(List<Document> documents, IDistance idistance) {
		for (Document document : documents) {
			for (Document odocument : documents) {
				double distance = idistance.distance(document.getTfidfWords(), odocument.getTfidfWords());
				DocumentSimilarity docSimilarity = new DocumentSimilarity();
				docSimilarity.setDoc1(document);
				docSimilarity.setDoc2(odocument);
				docSimilarity.setDistance(distance);
				document.getSimilarities().add(docSimilarity);
			}
			System.out.println("doc " + document.getName() + " calculate similar finish");
		}
	}

	public static void main(String[] args) throws Exception {
		String path = DocumentUtils.class.getClassLoader().getResource("测试").toURI().getPath();
		DocumentSet dataSet = DocumentLoader.loadDocumentSet(path);
		calculateTFIDF_0(dataSet.getDocuments());
		calculateSimilarity(dataSet.getDocuments(), new CosineDistance());
	}
	
}
