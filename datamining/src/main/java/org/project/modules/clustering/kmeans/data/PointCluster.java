package org.project.modules.clustering.kmeans.data;

import java.util.ArrayList;
import java.util.List;

public class PointCluster {

	private Point center = null;
	
	private List<Point> points = null;

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public List<Point> getPoints() {
		if (null == points) {
			points = new ArrayList<Point>();
		}
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
	public Point computeMeansCenter() {
		int len = getPoints().size();
		double a = 0.0, b = 0.0;
		for (Point point : getPoints()) {
			a += point.getX();
			b += point.getY();
		}
		return new Point(a / len, b / len);
	}
	
	public Point computeMediodsCenter() {
		Point targetPoint = null;
		double distance = Integer.MAX_VALUE;
		for (Point point : getPoints()) {
			double d = 0.0;
			for (Point temp : getPoints()) {
				d += manhattanDistance(point, temp);
			}
			if (d < distance) {
				distance = d;
				targetPoint = point;
			}
		}
		return targetPoint;
	}
	
	public double computeSSE() {
		double result = 0.0;
		for (Point point : getPoints()) {
			result += euclideanDistance(point, center);
		}
		return result;
	}
	
	//计算两点之间的曼哈顿距离
	protected double manhattanDistance(Point a, Point b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}
	
	//计算两点之间的欧氏距离
	protected double euclideanDistance(Point a, Point b) {
		double sum =  Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
		return Math.sqrt(sum);
	}
	
}
