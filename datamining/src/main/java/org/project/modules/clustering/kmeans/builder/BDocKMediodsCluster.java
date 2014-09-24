package org.project.modules.clustering.kmeans.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.project.common.distance.EuclideanDistance;
import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentUtils;
import org.project.modules.algorithm.featureselect.FSChiSquare;
import org.project.modules.algorithm.featureselect.FSExpectedCrossEntropy;
import org.project.modules.algorithm.featureselect.FSInformationGain;
import org.project.modules.algorithm.featureselect.IFeatureSelect;
import org.project.modules.algorithm.lcs.LCS;
import org.project.modules.clustering.kmeans.data.DataPoint;
import org.project.modules.clustering.kmeans.data.DataPointCluster;
import org.project.utils.DistanceUtils;

public class BDocKMediodsCluster extends AbstractCluster {
	
	//阀值 CHI
	public static final double THRESHOLD = 0.028;
	//阀值 ECE
//	public static final double THRESHOLD = 0.12;
	//阀值 IG
//	public static final double THRESHOLD = 0.12;
	//迭代次数
	public static final int ITER_NUM = 10;
	//开方检验词限制
	public static int CHI_WORD_LIMIT = 100;
	//期望交叉熵词限制
	public static int ECE_WORD_LIMIT = 700;
	//信息增益词限制
	public static int IG_WORD_LIMIT = 600;
	
	/*
	 * 初始化数据
	 * 先通过开方检验进行降维
	 */
	public List<DataPoint> initData() {
		long start = System.currentTimeMillis();
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		try {
			String path = "D:\\resources\\data\\res1\\";
			DocumentSet documentSet = DocumentLoader.loadDocumentSetByThread(path);
			reduceDimensionsByCHI(documentSet);
//			reduceDimensionsByECE(documentSet);
//			reduceDimensionsByIG(documentSet);
			//计算TFIDF
			List<Document> documents = documentSet.getDocuments();
			DocumentUtils.calculateTFIDF_0(documents);
			for(Document document : documents) {
				DataPoint dataPoint = new DataPoint();
				dataPoint.setValues(document.getTfidfWords());
				dataPoint.setCategory(document.getName());
				dataPoints.add(dataPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long cost = System.currentTimeMillis() - start;
		System.out.println("spend time : " + (cost / 100));
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
		Map<String, Double> eceWords = documentSet.getFeatureSelect();
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
		Map<String, Double> eceWords = documentSet.getFeatureSelect();
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
	
	//随机生成中心点，并生成初始的K个聚类
	public List<DataPointCluster> genInitCluster(List<DataPoint> points, int k) {
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		Random random = new Random();
		Set<String> categories = new HashSet<String>();
		while (clusters.size() < k) {
			DataPoint center = points.get(random.nextInt(points.size()));
			String category = center.getCategory();
			if (categories.contains(category)) continue;
			categories.add(category);
			DataPointCluster cluster = new DataPointCluster();
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	public List<DataPointCluster> genInitCluster1(List<DataPoint> dataPoints, int k) {
		List<DataPoint> temp = new ArrayList<DataPoint>();
		for (DataPoint dataPoint : dataPoints) {
			temp.add(dataPoint);
		}
		int len = temp.size();
		List<DataPointCluster> clusters = new ArrayList<DataPointCluster>();
		DataPointCluster cluster = new DataPointCluster();
		DataPoint center = temp.remove(new Random().nextInt(len));
		cluster.setCenter(center);
		cluster.getDataPoints().add(center);
		clusters.add(cluster);
		while (clusters.size() < k) {
			DataPoint tempCenter = computeMediodsCenter(temp);
			double maxDistance = Integer.MIN_VALUE;
			for (DataPoint dataPoint : temp) {
				double distance = new EuclideanDistance().distance(
						dataPoint.getValues(), tempCenter.getValues());
				if (distance > maxDistance) {
					maxDistance = distance;
					center = dataPoint;
				}
			}
			cluster = new DataPointCluster();
			temp.remove(center);
			cluster.setCenter(center);
			cluster.getDataPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	public DataPoint computeMediodsCenter(List<DataPoint> dataPoints) {
		DataPoint centerPoint = null;
		double distance = Integer.MAX_VALUE;
		for (DataPoint dataPoint : dataPoints) {
			double d = 0.0;
			for (DataPoint temp : dataPoints) {
				d += new EuclideanDistance().distance(dataPoint.getValues(), temp.getValues());
			}
			if (d < distance) {
				distance = d;
				centerPoint = dataPoint;
			}
		}
		return centerPoint;
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
		if (!flag && iterNum < ITER_NUM) {
			for (DataPointCluster cluster : clusters) {
				cluster.getDataPoints().clear();
			}
			handleCluster(points, clusters, ++iterNum);
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
	
	private String lcs(List<String> alist) {
      Map<String,Integer> cmap = new HashMap<String, Integer>();
      int len = alist.size();
      for(int i=0;i<len;i++){
          for(int j=i+1;j<len;j++){
//        	  String substr = StringUtils.maxSubSeq(alist.get(i), alist.get(j));
        	  String substr = LCS.lcseq(alist.get(i), alist.get(j));
              cmap.put(substr.trim(),cmap.containsKey(substr)?cmap.get(substr)+1:1);
          }
      }
      List<Map.Entry<String,Integer>> clist = new ArrayList<Map.Entry<String,Integer>>(cmap.entrySet());
      Collections.sort(clist,new Comparator<Map.Entry<String, Integer>>() {
          @Override
          public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
              return o2.getValue()-o1.getValue();
          }
      });

      String rtag = "";
      for(Map.Entry<String,Integer> o:clist){
          String tag = o.getKey();
          if(tag.length()>=4){
              rtag = tag;
              break;
          }
      }
      return rtag;
  }
	
	public void build() {
		List<DataPoint> points = initData();
		List<DataPointCluster> clusters = genInitCluster1(points, 4);
		for (DataPointCluster cluster : clusters) {
			System.out.println("center: " + cluster.getCenter().getCategory());
		}
		handleCluster(points, clusters, 0);
		for (DataPointCluster cluster : clusters) {
			String category = cluster.getCenter().getCategory();
			System.out.println("center: " + category + "--" + cluster.getDataPoints().size());
			List<String> titles = new ArrayList<String>();
			for (DataPoint dataPoint : cluster.getDataPoints()) {
				String dpCategory = dataPoint.getCategory();
				System.out.println(dpCategory);
				titles.add(dpCategory);
			}
			System.out.println("lcs: " + lcs(titles));
			System.out.println("-------------------");
		}
	}
	
	public static void main(String[] args) {
		new BDocKMediodsCluster().build();
		System.exit(0);
	}
	
}
