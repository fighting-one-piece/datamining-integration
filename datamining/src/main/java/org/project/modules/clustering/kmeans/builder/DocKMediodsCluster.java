package org.project.modules.clustering.kmeans.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.project.common.document.DocumentLoader;
import org.project.common.document.Document;
import org.project.common.document.DocumentUtils;
import org.project.modules.clustering.kmeans.data.DataPoint;
import org.project.modules.clustering.kmeans.data.DataPointCluster;
import org.project.utils.DistanceUtils;

public class DocKMediodsCluster extends AbstractCluster {
	
//	public static final double THRESHOLD = 0.02;
	public static final double THRESHOLD = 0.4;
	
	public List<DataPoint> initData() {
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		try {
			String path = DocKMediodsCluster.class.getClassLoader().getResource("测试").toURI().getPath();
			List<Document> docs = DocumentLoader.loadDocList(path);
			DocumentUtils.calculateTFIDF(docs);
			for(Document doc : docs) {
				DataPoint dataPoint = new DataPoint();
				dataPoint.setValues(doc.getTfidfWords());
				dataPoint.setCategory(doc.getCategory());
				dataPoints.add(dataPoint);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataPoints;
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
			double distance = DistanceUtils.euclidean(
					newCenter.getValues(), center.getValues());
			System.out.println("distaince: " + distance);
			if (distance > THRESHOLD || center.equals(newCenter)) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		System.out.println("--------------");
		if (!flag) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters);
		}
	}
	
	public List<DataPointCluster> cluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = genInitCluster(points, k);
		printDataPointClusters(clusters);
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
