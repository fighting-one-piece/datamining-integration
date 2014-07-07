package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

public class BranchNode extends Node {
	
	private static final long serialVersionUID = 1L;
	
	private String name = null;
	
	private String[] values = null;
	
	private Node[] children = null;
	
	public BranchNode() {
		
	}
	
	public BranchNode(String name) {
		this.name = name;
	}
	
	public BranchNode(String name, String[] values, Node[] children) {
		super();
		this.name = name;
		this.values = values;
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public Node[] getChildren() {
		return children;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}
	
	@Override
	public Type getType() {
		return Type.BRANCH;
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
		length = dataInput.readInt();
		String[] t_values = new String[length];
		for (int i = 0; i < length; i++) {
			int len = dataInput.readInt();
			buff = new byte[len];
			dataInput.readFully(buff, 0, len);
			t_values[i] = new String(buff);
		}
		values = t_values;
		length = dataInput.readInt();
		Node[] t_children = new Node[length];
		for (int i = 0; i < length; i++) {
			t_children[i] = Node.read(dataInput);
		}
		children = t_children;
	}
	
	@Override
	protected void writeNode(DataOutput dataOutput) throws IOException {
		int length = name.length();
		dataOutput.writeInt(length);
		dataOutput.write(name.getBytes(), 0, length);
		dataOutput.writeInt(values.length);
	    for (String value : values) {
	    	int len = value.length();
			dataOutput.writeInt(len);
			dataOutput.write(value.getBytes(), 0, len);
	    }
	    dataOutput.writeInt(children.length);
	    for (Node node : children) {
	    	node.write(dataOutput);
	    }
	}
}
