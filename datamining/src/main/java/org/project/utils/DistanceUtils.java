package org.project.utils;

import java.util.Map;

public class DistanceUtils {

	/*
	 * 欧氏距离
	 */
	public static double euclidean(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.pow((p2[i] - p1[i]), 2);
		}
		return Math.sqrt(result);
	}

	/*
	 * 曼哈顿距离
	 */
	public static double manhattan(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.abs(p2[i] - p1[i]);
		}
		return result;
	}

	/*
	 * 余弦距离
	 */
	public static double cosine(double[] p1, double[] p2) {
		double a = 0, b = 0, c = 0;
		for (int i = 0; i < p1.length; i++) {
			a += p1[i] * p2[i];
			b += Math.pow(p1[i], 2);
			c += Math.pow(p2[i], 2);
		}
		b = Math.sqrt(b);
		c = Math.sqrt(c);
		if (b == 0 || c ==0) return 0;
		return a / (b * c);
	}
	
	/*
	 * 余弦距离
	 */
	public static double cosine(Map<String, Double> p1, Map<String, Double> p2) {
		double x = 0, y = 0, z = 0;
		for (Map.Entry<String, Double> entry : p1.entrySet()) {
			String k1 = entry.getKey();
			double v1 = entry.getValue();
			if (p2.containsKey(k1)) {
				x += v1 * p2.get(k1);
			}
			y += Math.pow(v1, 2);
		}
		for (Map.Entry<String, Double> entry : p2.entrySet()) {
			z += Math.pow(entry.getValue(), 2);
		}
		if (y == 0 || z ==0) return 0;
		return x / (Math.pow(y, 0.5) * Math.pow(z, 0.5));
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
