package org.project.common.distance;

import java.util.Iterator;

import org.project.common.vector.Vector;

/** 欧氏距离*/
public class EuclideanDistanceMeasure implements DistanceMeasure {

	@Override
	public double distance(Vector<Double> v1, Vector<Double> v2) {
		if (v1.size() != v2.size()) {
			throw new RuntimeException("v1 size not equal v2 size");
		}
		Iterator<Vector.Element<Double>> v1Iter = v1.all().iterator();
		Iterator<Vector.Element<Double>> v2Iter = v2.all().iterator();
		double result = 0.0;
		while (v1Iter.hasNext()) {
			result += Math.pow((v1Iter.next().get() - v2Iter.next().get()), 2);
		}
		return Math.sqrt(result);
	}
	
	public static double distance(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.pow((p2[i] - p1[i]), 2);
		}
		return Math.sqrt(result);
	}
}
