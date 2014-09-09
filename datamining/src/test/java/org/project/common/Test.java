package org.project.common;

import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		List<P> p1s = new ArrayList<P>();
		p1s.add(new P(1.0, 1.0, "one"));
		p1s.add(new P(2.0, 2.0, "two"));
		p1s.add(new P(3.0, 3.0, "three"));
		List<P> p2s = new ArrayList<P>();
		for (P p  : p1s) {
			p2s.add(p);
		}
		for (P p : p2s) {
			System.out.println("p2s: " + p.getX() + "-" + p.getY() + "-" + p.getCategory());
		}
	}
}

class P {
	
	private Double x = null;
	
	private Double y = null;
	
	private String category = null;
	
	public P(double x, double y, String category) {
		this.x = new Double(x);
		this.y = new Double(y);
		this.category = new String(category);
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}


}