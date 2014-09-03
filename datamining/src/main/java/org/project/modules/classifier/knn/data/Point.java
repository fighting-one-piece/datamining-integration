package org.project.modules.classifier.knn.data;

public class Point {

	private double x;
	
	private double y;
	
	private String category = null;
	
	public Point() {
		super();
	}

	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point(double x, double y, String category) {
		super();
		this.x = x;
		this.y = y;
		this.category = category;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
