package org.project.modules.clustering.kmeans.builder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.project.modules.clustering.kmeans.data.PointCluster;
import org.project.modules.clustering.kmeans.data.Point;

public class KMediodsCluster extends AbstractCluster {
	
	public static final double THRESHOLD = 2.0;
	
	public List<Point> initData() {
		List<Point> points = new ArrayList<Point>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = KMediodsCluster.class.getClassLoader().getResourceAsStream("kmeans1.txt");
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line && !"".equals(line)) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				double x = Double.parseDouble(tokenizer.nextToken());
				double y = Double.parseDouble(tokenizer.nextToken());
				points.add(new Point(x , y));
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
		}
		return points;
	}
	
	//随机生成中心点，并生成初始的K个聚类
	public List<PointCluster> genInitCluster(List<Point> points, int k) {
		List<PointCluster> clusters = new ArrayList<PointCluster>();
		Random random = new Random();
		for (int i = 0, len = points.size(); i < k; i++) {
			PointCluster cluster = new PointCluster();
			Point center = points.get(random.nextInt(len));
			cluster.setCenter(center);
			cluster.getPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	//将点归入到聚类中
	public void handleCluster(List<Point> points, List<PointCluster> clusters) {
		for (Point point : points) {
			PointCluster minCluster = null;
			double minDistance = Integer.MAX_VALUE;
			for (PointCluster cluster : clusters) {
				Point center = cluster.getCenter();
				double distance = euclideanDistance(point, center);
//				double distance = manhattanDistance(point, center);
				if (distance < minDistance) {
					minDistance = distance;
					minCluster = cluster;
				}
			}
			if (null != minCluster) {
				minCluster.getPoints().add(point);
			}
		}
		//终止条件定义为原中心点与新中心点距离小于一定阀值
		//当然也可以定义为原中心点等于新中心点
		boolean flag = true;
		for (PointCluster cluster : clusters) {
			Point center = cluster.getCenter();
			System.out.println("center: " + center);
			Point newCenter = cluster.computeMediodsCenter();
			System.out.println("new center: " + newCenter);
//			if (!center.equals(newCenter)) {
			double distance = euclideanDistance(center, newCenter);
			System.out.println("distaince: " + distance);
			if (distance > THRESHOLD) {
				flag = false;
				cluster.setCenter(newCenter);
			}
		}
		if (!flag) {
			for (PointCluster cluster : clusters) {
				cluster.getPoints().clear();
			}
			handleCluster(points, clusters);
		}
	}
	
	public List<PointCluster> cluster(List<Point> points, int k) {
		List<PointCluster> clusters = genInitCluster(points, k);
		handleCluster(points, clusters);
		return clusters;
	}
	
	public void build() {
		List<Point> points = initData();
		List<PointCluster> clusters = cluster(points, 4);
		printClusters(clusters);
	}
	
	public static void main(String[] args) {
		KMediodsCluster builder = new KMediodsCluster();
		builder.build();
	}
	
}
