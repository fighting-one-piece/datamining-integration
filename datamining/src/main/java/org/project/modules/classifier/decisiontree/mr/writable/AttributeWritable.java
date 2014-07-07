package org.project.modules.classifier.decisiontree.mr.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class AttributeWritable implements Writable, Cloneable {
	
	private Long id = null;
	
	private String category = null;
	
	private String attributeValue = null;
	
	public AttributeWritable() {
		
	}
	
	public AttributeWritable(Long id, String category, String attributeValue) {
		super();
		this.id = id;
		this.category = category;
		this.attributeValue = attributeValue;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		this.id = dataInput.readLong();
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		this.category = new String(buff);
		length = dataInput.readInt();
		buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		this.attributeValue = new String(buff);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeLong(id);
		dataOutput.writeInt(category.length());
		dataOutput.writeBytes(category);
		dataOutput.writeInt(attributeValue.length());
		dataOutput.writeBytes(attributeValue);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}


}
