package org.project.modules.clustering.dbscan.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentUtils;
import org.project.modules.algorithm.featureselect.FSChiSquare;
import org.project.modules.algorithm.featureselect.FSExpectedCrossEntropy;
import org.project.modules.algorithm.featureselect.FSInformationGain;
import org.project.modules.algorithm.featureselect.IFeatureSelect;
import org.project.modules.clustering.dbscan.data.DataPoint;
import org.project.modules.clustering.kmeans.builder.DocKMediodsCluster;
import org.project.utils.DistanceUtils;

public class DocDBScanBuilder {
	
	//半径
	public static double EPISLON = 0.04;
	//密度、最小点个数
	public static int MIN_POINTS = 15;
	//开方检验词限制
	public static int CHI_WORD_LIMIT = 100;
	//期望交叉熵词限制
	public static int ECE_WORD_LIMIT = 600;
	//信息增益词限制
	public static int IG_WORD_LIMIT = 600;
	
	//初始化数据
	public List<DataPoint> initData() {
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		try {
			String path = DocKMediodsCluster.class.getClassLoader().getResource("测试").toURI().getPath();
			DocumentSet documentSet = DocumentLoader.loadDocumentSet(path);
			reduceDimensionsByCHI(documentSet);
//			reduceDimensionsByECE(documentSet);
//			reduceDimensionsByIG(documentSet);
			//计算TFIDF
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			for(Document doc : documents) {
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
	
	//期望交叉熵特征选择降维
	public void reduceDimensionsByECE(DocumentSet documentSet) {
		IFeatureSelect featureSelect = new FSExpectedCrossEntropy();
		featureSelect.handle(documentSet);
		Map<String, Double> eceWords = documentSet.getSelectedFeatures();
		List<Map.Entry<String, Double>> list = sortMap(eceWords);
		int len = list.size() < ECE_WORD_LIMIT ? list.size() : ECE_WORD_LIMIT;
		List<String> wordList = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
			wordList.add(list.get(i).getKey());
		}
		List<Document> documents = documentSet.getDocuments();
		for (Document document : documents) {
			Set<String> wordSet = document.getWordSet();
			Iterator<String> iter = wordSet.iterator();
			while (iter.hasNext()) {
				String word = iter.next();
				if (!wordList.contains(word)) {
					iter.remove();
				}
			}
			document.setWords(wordSet.toArray(new String[0]));
		}
	}
	
	//信息增益特征选择降维
	public void reduceDimensionsByIG(DocumentSet documentSet) {
		IFeatureSelect featureSelect = new FSInformationGain();
		featureSelect.handle(documentSet);
		Map<String, Double> eceWords = documentSet.getSelectedFeatures();
		List<Map.Entry<String, Double>> list = sortMap(eceWords);
		int len = list.size() < IG_WORD_LIMIT ? list.size() : IG_WORD_LIMIT;
		List<String> wordList = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
			wordList.add(list.get(i).getKey());
		}
		List<Document> documents = documentSet.getDocuments();
		for (Document document : documents) {
			Set<String> wordSet = document.getWordSet();
			Iterator<String> iter = wordSet.iterator();
			while (iter.hasNext()) {
				String word = iter.next();
				if (!wordList.contains(word)) {
					iter.remove();
				}
			}
			document.setWords(wordSet.toArray(new String[0]));
		}
	}
	
	//获取当前点的邻居
	public List<DataPoint> obtainNeighbors(DataPoint current, List<DataPoint> points) {
		List<DataPoint> neighbors = new ArrayList<DataPoint>();
		for (DataPoint point : points) {
			double distance = DistanceUtils.cosine(current.getValues(), point.getValues());
//			System.out.println("distance: " + distance);
			if (distance > EPISLON) {
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
				if (nneighbors.size() > MIN_POINTS) {
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
				System.out.println("----------------------------neighbors: " + neighbors.size());
				if (neighbors.size() >= MIN_POINTS) {
					//满足核心对象条件的点创建一个新簇
					clusterId = point.getClusterId() <= 0 ? (++clusterId) : point.getClusterId();
					System.out.println("--------------------------------clusterId: " + clusterId);
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
