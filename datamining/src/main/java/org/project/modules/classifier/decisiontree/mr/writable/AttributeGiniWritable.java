package org.project.modules.classifier.decisiontree.mr.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class AttributeGiniWritable implements Writable, Cloneable {
	
	private String attribute = null;
	
	private double gini = 0.0;
	
	private boolean isCategory = false;
	
	private String splitPoint = null;
	
	public AttributeGiniWritable() {
		
	}
	
	public AttributeGiniWritable(String attribute, 
			double gini, String splitPoint) {
		this.attribute = attribute;
		this.gini = gini;
		this.splitPoint = splitPoint;
	}
	
	public AttributeGiniWritable(String attribute, 
			double gini, boolean isCategory, String splitPoint) {
		this.attribute = attribute;
		this.gini = gini;
		this.isCategory = isCategory;
		this.splitPoint = splitPoint;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		this.attribute = new String(buff);
		this.gini = dataInput.readDouble();
		this.isCategory = dataInput.readBoolean();
		if (dataInput.readBoolean()) {
			length = dataInput.readInt();
			buff = new byte[length];
			dataInput.readFully(buff, 0, length);
			this.splitPoint = new String(buff);
		}
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(attribute.length());
		dataOutput.writeBytes(attribute);
		dataOutput.writeDouble(gini);
		dataOutput.writeBoolean(isCategory);
		dataOutput.writeBoolean(null != splitPoint);
		if (null != splitPoint) {
			dataOutput.writeInt(splitPoint.length());
			dataOutput.writeBytes(splitPoint);
		}
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getSplitPoint() {
		return splitPoint;
	}

	public void setSplitPoint(String splitPoint) {
		this.splitPoint = splitPoint;
	}

	public double getGini() {
		return gini;
	}

	public void setGainRatio(double gini) {
		this.gini = gini;
	}
	
	public boolean isCategory() {
		return isCategory;
	}

	public void setCategory(boolean isCategory) {
		this.isCategory = isCategory;
	}


}
