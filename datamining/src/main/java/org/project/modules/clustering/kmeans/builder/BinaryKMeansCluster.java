package org.project.modules.clustering.kmeans.builder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.project.modules.clustering.kmeans.data.PointCluster;
import org.project.modules.clustering.kmeans.data.Point;

public class BinaryKMeansCluster extends AbstractCluster {
	
	public static final double THRESHOLD = 2.0;
	
	public List<Point> initData() {
		List<Point> points = new ArrayList<Point>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = BinaryKMeansCluster.class.getClassLoader().getResourceAsStream("kmeans1.txt");
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
	
	//生成初始的1个聚类
	public List<PointCluster> genInitCluster(List<Point> points) {
		List<PointCluster> clusters = new ArrayList<PointCluster>();
		PointCluster pointCluster = new PointCluster();
		for (Point point : points) {
			pointCluster.getPoints().add(point);
		}
		Point center = pointCluster.computeMeansCenter();
		pointCluster.setCenter(center);
		clusters.add(pointCluster);
		return clusters;
	}
	
	public List<PointCluster> cluster(List<Point> points, int k) {
		List<PointCluster> clusters = genInitCluster(points);
		while (clusters.size() < k) {
			List<PointCluster> minClusters = null;
			double minSSE = Integer.MAX_VALUE;
			for (PointCluster cluster : clusters) {
				List<Point> clusterPoints = cluster.getPoints();
				KMeansCluster kmeansCluster = new KMeansCluster();
				List<PointCluster> splitClusters = kmeansCluster.cluster(clusterPoints, 2);
				double sumSSE = 0.0;
				for (PointCluster splitCluster : splitClusters) {
					sumSSE += splitCluster.computeSSE();
				}
				if (sumSSE < minSSE) {
					minSSE = sumSSE;
					minClusters = splitClusters;
				}
			}
			if (null != minClusters) {
				clusters.addAll(minClusters);
			}
		}
		return clusters;
	}
	
	public void build() {
		List<Point> points = initData();
		List<PointCluster> clusters = cluster(points, 4);
		System.out.println("------result------");
		printClusters(clusters);
	}

	public static void main(String[] args) {
		BinaryKMeansCluster builder = new BinaryKMeansCluster();
		builder.build();
	}
	
}
