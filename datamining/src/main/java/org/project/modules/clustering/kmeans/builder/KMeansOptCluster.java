package org.project.modules.clustering.kmeans.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.project.common.distance.EuclideanDistance;
import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentUtils;
import org.project.modules.algorithm.featureselect.FSChiSquare;
import org.project.modules.algorithm.featureselect.IFeatureSelect;
import org.project.modules.clustering.kmeans.data.DataPoint;
import org.project.modules.clustering.kmeans.data.DataPointCluster;

public class KMeansOptCluster {
	
	//开方检验词限制
	public static int CHI_WORD_LIMIT = 100;
	
	private List<DataPoint> dataPoints = new ArrayList<DataPoint>();
	
	private List<DataPoint> isolatePoints = new ArrayList<DataPoint>();
	
	public void splitDataPoints() {
		try {
			String path = DocKMediodsCluster.class.getClassLoader().getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocumentSet(path);
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			DocumentUtils.calculateSimilarity(documents, new EuclideanDistance());
			double meanSum = 0;
			for(Document document : documents) {
				double mean = document.calculateSimilarityMean();
				System.out.println("mean: " + mean);
				meanSum += mean;
			}
			double a = 1.1 * meanSum / documents.size();
			System.out.println("a: " + a);
			for(Document document : documents) {
				DataPoint dataPoint = new DataPoint();
				dataPoint.setValues(document.getTfidfWords());
				dataPoint.setCategory(document.getCategory());
				double mean = document.calculateSimilarityMean();
				if (mean > a) {
					isolatePoints.add(dataPoint);
				} else {
					dataPoints.add(dataPoint);
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	//开方检验特征选择降维
	public void reduceDimensionsByCHI(DocumentSet documentSet) {
		IFeatureSelect featureSelect = new FSChiSquare();
		featureSelect.handle(documentSet);
		List<Document> documents = documentSet.getDocuments();
		for (Document document : documents) {
			Map<String, Double> chiWords = document.getChiWords();
			List<Map.Entry<String, Double>> list = sortMap(chiWords);
			int len = list.size() < CHI_WORD_LIMIT ? list.size() : CHI_WORD_LIMIT;
			String[] words = new String[len];
			for (int i = 0; i < len; i++) {
				words[i] = list.get(i).getKey();
			}
			document.setWords(words);
		}
	}
	
	public List<Map.Entry<String, Double>> sortMap(Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = 
				new ArrayList<Map.Entry<String, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				if (o1.getValue().isNaN()) {
					o1.setValue(0.0);
				}
				if (o2.getValue().isNaN()) {
					o2.setValue(0.0);
				}
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		return list;
	}
	
	public List<DataPointCluster> genInitCluster(List<DataPoint> dataPoints, int k) {
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
	
	public List<DataPointCluster> genInitCluster1(List<DataPoint> dataPoints, int k) {
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		DataPointCluster cluster = new DataPointCluster();
		DataPoint center = dataPoints.get(new Random().nextInt(dataPoints.size()));
		cluster.setCenter(center);
		cluster.getDataPoints().add(center);
		clusters.add(cluster);
		while (clusters.size() < k) {
			double maxDistance = Integer.MIN_VALUE;
			for (DataPoint dataPoint : dataPoints) {
				double distance = new EuclideanDistance().distance(
						dataPoint.getValues(), center.getValues());
				if (distance > maxDistance) {
					maxDistance = distance;
					center = dataPoint;
				}
			}
			cluster = new DataPointCluster();
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	public List<DataPointCluster> genInitCluster2(List<DataPoint> dataPoints, int k) {
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		Set<String> categories = new HashSet<String>();
		while (clusters.size() < k) {
			for (DataPoint dataPoint : dataPoints) {
				String category = dataPoint.getCategory();
				if (categories.contains(category)) continue;
				categories.add(category);
				DataPointCluster cluster = new DataPointCluster();
				cluster.setCenter(dataPoint);
				cluster.getDataPoints().add(dataPoint);
				clusters.add(cluster);
			}
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
	
	//将点归入到聚类中
	public void handleCluster(List<DataPoint> dataPoints, List<DataPointCluster> clusters) {
		for (DataPoint point : dataPoints) {
			DataPointCluster minCluster = null;
			double minDistance = Integer.MAX_VALUE;
			for (DataPointCluster cluster : clusters) {
				DataPoint center = cluster.getCenter();
				double distance = new EuclideanDistance().distance(point.getValues(), center.getValues());
				if (distance < minDistance) {
					minDistance = distance;
					minCluster = cluster;
				}
			}
			if (null != minCluster) {
				minCluster.getDataPoints().add(point);
			}
		}
		//终止条件定义为原中心点与新中心点距离小于一定阀值
		//当然也可以定义为原中心点等于新中心点
		boolean flag = true;
		System.out.println("---------");
		for (DataPointCluster cluster : clusters) {
			DataPoint center = cluster.getCenter();
			DataPoint newCenter = cluster.computeMediodsCenter();
			double distance = new EuclideanDistance().distance(center.getValues(), newCenter.getValues());
			System.out.println("distaince: " + distance);
			if (distance > 0.26) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		if (!flag) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(dataPoints, clusters);
		}
	}
	
	public void print(List<DataPoint> dataPoints) {
		System.out.println("------------" + dataPoints.size()+ "---------------");
		for (DataPoint dataPoint : dataPoints) {
			System.out.println(dataPoint.getCategory());
		}
	}
	
	public void build() {
		splitDataPoints();
		List<DataPointCluster> clusters = genInitCluster2(dataPoints, 4);
		for (DataPointCluster cluster : clusters) {
			System.out.println(cluster.getCenter().getCategory());
		}
		handleCluster(dataPoints, clusters);
		for (DataPointCluster cluster : clusters) {
			System.out.println("center: " + cluster.getCenter());
			System.out.println("cluster size: " + cluster.getDataPoints().size());
			for (DataPoint point : cluster.getDataPoints()) {
				System.out.println(point.getCategory());
			}
		}
	}
	
	public static void main(String[] args) {
		new KMeansOptCluster().build();
	}

}
