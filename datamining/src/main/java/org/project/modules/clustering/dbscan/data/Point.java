package org.project.modules.clustering.dbscan.data;

public class Point {
	
	private int clusterId = 0;

	private double x = 0.0;
	
	private double y = 0.0;
	
	private boolean isAccessed = false;
	
	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
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
	
	public boolean isAccessed() {
		return isAccessed;
	}

	public void setAccessed(boolean isAccessed) {
		this.isAccessed = isAccessed;
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
