package org.project.modules.clustering.dbscan.builder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.project.modules.clustering.dbscan.data.Point;

public class DBScanBuilder {
	
	//半径
	public static double Epislon = 1;
	//密度、最小点个数
	public static int MinPoints = 5;
	
	public List<Point<Double>> initData() {
		List<Point<Double>> points = new ArrayList<Point<Double>>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = DBScanBuilder.class.getClassLoader().getResourceAsStream("dbscan.txt");
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line && !"".equals(line)) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				double x = Double.parseDouble(tokenizer.nextToken());
				double y = Double.parseDouble(tokenizer.nextToken());
				points.add(new Point<Double>(x , y));
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
	
	//计算两点之间的欧氏距离
	public double euclideanDistance(Point<Double> a, Point<Double> b) {
		double sum =  Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
		return Math.sqrt(sum);
	}
	
	//获取当前点的邻居
	public List<Point<Double>> obtainNeighbors(Point<Double> current, List<Point<Double>> points) {
		List<Point<Double>> neighbors = new ArrayList<Point<Double>>();
		for (Point<Double> point : points) {
			double distance = euclideanDistance(current, point);
			if (distance < Epislon) {
				neighbors.add(point);
			}
		}
		return neighbors;
	}
	
	public void mergeCluster(Point<Double> point, List<Point<Double>> neighbors,
			int clusterId, List<Point<Double>> points) {
		point.setClusterId(clusterId);
		for (Point<Double> neighbor : neighbors) {
			//邻域点中未被访问的点先观察是否是核心对象
			//如果是核心对象，则其邻域范围内未被聚类的点归入当前聚类中
			if (!neighbor.isAccessed()) {
				neighbor.setAccessed(true);
				List<Point<Double>> nneighbors = obtainNeighbors(neighbor, points);
				if (nneighbors.size() > MinPoints) {
					for (Point<Double> nneighbor : nneighbors) {
						if (nneighbor.getClusterId() <= 0) {
							nneighbor.setClusterId(clusterId);
						}
					}
				}
			}
			//未被聚类的点归入当前聚类中
			if (neighbor.getClusterId() <= 0) {
				neighbor.setClusterId(clusterId);
			}
		}
	}
	
	public void cluster(List<Point<Double>> points) {
		//clusterId初始为0表示未分类,分类后设置为一个正数,如果设置为-1表示噪声 
		int clusterId = 0;
		boolean flag = true;
		//所有点都被访问完成即停止遍历
		while (flag) {
			for (Point<Double> point : points) {
				if (point.isAccessed()) {
					continue;
				}
				point.setAccessed(true);
				flag = true;
				List<Point<Double>> neighbors = obtainNeighbors(point, points);
				System.out.println("neighbors: " + neighbors.size());
				if (neighbors.size() >= MinPoints) {
					//满足核心对象条件的点创建一个新簇
					clusterId = point.getClusterId() <= 0 ? (++clusterId) : point.getClusterId();
					System.out.println("--clusterId: " + clusterId);
					mergeCluster(point, neighbors, clusterId, points);
				} else {
					//未满足核心对象条件的点暂时当作噪声处理
					if(point.getClusterId() <= 0) {
						 point.setClusterId(-1);
					}
				}
				flag = false;
			}
		}
	}
	
	//打印结果
	public void print(List<Point<Double>> points) {
		Collections.sort(points, new Comparator<Point<Double>>() {
			@Override
			public int compare(Point<Double> o1, Point<Double> o2) {
				return Integer.valueOf(o1.getClusterId()).compareTo(o2.getClusterId());
			}
		});
		for (Point<Double> point : points) {
			System.out.println(point.getClusterId() + " - " + point);
		}
	}

	public void build() {
		List<Point<Double>> points = initData();
		cluster(points);
		print(points);
	}
	
	public static void main(String[] args) {
		DBScanBuilder builder = new DBScanBuilder();
		builder.build();
	}
}
