package org.project.common.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.project.common.distance.IDistance;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

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
	 * 计算文档间的相似度
	 * @param documents
	 * @param idistance
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
	
	/**
	 * 转化为矩阵 行为文档 列为词
	 * @param documents
	 * @return
	 */
	public static double[][] convertToDocTermMatrix(List<Document> documents) {
		Collection<String> allWords = DocumentHelper.wordsInDocs(documents, true);
		int docSize = documents.size(), allWordsSize = allWords.size();
		double[][] matrix = new double[docSize][allWordsSize];
		for (int i = 0; i < docSize; i++) {
			Map<String, Double> tfidfWords = documents.get(i).getTfidfWords();
			int j = 0;
			for (String word : allWords) {
				if (tfidfWords.containsKey(word)) {
					matrix[i][j] = tfidfWords.get(word).doubleValue();
				} else {
					matrix[i][j] = 0;
				}
				j++;
			}
		}
		return matrix;
	}
	
	/**
	 * 转化为矩阵 行为词 列为文档
	 * @param documents
	 * @return
	 */
	public static double[][] convertToTermDocMatrix(List<Document> documents) {
		Collection<String> allWords = DocumentHelper.wordsInDocs(documents, true);
		int docSize = documents.size(), allWordsSize = allWords.size();
		double[][] matrix = new double[allWordsSize][docSize];
		for (int i = 0; i < docSize; i++) {
			Map<String, Double> tfidfWords = documents.get(i).getTfidfWords();
			int j = 0;
			for (String word : allWords) {
				if (tfidfWords.containsKey(word)) {
					matrix[j][i] = tfidfWords.get(word).doubleValue();
				} else {
					matrix[j][i] = 0;
				}
				j++;
			}
		}
		return matrix;
	}
	
	/**
	 * 计算相似矩阵
	 * A * A' = D * S * S' * D'
	 * A' * A = D * S' * S * D'
	 * 比较两个术语  做"正向"乘法；第i行第j列表明了术语i和j的相似程度
	 * 比较两个文件做"逆向"乘法；第i行第j列表明了文件i和j的相似程度
	 * svd函数只适合行数大于列数的矩阵，如果行数小于列数，可对其转置矩阵做SVD分解 
	 * @param matrix
	 * @param dimension 维数
	 * @return
	 */
	public static double[][] calculateSimilarityMatrix(double[][] matrix, int dimension) {
		Matrix sMatrix = new Matrix(matrix);  
		boolean flag = matrix.length >= matrix[0].length ? true : false;
        SingularValueDecomposition svd = flag ? sMatrix.svd() : sMatrix.transpose().svd();  
        Matrix D = svd.getU();  
        Matrix S = svd.getS();  
        for(int i = dimension; i < S.getRowDimension(); i++){//降到dimension维  
            S.set(i, i, 0);  
        }  
        return flag ? D.times(S.times(S.transpose().times(D.transpose()))).getArray() 
        		: D.times(S.transpose().times(S.times(D.transpose()))).getArray();  
	}
	
	public static void main(String[] args) throws Exception {
		String path = DocumentUtils.class.getClassLoader().getResource("微测").toURI().getPath();
		DocumentSet documentSet = DocumentLoader.loadDocumentSet(path);
		calculateTFIDF_0(documentSet.getDocuments());
//		calculateSimilarity(documentSet.getDocuments(), new CosineDistance());
//		double[][] tdmatrix = convertToDocTermMatrix(documentSet.getDocuments());
		double[][] tdmatrix = convertToTermDocMatrix(documentSet.getDocuments());
		System.out.println("matrix row: " + tdmatrix.length + " col: " + tdmatrix[0].length);
		double[][] matrix = calculateSimilarityMatrix(tdmatrix, 100);
		System.out.println("matrix row: " + matrix.length + " col: " + matrix[0].length);
//		for (int i = 0, row = matrix.length; i < row; i++) {
//			for (int j = 0, col = matrix[0].length; j < col; j++) {
//				System.out.print(matrix[i][j] + ",");
//			}
//			System.out.println();
//		}
	}
	
}
