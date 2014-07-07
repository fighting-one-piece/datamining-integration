package org.project.modules.classifier.decisiontree.mr.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class AttributeKVWritable implements WritableComparable<AttributeKVWritable>, Cloneable {
	
	private String attributeName = null;
	
	private String attributeValue = null;
	
	public AttributeKVWritable() {
		
	}

	public AttributeKVWritable(String attributeName, String attributeValue) {
		super();
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		this.attributeName = new String(buff);
		length = dataInput.readInt();
		buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		this.attributeValue = new String(buff);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(attributeName.length());
		dataOutput.writeBytes(attributeName);
		dataOutput.writeInt(attributeValue.length());
		dataOutput.writeBytes(attributeValue);
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	@Override
	public int compareTo(AttributeKVWritable o) {
		return o.getAttributeName().compareTo(attributeName) == 0 
				? o.getAttributeValue().compareTo(attributeValue)
					: o.getAttributeName().compareTo(attributeName);
	}

	@Override
	public int hashCode() {
		return attributeName.hashCode();
	}

}
