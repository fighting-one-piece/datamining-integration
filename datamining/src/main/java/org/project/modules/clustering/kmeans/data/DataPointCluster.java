package org.project.modules.clustering.kmeans.data;

import java.util.ArrayList;
import java.util.List;

import org.project.utils.DistanceUtils;

public class DataPointCluster {

	private DataPoint center = null;
	
	private List<DataPoint> points = null; 

	public DataPoint getCenter() {
		return center;
	}

	public void setCenter(DataPoint center) {
		this.center = center;
	}

	public List<DataPoint> getDataPoints() {
		if (null == points) {
			points = new ArrayList<DataPoint>();
		}
		return points;
	}

	public void setDataPoints(List<DataPoint> points) {
		this.points = points;
	}
	
	public DataPoint computeMeansCenter() {
		int len = getDataPoints().size();
		if (len == 0) {
			throw new RuntimeException("no datapoint");
		}
		if (len == 1) {
			return getDataPoints().get(0);
		}
		int vLen = getDataPoints().get(0).getValues().length;
		double[] temp = new double[vLen];
		for (DataPoint point : getDataPoints()) {
			double[] values = point.getValues();
			for (int i = 0; i < vLen; i++) {
				temp[i] += values[i];
			}
		}
		for (int i = 0; i < vLen; i++) {
			temp[i] = temp[i] / len;
		}
		return new DataPoint(temp);
	}
	
	public DataPoint computeMediodsCenter() {
		DataPoint targetDataPoint = null;
		double distance = Integer.MAX_VALUE;
		for (DataPoint point : getDataPoints()) {
			double d = 0.0;
			for (DataPoint temp : getDataPoints()) {
				d += DistanceUtils.manhattan(
						point.getValues(), temp.getValues());
			}
			if (d < distance) {
				distance = d;
				targetDataPoint = point;
			}
		}
		return targetDataPoint;
	}
	
	public double computeSSE() {
		double result = 0.0;
		for (DataPoint point : getDataPoints()) {
			result += DistanceUtils.euclidean(point.getValues(), center.getValues());
		}
		return result;
	}
	
	
}
