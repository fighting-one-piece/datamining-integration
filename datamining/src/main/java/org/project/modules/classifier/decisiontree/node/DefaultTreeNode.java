package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

public class DefaultTreeNode extends Node {
	
	private static final long serialVersionUID = 1L;
	
	private String name = null;
	
	private Set<Node> children = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Node> getChildren() {
		if (null == children) {
			children = new HashSet<Node>();
		}
		return children;
	}

	public void setChildren(Set<Node> children) {
		this.children = children;
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
