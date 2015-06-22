package org.project.modules.algorithm.recommend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.project.modules.algorithm.recommend.data.DataLoader;
import org.project.modules.algorithm.recommend.data.Movie;

public class BasedItemRecommend {
	
	//最近最相邻项目数量
	public static final int NEAREST_ITEM = 100;
	//推荐结果数量
	public static final int RECOMMEND_NUM = 10;
	//电影映射
	private Map<Long, Movie> movieMap = DataLoader.getMovieMap();
	//用户电影映射
	private Map<Long, Map<Long, Integer>> userMovies = DataLoader.getUserMovieMap();
	//电影用户映射
	private Map<Long, Map<Long, Integer>> movieUsers = DataLoader.getMovieUserMap();
	
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
	
	//计算距离当前项目最近的N个项目
	public List<Map.Entry<Long, Double>> calNearestItem(Long movieId) {
		Map<Long, Integer> curUserRating = movieUsers.get(movieId);
		Map<Long, Double> movieRatingMap = new HashMap<Long, Double>();
		for (Map.Entry<Long, Map<Long, Integer>> entry : movieUsers.entrySet()) {
			Map<Long, Integer> userRating = entry.getValue();
			double distance = cosine(curUserRating, userRating);
			movieRatingMap.put(entry.getKey(), distance);
		}
		List<Map.Entry<Long, Double>> list = new ArrayList<Map.Entry<Long, Double>>(movieRatingMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Long, Double>>() {
			@Override
			public int compare(Entry<Long, Double> o1, Entry<Long, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return list.subList(0, NEAREST_ITEM);
	}
	
	//预测未评分项目的评分
	public double predictRating(Long userId, Long unratingMovieId) {
		double similaritySum = 0.0, ratingSimilaritySum = 0.0;
		Map<Long, Integer> movieRatings = userMovies.get(userId);
		for (Map.Entry<Long, Integer> entry : movieRatings.entrySet()) {
			Long ratingMovieId = entry.getKey();
			int userRating = entry.getValue();
			System.out.println("urating: " + userRating);
			Map<Long, Integer> rating1 = movieUsers.get(ratingMovieId);
			Map<Long, Integer> rating2 = movieUsers.get(unratingMovieId);
			double similarity = 0;
			if (null != rating1 && null != rating2) {
				similarity = cosine(rating1, rating2);
			}
			similaritySum += similarity;
			ratingSimilaritySum += similarity * userRating;
		}
		if (similaritySum == 0) return 0;
		return ratingSimilaritySum / similaritySum;
	}

	//根据用户当前数据推荐项目
	public void recommend(Long userId) {
		Set<Long> userRatingMovieIds = userMovies.get(userId).keySet();
		Set<Long> movieIds = movieMap.keySet();
		Set<Long> userUnratingMovieIds = new HashSet<Long>();
		for (Long movieId : movieIds) {
			if (userRatingMovieIds.contains(movieId)) continue;
			userUnratingMovieIds.add(movieId);
		}
		Map<Long, Double> movieRatingMap = new HashMap<Long, Double>();
		for (Long userUnratingMovieId : userUnratingMovieIds) {
			double rating = predictRating(userId, userUnratingMovieId);
			System.out.println("rating: " + rating);
			movieRatingMap.put(userUnratingMovieId, rating);
		}
		List<Map.Entry<Long, Double>> movieRatingList = 
				new ArrayList<Map.Entry<Long, Double>>(movieRatingMap.entrySet());
		Collections.sort(movieRatingList, new Comparator<Map.Entry<Long, Double>>() {
			@Override
			public int compare(Map.Entry<Long, Double> o1, Map.Entry<Long, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		for (int i = 0, len = movieRatingList.size(); i < len; i++) {
			Map.Entry<Long, Double> movieRating = movieRatingList.get(i);
			System.out.println("movie: " + movieRating.getKey() + " rating: " + movieRating.getValue());
			if (i == RECOMMEND_NUM) break;
		}
	}
	
	public static void main(String[] args) {
		new BasedItemRecommend().recommend(10L);
	}
	
}
