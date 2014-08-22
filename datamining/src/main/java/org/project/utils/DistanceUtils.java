package org.project.utils;

import java.util.Map;

import org.project.common.distance.CosineDistance;
import org.project.common.distance.EuclideanDistance;
import org.project.common.distance.ManhattanDistance;

public class DistanceUtils {

	/*
	 * 欧氏距离
	 */
	public static double euclidean(double[] p1, double[] p2) {
		return new EuclideanDistance().distance(p1, p2);
	}
	
	/*
	 * 欧氏距离
	 */
	public static double euclidean(Map<String, Double> p1, Map<String, Double> p2) {
		return new EuclideanDistance().distance(p1, p2);
	}

	/*
	 * 曼哈顿距离
	 */
	public static double manhattan(double[] p1, double[] p2) {
		return new ManhattanDistance().distance(p1, p2);
	}
	
	/*
	 * 曼哈顿距离
	 */
	public static double manhattan(Map<String, Double> p1, Map<String, Double> p2) {
		return new ManhattanDistance().distance(p1, p2);
	}

	/*
	 * 余弦距离
	 */
	public static double cosine(double[] p1, double[] p2) {
		return new CosineDistance().distance(p1, p2);
	}
	
	/*
	 * 余弦距离
	 */
	public static double cosine(Map<String, Double> p1, Map<String, Double> p2) {
		return new CosineDistance().distance(p1, p2);
	}

	/*
	 * 编辑距离 x和y的长度之和减去它们的最长公共子序列长度的两倍
	 */
	public static double edit(String x, String y) {
		String lcs = StringUtils.maxSubSeq(x, y);
		int lcsLen = null == lcs ? 0 : lcs.length();
		return x.length() + y.length() - lcsLen * 2;
	}
	
	public static void main(String[] args) {
		System.out.println(edit("aba", "bab"));
		System.out.println(edit("abcde", "acfdeg"));
	}
	
}
