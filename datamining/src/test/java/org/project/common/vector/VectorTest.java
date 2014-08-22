package org.project.common.vector;

import org.junit.Test;
import org.project.common.distance.IDistance;
import org.project.common.distance.EuclideanDistance;
import org.project.common.distance.ManhattanDistance;
import org.project.common.function.Functions;

public class VectorTest {

	@Test
	public void test() {
		Double[] values = new Double[]{1.0, 2.0};
		Vector<Double> vector = new DoubleVector(values);
		System.out.println(vector.getElement(0).get());
		System.out.println(vector.getElement(1).get());
		Vector<Double> newVector = vector.plus(1.0);
		System.out.println(newVector.getElement(0).get());
		System.out.println(newVector.getElement(1).get());
	}
	
	@Test 
	public void test1() {
		double[] v1 = new double[]{4.0, 4.0};
		double[] v2 = new double[]{5.0, 5.0};
		System.out.println(new ManhattanDistance().distance(v1, v2));
		System.out.println(new EuclideanDistance().distance(v1, v2));
		Vector<Double> dv1 = new DoubleVector(new Double[]{4.0, 4.0});
		Vector<Double> dv2 = new DoubleVector(new Double[]{5.0, 5.0});
		IDistance dm = new ManhattanDistance();
		System.out.println(dm.distance(dv1, dv2));
		dm = new EuclideanDistance();
		System.out.println(dm.distance(dv1, dv2));
		System.out.println(DoubleVector.aggregate(
				dv1, dv2, Functions.plus(), Functions.minusAbsPow(2)));
		
	}
}
