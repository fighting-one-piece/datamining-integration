package org.project.common.distance;

import java.util.Map;

import org.project.common.vector.Vector;

public interface IDistance {

	double distance(double[] p1, double[] p2);

	double distance(Vector<Double> v1, Vector<Double> v2);

	double distance(Map<String, Double> p1, Map<String, Double> p2);
	
}
