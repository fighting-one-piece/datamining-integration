package org.project.common.distance;

import org.project.common.vector.Vector;

public interface DistanceMeasure {

	double distance(Vector<Double> v1, Vector<Double> v2);
}
