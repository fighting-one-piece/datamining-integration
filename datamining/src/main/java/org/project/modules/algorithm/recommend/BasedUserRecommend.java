package org.project.modules.algorithm.recommend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.project.modules.algorithm.recommend.data.DataLoader;

public class BasedUserRecommend {
	
	//最近最相邻用户数量
	public static final int NEAREST_USER = 100;
	//推荐结果数量
	public static final int RECOMMEND_NUM = 100;
	//用户电影映射
	private Map<Long, Map<Long, Integer>> userMovies = DataLoader.getUserMovieMap();
	
	//欧氏距离计算
	public double euclidean(Map<Long, Integer> p1, Map<Long, Integer> p2) {
		double result = 0.0;
		for (Map.Entry<Long, Integer> entry : p1.entrySet()) {
			int v1 = entry.getValue();
			Long k1 = entry.getKey();
			int v2 = null == p2.get(k1) ? 0 : p2.get(k1);
			result += Math.pow(v1 - v2, 2);
		}
		return Math.sqrt(result);
	}

	//余弦距离计算
	public double cosine(Map<Long, Integer> p1, Map<Long, Integer> p2) {
		double x = 0, y = 0, z = 0;
		for (Map.Entry<Long, Integer> entry : p1.entrySet()) {
			Long k1 = entry.getKey();
			int v1 = entry.getValue();
			if (p2.containsKey(k1)) {
				x += v1 * p2.get(k1);
			}
			y += Math.pow(v1, 2);
		}
		for (Map.Entry<Long, Integer> entry : p2.entrySet()) {
			z += Math.pow(entry.getValue(), 2);
		}
		if (y == 0 || z ==0) return 0;
		return x / (Math.pow(y, 0.5) * Math.pow(z, 0.5));
	}
	
	//计算距离当前用户最近的N的用户
	public List<Map.Entry<Long, Double>> calNearestUser(Long userId) {
		Map<Long, Integer> curMovieRatings = userMovies.get(userId);
		Map<Long, Double> userDistance = new HashMap<Long, Double>();
		for (Map.Entry<Long, Map<Long, Integer>> entry : userMovies.entrySet()) {
			Map<Long, Integer> movieRating = entry.getValue();
			double distance = cosine(curMovieRatings, movieRating);
			userDistance.put(entry.getKey(), distance);
		}
		List<Map.Entry<Long, Double>> list = new ArrayList<Map.Entry<Long, Double>>(userDistance.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Long, Double>>() {
			@Override
			public int compare(Entry<Long, Double> o1, Entry<Long, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return list.subList(0, NEAREST_USER);
	}

	//根据用户最近最相邻的用户获取最后推荐结果
	public void recommend(Long userId) {
		Set<Long> userMovieIds = userMovies.get(userId).keySet();
		Map<Long, Double> map = new HashMap<Long, Double>();
		List<Map.Entry<Long, Double>> nearestUsers = calNearestUser(userId); 
		for (Map.Entry<Long, Double> nearestUser : nearestUsers) {
			Long nearestUserId = nearestUser.getKey();
			double similarity = nearestUser.getValue();
			Map<Long, Integer> movieRatings = userMovies.get(nearestUserId);
			for (Map.Entry<Long, Integer> movieRating : movieRatings.entrySet()) {
				Long movieId = movieRating.getKey();
				if (userMovieIds.contains(movieId)) continue; 
				Double value = map.get(movieId);
				if (null == value) {
					map.put(movieId, similarity);
				} else {
					map.put(movieId, value + similarity);
				}
			}
		}
		List<Map.Entry<Long, Double>> list = new ArrayList<Map.Entry<Long, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Long, Double>>() {
			@Override
			public int compare(Entry<Long, Double> o1, Entry<Long, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		System.out.println("---recommend result---");
		for (int i = 0, len = list.size(); i < len; i++) {
			Map.Entry<Long, Double> entry = list.get(i);
			System.out.println(entry.getKey() + " : " + entry.getValue());
			if (i == RECOMMEND_NUM) break;
		}
	}
	
	public static void main(String[] args) {
		new BasedUserRecommend().recommend(1L);
	}
	
}
