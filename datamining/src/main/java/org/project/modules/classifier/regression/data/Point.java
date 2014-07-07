package org.project.modules.classifier.regression.data;

public class Point {

	private double x = 0.0;
	
	private double y = 0.0;
	
	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public boolean equals(Point o) {
		if (x == o.getX() && y == o.getY()) 
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + x + " : " + y + "]";
	}
	
}
