package org.project.modules.classifier.regression.builder;

import java.util.ArrayList;
import java.util.List;

import org.project.modules.classifier.regression.data.Point;

public class LeastSquaresBuilder {
	
	//初始化数据
	private List<Point> initializeData() {
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(1, 110));
		points.add(new Point(2, 120));
		points.add(new Point(3, 130));
		points.add(new Point(4, 140));
		points.add(new Point(5, 150));
		points.add(new Point(6, 160));
		points.add(new Point(7, 170));
		points.add(new Point(8, 180));
		points.add(new Point(9, 190));
		return points;
	}
	
	/**
	 * Y = a0 + a1 * X 
	 * a0 = ∑Yi / m - a1 * ∑Xi / m
	 * a1 = [m∑(XiYi) - ∑Xi * ∑Yi] / [m∑(Xi^2) - (∑Xi)^2]
	 * 计算a0、a1
	 */
	private double[] calculate(List<Point> points) {
		double[] xParameters = extract(points, "X");
		double[] yParameters = extract(points, "Y");
		double m = points.size();
		double xSum = sum(xParameters, false);
		double ySum = sum(yParameters, false);
		double xySum = sum(xParameters, yParameters);
		double xxSum = sum(xParameters, true);
		double p1 = m * xySum - xSum * ySum;
		double p2 = m * xxSum - Math.pow(xSum, 2);
		double a1 =  p1 / p2;
		double a0 = ySum / m - a1 * xSum / m;
		System.out.println("Y = " + a0 + " + " + a1 + " * X");
		return new double[]{a0 , a1};
	}
	
	//预测X的值Y
	private double predict(double[] a0_a1, double x) {
		if (a0_a1.length != 2) {
			throw new RuntimeException("error");
		}
		return a0_a1[0] + a0_a1[1] * x;
	}
	
	//计算∑(Xi) 或则 ∑(Xi^2)
	private double sum(double[] parameters, boolean isSquare) {
		double result = 0.0;
		for (double parameter : parameters) {
			result += isSquare ? Math.pow(parameter, 2) : parameter;
		}
		return result;
	}
	
	//计算∑(XiYi)
	private double sum(double[] xParameters, double[] yParameters) {
		double result = 0.0;
		for (int i = 0, len = xParameters.length; i < len; i++) {
			result += xParameters[i] * yParameters[i];
		}
		return result;
	}
	
	//抽取某一维度点集合
	private double[] extract(List<Point> points, String type) {
		double[] result = new double[points.size()];
		int index = 0;
		for (Point point : points) {
			if ("X".equals(type)) {
				result[index++] = point.getX();
			} else if ("Y".equals(type)) {
				result[index++] = point.getY();
			}
		}
		return result;
	}
	
	public void build() {
		List<Point> points = initializeData();
		double[] a0_a1 = calculate(points);
		double x = 10;
		double result = predict(a0_a1, x);
		System.out.println("predict " + x + " result: " + result);
	}

	public static void main(String[] args) {
		LeastSquaresBuilder builder = new LeastSquaresBuilder();
		builder.build();
	}
}
