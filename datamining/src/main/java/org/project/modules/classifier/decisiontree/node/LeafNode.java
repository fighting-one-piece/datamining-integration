package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

public class LeafNode extends Node {
	
	private static final long serialVersionUID = 1L;
	
	private String name = null;
	
	public LeafNode() {
		
	}
	
	public LeafNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Type getType() {
		return Type.LEAF;
	}

	@Override
	public Object classify(Data data) {
		return null;
	}

	@Override
	public Object classify(Instance... instances) {
		return null;
	}
	
	@Override
	protected void readNode(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		name = new String(buff);
	}
	
	@Override
	protected void writeNode(DataOutput dataOutput) throws IOException {
		int length = name.length();
		dataOutput.writeInt(length);
		dataOutput.write(name.getBytes(), 0, length);
	}

}
