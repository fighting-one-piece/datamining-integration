package org.project.modules.clustering.kmeans.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.project.common.document.DocumentLoader;
import org.project.common.document.Document;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentUtils;
import org.project.modules.algorithm.featureselect.FSChiSquare;
import org.project.modules.algorithm.featureselect.IFeatureSelect;
import org.project.modules.clustering.kmeans.data.DataPoint;
import org.project.modules.clustering.kmeans.data.DataPointCluster;
import org.project.utils.DistanceUtils;

public class DocKMediodsCluster extends AbstractCluster {
	
	//阀值
	public static final double THRESHOLD = 0.01;
	//迭代次数
	public static final int ITER_NUM = 10;
	//开方检验词限制
	public static int CHI_WORD_LIMIT = 100;
	//期望交叉熵词限制
	public static int ECE_WORD_LIMIT = 600;
	//信息增益词限制
	public static int IG_WORD_LIMIT = 500;
	
	public List<DataPoint> initData() {
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		try {
			String path = DocKMediodsCluster.class.getClassLoader().getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocSet(path);
			reduceDimensionsByCHI(documentSet);
			//计算TFIDF
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF(documents);
			for(Document document : documents) {
				DataPoint dataPoint = new DataPoint();
				dataPoint.setValues(document.getTfidfWords());
				dataPoint.setCategory(document.getCategory());
				dataPoints.add(dataPoint);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataPoints;
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
	
	//随机生成中心点，并生成初始的K个聚类
	public List<DataPointCluster> genInitCluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		Random random = new Random();
		for (int i = 0, len = points.size(); i < k; i++) {
			DataPointCluster cluster = new DataPointCluster();
			DataPoint center = points.get(random.nextInt(len));
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	//将点归入到聚类中
	public void handleCluster(List<DataPoint> points, List<DataPointCluster> clusters, int iterNum) {
		System.out.println("iterNum: " + iterNum);
		for (DataPoint point : points) {
			DataPointCluster maxCluster = null;
			double maxDistance = Integer.MIN_VALUE;
			for (DataPointCluster cluster : clusters) {
				DataPoint center = cluster.getCenter();
				double distance = DistanceUtils.cosine(point.getValues(), center.getValues());
				if (distance > maxDistance) {
					maxDistance = distance;
					maxCluster = cluster;
				}
			}
			if (null != maxCluster) {
				maxCluster.getDataPoints().add(point);
			}
		}
		//终止条件定义为原中心点与新中心点距离小于一定阀值
		//当然也可以定义为原中心点等于新中心点
		boolean flag = true;
		for (DataPointCluster cluster : clusters) {
			DataPoint center = cluster.getCenter();
			DataPoint newCenter = cluster.computeMediodsCenter();
			double distance = DistanceUtils.cosine(
					newCenter.getValues(), center.getValues());
			System.out.println("distaince: " + distance);
			if (distance > THRESHOLD) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		System.out.println("--------------");
		if (!flag) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters, ++iterNum);
		}
	}
	
	public List<DataPointCluster> cluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = genInitCluster(points, k);
		printDataPointClusters(clusters);
		handleCluster(points, clusters, 0);
		return clusters;
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
	
	public void build() {
		List<DataPoint> points = initData();
		List<DataPointCluster> clusters = cluster(points, 4);
		printDataPointClusters(clusters);
	}
	
	public static void main(String[] args) {
		DocKMediodsCluster builder = new DocKMediodsCluster();
		builder.build();
	}
	
}
