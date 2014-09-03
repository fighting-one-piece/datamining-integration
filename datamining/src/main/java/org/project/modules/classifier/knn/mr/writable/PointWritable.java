package org.project.modules.classifier.knn.mr.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.project.modules.classifier.knn.data.Point;

public class PointWritable implements WritableComparable<PointWritable> {

	private DoubleWritable x = null;
	
	private DoubleWritable y = null;
	
	private Text category = null;
	
	private DoubleWritable distance = null;
	
	public PointWritable() {
		this.x = new DoubleWritable();
		this.y = new DoubleWritable();
		this.category = new Text();
		this.distance = new DoubleWritable();
	}
	
	public PointWritable(Point point, double distance) {
		this.x = new DoubleWritable(point.getX());
		this.y = new DoubleWritable(point.getY());
		this.category = new Text(point.getCategory());
		this.distance = new DoubleWritable(distance);
	}
	
	public PointWritable(double x, double y) {
		this.x = new DoubleWritable(x);
		this.y = new DoubleWritable(y);
		this.category = new Text();
		this.distance = new DoubleWritable();
	}
	
	public PointWritable(double x, double y, String category) {
		this.x = new DoubleWritable(x);
		this.y = new DoubleWritable(y);
		this.category = new Text(category);
		this.distance = new DoubleWritable();
	}
	
	public PointWritable(double x, double y, String category, double distance) {
		this.x = new DoubleWritable(x);
		this.y = new DoubleWritable(y);
		this.category = new Text(category);
		this.distance = new DoubleWritable(distance);
	}

	public DoubleWritable getX() {
		return x;
	}

	public void setX(DoubleWritable x) {
		this.x = x;
	}

	public DoubleWritable getY() {
		return y;
	}

	public void setY(DoubleWritable y) {
		this.y = y;
	}

	public Text getCategory() {
		return category;
	}

	public void setCategory(Text category) {
		this.category = category;
	}

	public DoubleWritable getDistance() {
		return distance;
	}

	public void setDistance(DoubleWritable distance) {
		this.distance = distance;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		x.readFields(in);
		y.readFields(in);
		category.readFields(in);
		distance.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		x.write(out);
		y.write(out);
		category.write(out);
		distance.write(out);
	}

	@Override
	public int compareTo(PointWritable o) {
		return distance.compareTo(o.getDistance());
	}
	
	
	
	
}
