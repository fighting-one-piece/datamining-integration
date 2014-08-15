package org.project.modules.clustering.kmeans.builder;

import java.util.List;

import org.project.modules.clustering.kmeans.data.DataPoint;
import org.project.modules.clustering.kmeans.data.DataPointCluster;
import org.project.modules.clustering.kmeans.data.Point;
import org.project.modules.clustering.kmeans.data.PointCluster;

public abstract class AbstractCluster {
	
	//计算两点之间的曼哈顿距离
	protected double manhattanDistance(Point a, Point b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}
	
	//计算两点之间的欧氏距离
	protected double euclideanDistance(Point a, Point b) {
		double sum =  Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
		return Math.sqrt(sum);
	}
	
	protected void printClusters(List<PointCluster> clusters) {
		for (PointCluster cluster : clusters) {
			System.out.println("center: " + cluster.getCenter());
			System.out.println("cluster size: " + cluster.getPoints().size());
			for (Point point : cluster.getPoints()) {
				System.out.println(point);
			}
		}
	}
	
	protected void printDataPointClusters(List<DataPointCluster> clusters) {
		for (DataPointCluster cluster : clusters) {
			System.out.println("center: " + cluster.getCenter());
			System.out.println("cluster size: " + cluster.getDataPoints().size());
			for (DataPoint point : cluster.getDataPoints()) {
				System.out.println(point.getCategory());
			}
		}
	}
}
