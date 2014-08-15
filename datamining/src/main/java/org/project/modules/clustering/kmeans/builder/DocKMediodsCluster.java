package org.project.modules.clustering.kmeans.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.project.common.document.DocumentLoader;
import org.project.common.document.Document;
import org.project.common.document.DocumentHelper;
import org.project.modules.clustering.kmeans.data.DataPoint;
import org.project.modules.clustering.kmeans.data.DataPointCluster;
import org.project.utils.DistanceUtils;

public class DocKMediodsCluster extends AbstractCluster {
	
	public static final double THRESHOLD = 2.0;
	
	public List<DataPoint> initData() {
		List<DataPoint> points = new ArrayList<DataPoint>();
		try {
			String path = DocKMediodsCluster.class.getClassLoader().getResource("测试").toURI().getPath();
			List<Document> docs = DocumentLoader.loadDocList(path);
			Set<String> allWords = new HashSet<String>();
			for(Document doc : docs) {
				allWords.addAll(doc.getWordSet());
			}
			String[] words = allWords.toArray(new String[0]);
			for(Document doc : docs) {
				double[] values = DocumentHelper.docWordsVector(doc, words);
				doc.setWordsVec(values);
				DataPoint dataPoint = new DataPoint();
				dataPoint.setValues(values);
				dataPoint.setCategory(doc.getCategory());
				points.add(dataPoint);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return points;
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
	public void handleCluster(List<DataPoint> points, List<DataPointCluster> clusters) {
		for (DataPoint point : points) {
			DataPointCluster minCluster = null;
			double minDistance = Integer.MAX_VALUE;
			for (DataPointCluster cluster : clusters) {
				DataPoint center = cluster.getCenter();
				double distance = DistanceUtils.euclidean(
						point.getValues(), center.getValues());
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
		for (DataPointCluster cluster : clusters) {
			DataPoint center = cluster.getCenter();
			System.out.println("center: " + center);
			DataPoint newCenter = cluster.computeMediodsCenter();
			System.out.println("new center: " + newCenter);
//			if (!center.equals(newCenter)) {
			double distance = DistanceUtils.euclidean(
					newCenter.getValues(), center.getValues());
			System.out.println("distaince: " + distance);
			if (distance > THRESHOLD) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		if (!flag) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters);
		}
	}
	
	public List<DataPointCluster> cluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = genInitCluster(points, k);
		handleCluster(points, clusters);
		return clusters;
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
