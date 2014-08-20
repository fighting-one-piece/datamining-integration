package org.project.utils;

public class MathUtils {

	public static double log2(double value) {
		return Math.log(value) / Math.log(2);
	}
	
	public static double log10(double value) {
		return Math.log10(value);
	}
	
	public static double logn(double value, int n) {
		return Math.log(value) / Math.log(n);
	}
	
	public static double pow2(double value) {
		return Math.pow(value, 2);
	}
}
