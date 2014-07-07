package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

public class BinaryTreeNode extends Node {
	
	private static final long serialVersionUID = 1L;
	
	private String name = null;
	
	private BinaryTreeNode left = null;
	
	private BinaryTreeNode right = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BinaryTreeNode getLeft() {
		return left;
	}

	public void setLeft(BinaryTreeNode left) {
		this.left = left;
	}

	public BinaryTreeNode getRight() {
		return right;
	}

	public void setRight(BinaryTreeNode right) {
		this.right = right;
	}

	@Override
	public Type getType() {
		return null;
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
	public void readNode(DataInput dataInput) throws IOException {
		
	}

	@Override
	protected void writeNode(DataOutput dataOutput) throws IOException {
		
	}

}
