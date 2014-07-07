package org.project.common.distance;

import java.util.Iterator;

import org.project.common.vector.Vector;

/** 曼哈顿距离*/
public class ManhattanDistanceMeasure implements DistanceMeasure {

	@Override
	public double distance(Vector<Double> v1, Vector<Double> v2) {
		if (v1.size() != v2.size()) {
			throw new RuntimeException("v1 size not equal v2 size");
		}
		Iterator<Vector.Element<Double>> v1Iter = v1.all().iterator();
		Iterator<Vector.Element<Double>> v2Iter = v2.all().iterator();
		double result = 0.0;
		while (v1Iter.hasNext()) {
			result += Math.abs(v1Iter.next().get() - v2Iter.next().get());
		}
		return result;
	}

	public static double distance(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.abs(p2[i] - p1[i]);
		}
		return result;
	}

}
