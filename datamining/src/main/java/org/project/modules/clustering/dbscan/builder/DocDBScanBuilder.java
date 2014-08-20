package org.project.modules.clustering.dbscan.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentUtils;
import org.project.modules.clustering.dbscan.data.DataPoint;
import org.project.modules.clustering.kmeans.builder.DocKMediodsCluster;
import org.project.utils.DistanceUtils;

public class DocDBScanBuilder {
	
	//半径
	public static double Epislon = 0.2;
	//密度、最小点个数
	public static int MinPoints = 8;
	
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
	
	//获取当前点的邻居
	public List<DataPoint> obtainNeighbors(DataPoint current, List<DataPoint> points) {
		List<DataPoint> neighbors = new ArrayList<DataPoint>();
		for (DataPoint point : points) {
			double distance = DistanceUtils.euclidean(current.getValues(), point.getValues());
			System.out.println("distance: " + distance);
			if (distance < Epislon) {
				neighbors.add(point);
			}
		}
		return neighbors;
	}
	
	public void mergeCluster(DataPoint point, List<DataPoint> neighbors,
			int clusterId, List<DataPoint> points) {
		point.setClusterId(clusterId);
		for (DataPoint neighbor : neighbors) {
			//邻域点中未被访问的点先观察是否是核心对象
			//如果是核心对象，则其邻域范围内未被聚类的点归入当前聚类中
			if (!neighbor.isAccessed()) {
				neighbor.setAccessed(true);
				List<DataPoint> nneighbors = obtainNeighbors(neighbor, points);
				if (nneighbors.size() > MinPoints) {
					for (DataPoint nneighbor : nneighbors) {
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
	
	public void cluster(List<DataPoint> points) {
		//clusterId初始为0表示未分类,分类后设置为一个正数,如果设置为-1表示噪声 
		int clusterId = 0;
		boolean flag = true;
		//所有点都被访问完成即停止遍历
		while (flag) {
			for (DataPoint point : points) {
				if (point.isAccessed()) {
					continue;
				}
				point.setAccessed(true);
				flag = true;
				List<DataPoint> neighbors = obtainNeighbors(point, points);
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
	public void print(List<DataPoint> points) {
		Collections.sort(points, new Comparator<DataPoint>() {
			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				return Integer.valueOf(o1.getClusterId()).compareTo(o2.getClusterId());
			}
		});
		for (DataPoint point : points) {
			System.out.println(point.getClusterId() + " - " + point.getCategory());
		}
	}

	public void build() {
		List<DataPoint> points = initData();
		cluster(points);
		print(points);
	}
	
	public static void main(String[] args) {
		DocDBScanBuilder builder = new DocDBScanBuilder();
		builder.build();
	}
}
