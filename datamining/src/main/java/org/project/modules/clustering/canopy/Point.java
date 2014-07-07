package org.project.modules.clustering.canopy;

public class Point {
	
	public static final int MARK_NULL = 0;
	public static final int MARK_STRONG = 0;
	public static final int MARK_WEAK = 0;

	private double x = 0.0;
	private double y = 0.0;
	private int mark = MARK_NULL;

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
	
	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return x + " - " + y;
	}
}