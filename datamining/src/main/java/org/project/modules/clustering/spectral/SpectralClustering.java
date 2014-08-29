package org.project.modules.clustering.spectral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.project.common.distance.CosineDistance;
import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentSimilarity;
import org.project.common.document.DocumentUtils;
import org.project.modules.clustering.spectral.data.Data;
import org.project.modules.clustering.spectral.data.DataPoint;
import org.project.modules.clustering.spectral.data.DataPointCluster;
import org.project.utils.DistanceUtils;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class SpectralClustering {

	public Data getInitData() {
		Data data = new Data();
		try {
			String path = SpectralClustering.class.getClassLoader()
					.getResource("测试").toURI().getPath();
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
					similarities.put(similarity.getDoc2().getName(),
							similarity.getDistance());
				}
			}
			String[] docnames = nmap.keySet().toArray(new String[0]);
			data.setRow(docnames);
			data.setColumn(docnames);
			data.setDocnames(docnames);
			int len = docnames.length;
			double[][] original = new double[len][len];
			for (int i = 0; i < len; i++) {
				Map<String, Double> similarities = nmap.get(docnames[i]);
				for (int j = 0; j < len; j++) {
					double distance = similarities.get(docnames[j]);
					original[i][j] = distance;
				}
			}
			data.setOriginal(original);
			data.setCmap(cmap);
			data.setNmap(nmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public double[][] getW(Data data) {
		Map<String, Map<String, Double>> nmap = data.getNmap();
		String[] docnames = data.getDocnames();
		int len = docnames.length;
		double[][] w = new double[len][len];
		for (int i = 0; i < len; i++) {
			Map<String, Double> similarities = nmap.get(docnames[i]);
			for (int j = 0; j < len; j++) {
				double distance = similarities.get(docnames[j]);
				w[i][j] = distance < 0.01 ? 1 : 0;
			}
		}
		return w;
	}

	public double[][] getVerticalD(double[][] W) {
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

	public double[][] getHorizontalD(double[][] W) {
		int row = W.length;
		int column = W[0].length;
		double[][] d = new double[row][column];
		for (int i = 0; i < row; i++) {
			double sum = 0;
			for (int j = 0; j < column; j++) {
				sum += W[i][j];
			}
			d[i][i] = sum;
		}
		return d;
	}
	
	public void sortSimilarities(List<DocumentSimilarity> similarities) {
		Collections.sort(similarities, new Comparator<DocumentSimilarity>() {
			@Override
			public int compare(DocumentSimilarity o1, DocumentSimilarity o2) {
				return Double.valueOf(o1.getDistance()).compareTo(
						o2.getDistance());
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

	// 随机生成中心点，并生成初始的K个聚类
	public List<DataPointCluster> genInitCluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		Random random = new Random();
		Set<String> categories = new HashSet<String>();
		while (clusters.size() < k) {
			DataPoint center = points.get(random.nextInt(points.size()));
			String category = center.getCategory();
			if (categories.contains(category))
				continue;
			categories.add(category);
			DataPointCluster cluster = new DataPointCluster();
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}

	// 将点归入到聚类中
	public void handleCluster(List<DataPoint> points,
			List<DataPointCluster> clusters, int iterNum) {
		System.out.println("iterNum: " + iterNum);
		for (DataPoint point : points) {
			DataPointCluster maxCluster = null;
			double maxDistance = Integer.MIN_VALUE;
			for (DataPointCluster cluster : clusters) {
				DataPoint center = cluster.getCenter();
				double distance = DistanceUtils.cosine(point.getValues(),
						center.getValues());
				if (distance > maxDistance) {
					maxDistance = distance;
					maxCluster = cluster;
				}
			}
			if (null != maxCluster) {
				maxCluster.getDataPoints().add(point);
			}
		}
		// 终止条件定义为原中心点与新中心点距离小于一定阀值
		// 当然也可以定义为原中心点等于新中心点
		boolean flag = true;
		for (DataPointCluster cluster : clusters) {
			DataPoint center = cluster.getCenter();
			DataPoint newCenter = cluster.computeMediodsCenter();
			double distance = DistanceUtils.cosine(newCenter.getValues(),
					center.getValues());
			System.out.println("distaince: " + distance);
			if (distance > 0.028) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		System.out.println("--------------");
		if (!flag && iterNum < 25) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters, ++iterNum);
		}
	}

	public void kmeans(List<DataPoint> dataPoints) {
		List<DataPointCluster> clusters = genInitCluster(dataPoints, 4);
		for (DataPointCluster cluster : clusters) {
			System.out.println("center: " + cluster.getCenter().getCategory());
		}
		handleCluster(dataPoints, clusters, 0);
		int success = 0, failure = 0;
		for (DataPointCluster cluster : clusters) {
			String category = cluster.getCenter().getCategory();
			System.out.println("center: " + category + "--"
					+ cluster.getDataPoints().size());
			for (DataPoint dataPoint : cluster.getDataPoints()) {
				String dpCategory = dataPoint.getCategory();
				System.out.println(dpCategory);
				if (category.equals(dpCategory)) {
					success++;
				} else {
					failure++;
				}
			}
			System.out.println("----------");
		}
		System.out.println("total: " + (success + failure) + " success: "
				+ success + " failure: " + failure);
	}

	public void build() {
		Data data = getInitData();
		double[][] w = getW(data);
		System.out.println("-----w-----");
		print(w);
//		double[][] d = getVerticalD(w);
		double[][] d = getHorizontalD(w);
		System.out.println("-----d-----");
		print(d);
		Matrix W = new Matrix(w);
		Matrix D = new Matrix(d);
		Matrix L = D.minus(W);
		double[][] l = L.getArray();
		System.out.println("-----l-----");
		print(l);
//		SingularValueDecomposition svd = L.svd();
		// Matrix S = svd.getS();
		// double[][] s = S.getArray();
		// System.out.println("-----s-----");
		// print(s);
		 EigenvalueDecomposition eig = L.eig();
		 System.out.println("-----e-----");
		 print(eig.getD().getArray());
//		 System.out.println("-----ev-----");
//		 print(eig.getV().getArray());
//		double[] singularValues = svd.getSingularValues();
//		int len = singularValues.length;
//		for (int i = 0; i < len; i++) {
//			singularValues[i] = singularValues[i] + singularValues[len - 1];
//			singularValues[len - 1] = singularValues[i]
//					- singularValues[len - 1];
//			singularValues[i] = singularValues[i] - singularValues[len - 1];
//		}
//		double[][] k = new double[len][len];
//		for (int i = 0; i < len; i++) {
//			for (int j = 0; j < len; j++) {
//				k[i][j] = (i < 50 && i == j) ? singularValues[i] : 0;
//			}
//		}
//		System.out.println("-----sv-----");
//		print(svd.getS().getArray());
//		Matrix K = new Matrix(k);
//		Matrix N = new Matrix(data.getOriginal());
//		Matrix NK = N.times(K);
//		System.out.println("-----nk-----");
//		double[][] nk = NK.getArray();
		double[][] nk = eig.getV().getArray();
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		for (int i = 0; i < nk.length; i++) {
			DataPoint dataPoint = new DataPoint();
			dataPoint.setCategory(data.getCmap().get(data.getColumn()[i]));
			dataPoint.setValues(nk[i]);
			dataPoints.add(dataPoint);
		}
		kmeans(dataPoints);
	}

	public static void main(String[] args) {
		new SpectralClustering().build();
	}
}
