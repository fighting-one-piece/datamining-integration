package org.project.modules.clustering.dbscan.data;

public class Point<T> {
	
	private int clusterId = 0;

	private T x = null;
	
	private T y = null;
	
	private boolean isAccessed = false;
	
	public Point(T x, T y) {
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

	public T getX() {
		return x;
	}

	public void setX(T x) {
		this.x = x;
	}

	public T getY() {
		return y;
	}

	public void setY(T y) {
		this.y = y;
	}
	
	public boolean isAccessed() {
		return isAccessed;
	}

	public void setAccessed(boolean isAccessed) {
		this.isAccessed = isAccessed;
	}

	public boolean equals(Point<T> o) {
		if (x.equals(o.getX()) && y.equals(o.getY())) 
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + x + " : " + y + "]";
	}
}
