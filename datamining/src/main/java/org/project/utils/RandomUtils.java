package org.project.utils;

import java.math.BigDecimal;
import java.util.Random;

public class RandomUtils {

	private static Random random = new Random();

	private RandomUtils() {

	}

	public static int nextInt() {
		return random.nextInt();
	}

	public static int nextInt(int n) {
		return random.nextInt(n);
	}

	public static int nextInt(double n) {
		BigDecimal decimal = new BigDecimal(n).setScale(0, BigDecimal.ROUND_HALF_UP);
		return random.nextInt(decimal.intValue());
	}

	public static double nextDouble() {
		return random.nextDouble();
	}

	public static void main(String[] args) {
		System.out.println("四舍五入取整:(2.1)="
				+ new BigDecimal("2.1").setScale(0, BigDecimal.ROUND_HALF_UP));
		System.out.println("四舍五入取整:(2.5)="
				+ new BigDecimal("2.5").setScale(0, BigDecimal.ROUND_HALF_UP));
		System.out.println("四舍五入取整:(2.9)="
				+ new BigDecimal("2.9").setScale(0, BigDecimal.ROUND_HALF_UP));
		BigDecimal a = new BigDecimal("2.9").setScale(0, BigDecimal.ROUND_HALF_UP);
		System.out.println(a.intValue());
		int x = 1, y = 2;
		x = x ^ y;
		y = x ^ y;
		x = x ^ y;
		System.out.println(x + ":" + y);
	}
}
