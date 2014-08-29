package org.project.modules.clustering.spectral;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.common.distance.CosineDistance;
import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentSimilarity;
import org.project.common.document.DocumentUtils;
import org.project.modules.clustering.spectral.data.Data;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class SpectralClustering {
	
	public Data getW() {
		Data data = new Data();
		try {
			String path = SpectralClustering.class.getClassLoader().getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocumentSet(path);
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			DocumentUtils.calculateSimilarity(documents, new CosineDistance());
			Map<String, Map<String, Double>> nmap = new HashMap<String, Map<String, Double>>();
			Map<String, String> cmap = new HashMap<String, String>();
			for (Document document : documents) {
				String name = document.getName();
				cmap.put(name, document.getCategory());
				Map<String, Double> similarities = nmap.get(name);
				if (null == similarities) {
					similarities = new HashMap<String, Double>();
					nmap.put(name, similarities);
				}
				for (DocumentSimilarity similarity : document.getSimilarities()) {
					similarities.put(similarity.getDoc2().getName(), similarity.getDistance());
				}
			}
			String[] docnames = nmap.keySet().toArray(new String[0]);
			data.setRow(docnames);
			data.setColumn(docnames);
			int len = docnames.length;
			double[][] values = new double[len][len];
			double[][] original = new double[len][len];
			for (int i = 0; i < len; i++) {
				Map<String, Double> similarities = nmap.get(docnames[i]);
				for (int j = 0; j < len; j++) {
					double distance = similarities.get(docnames[j]);
					values[i][j] = distance < 0.01 ? 1 : 0;
					original[i][j] = distance;
				}
			}
			data.setValues(values);
			data.setOriginal(original);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public double[][] getW(double[][] values) {
		double[][] w = null;
		return w;
	}
	
	public double[][] getD(double[][] W) {
		int row = W.length;
		int column = W[0].length;
		double[][] d = new double[row][column];
		for (int j = 0; j < column; j++) {
			double sum = 0;
			for (int i = 0; i < row; i++) {
				sum += W[i][j];
			}
			d[j][j] = sum;
		}
		return d;
	}
	
	public void sortSimilarities(List<DocumentSimilarity> similarities) {
		Collections.sort(similarities, new Comparator<DocumentSimilarity>() {

			@Override
			public int compare(DocumentSimilarity o1, DocumentSimilarity o2) {
				return Double.valueOf(o1.getDistance()).compareTo(o2.getDistance());
			}
		});
	}
	
	public void print(double[][] values) {
		for (int i = 0, il = values.length; i < il; i++) {
			for (int j = 0, jl = values[0].length; j < jl; j++) {
				System.out.print(values[i][j] + "  ");
			}
			System.out.println("\n");
		}
	}

	public void build() {
		Data data = getW();
		System.out.println("-----w-----");
		print(data.getValues());
		double[][] d = getD(data.getValues());
		System.out.println("-----d-----");
		print(d);
		Matrix W = new Matrix(data.getValues());
		Matrix D = new Matrix(d);
		Matrix L = D.minus(W);
		double[][] l = L.getArray();
		System.out.println("-----l-----");
		print(l);
		SingularValueDecomposition svd = L.svd();
//		Matrix S = svd.getS();
//		double[][] s = S.getArray();
//		System.out.println("-----s-----");
//		print(s);
//		EigenvalueDecomposition eig = L.eig();
//		System.out.println("-----e-----");
//		print(eig.getD().getArray());
//		System.out.println("-----ev-----");
//		print(eig.getV().getArray());
		double[] singularValues = svd.getSingularValues();
		System.out.println("singularValues: " + singularValues.length);
		int len = singularValues.length;
		for (int i = 0; i < len; i++) {
			singularValues[i] = singularValues[i] + singularValues[len - 1];
			singularValues[len - 1] = singularValues[i] - singularValues[len - 1];
			singularValues[i] = singularValues[i] - singularValues[len - 1];
		}
		double[][] k = new double[len][len];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				System.out.println();
				k[i][j] = (i < 50 && i == j) ? singularValues[i] : 0;
			}
		}
		Matrix K = new Matrix(k);
		Matrix N = new Matrix(data.getOriginal());
		Matrix NK = N.times(K);
		System.out.println("-----nk-----");
		print(NK.getArray());
	}
	
	public static void main(String[] args) {
		new SpectralClustering().build();
	}
}
