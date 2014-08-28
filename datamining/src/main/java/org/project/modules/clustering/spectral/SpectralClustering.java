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

public class SpectralClustering {
	
	public Data getW() {
		Data data = new Data();
		try {
			String path = SpectralClustering.class.getClassLoader().getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocumentSet(path);
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			DocumentUtils.calculateSimilarity(documents, new CosineDistance());
			Map<String, Map<String, Double>> map = new HashMap<String, Map<String, Double>>();
			for (Document document : documents) {
				String name = document.getName();
				Map<String, Double> similarities = map.get(name);
				if (null == similarities) {
					similarities = new HashMap<String, Double>();
					map.put(name, similarities);
				}
				for (DocumentSimilarity similarity : document.getSimilarities()) {
					similarities.put(similarity.getDoc2().getName(), similarity.getDistance());
				}
			}
			String[] docnames = map.keySet().toArray(new String[0]);
			data.setRow(docnames);
			data.setColumn(docnames);
			int len = docnames.length;
			double[][] values = new double[len][len];
			for (int i = 0; i < len; i++) {
				Map<String, Double> similarities = map.get(docnames[i]);
				for (int j = 0; j < len; j++) {
					double distance = similarities.get(docnames[j]);
					values[i][j] = distance < 0.01 ? 1 : 0;
				}
			}
			data.setValues(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public double[][] getD(double[][] w) {
		double[][] d = new double[0][0];
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

	public void build() {
		Data w = getW();
		System.out.println(w);
	}
	
	public static void main(String[] args) {
		new SpectralClustering().build();
	}
}
