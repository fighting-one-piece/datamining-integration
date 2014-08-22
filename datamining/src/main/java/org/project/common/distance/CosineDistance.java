package org.project.common.distance;

import java.util.Map;

import org.project.common.vector.Vector;

/**
 * 余弦距离
 */
public class CosineDistance implements IDistance {

	@Override
	public double distance(double[] p1, double[] p2) {
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
	
	@Override
	public double distance(Vector<Double> v1, Vector<Double> v2) {
		return 0;
	}
	
	@Override
	public double distance(Map<String, Double> p1, Map<String, Double> p2) {
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
	
}
