package org.project.modules.clustering.spectral.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.project.common.distance.CosineDistance;
import org.project.common.distance.EuclideanDistance;
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

public class BSpectralClusteringBuilder {

	public static int DIMENSION = 30;
	
	public static double THRESHOLD = 0.01;

	public Data getInitData() {
		Data data = new Data();
		try {
			String path = "D:\\resources\\data\\res2\\";
			DocumentSet documentSet = DocumentLoader.loadDocumentSetByThread(path);
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
					if (similarity.getDoc2().getName().equalsIgnoreCase(similarity.getDoc1().getName())) {
						similarities.put(similarity.getDoc2().getName(), 0.0);
					} else {
						similarities.put(similarity.getDoc2().getName(), similarity.getDistance());
					}
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

	/**
	 * 获取距离阀值在一定范围内的点
	 * @param data
	 * @return
	 */
	public double[][] getWByDistance(Data data) {
		Map<String, Map<String, Double>> nmap = data.getNmap();
		String[] docnames = data.getDocnames();
		int len = docnames.length;
		double[][] w = new double[len][len];
		for (int i = 0; i < len; i++) {
			Map<String, Double> similarities = nmap.get(docnames[i]);
			for (int j = 0; j < len; j++) {
				double distance = similarities.get(docnames[j]);
				w[i][j] = distance < THRESHOLD ? 1 : 0;
			}
		}
		return w;
	}
	
	/**
	 * 获取距离最近的K个点
	 * @param data
	 * @return
	 */
	public double[][] getWByKNearestNeighbors(Data data) {
		Map<String, Map<String, Double>> nmap = data.getNmap();
		String[] docnames = data.getDocnames();
		int len = docnames.length;
		double[][] w = new double[len][len];
		for (int i = 0; i < len; i++) {
			List<Map.Entry<String, Double>> similarities = 
					new ArrayList<Map.Entry<String, Double>>(nmap.get(docnames[i]).entrySet());
			sortSimilarities(similarities, DIMENSION);
			for (int j = 0; j < len; j++) {
				String name = docnames[j];
				boolean flag = false;
				for (Map.Entry<String, Double> entry : similarities) {
					if (name.equalsIgnoreCase(entry.getKey())) {
						flag = true;
						break;
					}
				}
				w[i][j] = flag ? 1 : 0;
			}
		}
		return w;
	}

	/**
	 * 垂直求和
	 * @param W
	 * @return
	 */
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

	/**
	 * 水平求和
	 * @param W
	 * @return
	 */
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
	
	/**
	 * 相似度排序，并取前K个，倒叙
	 * @param similarities
	 * @param k
	 */
	public void sortSimilarities(List<Map.Entry<String, Double>> similarities, int k) {
		Collections.sort(similarities, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		while (similarities.size() > k) {
			similarities.remove(similarities.size() - 1);
		}
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
	
	public List<DataPointCluster> genInitCluster1(List<DataPoint> dataPoints, int k) {
		List<DataPoint> temp = new ArrayList<DataPoint>();
		for (DataPoint dataPoint : dataPoints) {
			temp.add(dataPoint);
		}
		int len = temp.size();
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		DataPointCluster cluster = new DataPointCluster();
		DataPoint center = temp.remove(new Random().nextInt(len));
		cluster.setCenter(center);
		cluster.getDataPoints().add(center);
		clusters.add(cluster);
		while (clusters.size() < k) {
			DataPoint tempCenter = computeMediodsCenter(temp);
			double maxDistance = Integer.MIN_VALUE;
			for (DataPoint dataPoint : temp) {
				double distance = new EuclideanDistance().distance(
						dataPoint.getValues(), tempCenter.getValues());
				if (distance > maxDistance) {
					maxDistance = distance;
					center = dataPoint;
				}
			}
			cluster = new DataPointCluster();
			temp.remove(center);
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	public DataPoint computeMediodsCenter(List<DataPoint> dataPoints) {
		DataPoint centerPoint = null;
		double distance = Integer.MAX_VALUE;
		for (DataPoint dataPoint : dataPoints) {
			double d = 0.0;
			for (DataPoint temp : dataPoints) {
				d += new EuclideanDistance().distance(dataPoint.getValues(), temp.getValues());
			}
			if (d < distance) {
				distance = d;
				centerPoint = dataPoint;
			}
		}
		return centerPoint;
	}

	// 将点归入到聚类中
	public void handleCluster(List<DataPoint> points,
			List<DataPointCluster> clusters, int iterNum) {
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
			if (distance > 0.5) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		if (!flag && iterNum < 25) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters, ++iterNum);
		}
	}

	/**
	 * KMeans方法
	 * @param dataPoints
	 */
	public void kmeans(List<DataPoint> dataPoints) {
		List<DataPointCluster> clusters = genInitCluster1(dataPoints, 4);
		handleCluster(dataPoints, clusters, 0);
		int success = 0, failure = 0;
		for (DataPointCluster cluster : clusters) {
			String category = cluster.getCenter().getCategory();
			System.out.println("c: " + category);
			for (DataPoint dataPoint : cluster.getDataPoints()) {
				String dpCategory = dataPoint.getCategory();
				System.out.println("dc: " + dpCategory);
				if (category.equals(dpCategory)) {
					success++;
				} else {
					failure++;
				}
			}
			System.out.println("--------------------------");
		}
		System.out.println("total: " + (success + failure) + " success: "
				+ success + " failure: " + failure);
	}

	public void build() {
		Data data = getInitData();
		double[][] w = getWByKNearestNeighbors(data);
		double[][] d = getHorizontalD(w);
		Matrix W = new Matrix(w);
		Matrix D = new Matrix(d);
		Matrix L = D.minus(W);
		EigenvalueDecomposition eig = L.eig();
		double[][] v = eig.getV().getArray();
		double[][] vs = new double[v.length][DIMENSION];
		for (int i = 0, li = v.length; i < li; i++) {
			for (int j = 1, lj = DIMENSION; j <= lj; j++) {
				vs[i][j-1] = v[i][j];
			}
		}
		Matrix V = new Matrix(vs);
		Matrix O = new Matrix(data.getOriginal());
	    double[][] t = O.times(V).getArray();
	    List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		for (int i = 0; i < t.length; i++) {
			DataPoint dataPoint = new DataPoint();
			dataPoint.setCategory(data.getCmap().get(data.getColumn()[i]));
			dataPoint.setValues(t[i]);
			dataPoints.add(dataPoint);
		}
		for (int n = 0; n < 10; n++) {
			kmeans(dataPoints);
		}
	}

	public static void main(String[] args) {
		new BSpectralClusteringBuilder().build();
	}
	
}
