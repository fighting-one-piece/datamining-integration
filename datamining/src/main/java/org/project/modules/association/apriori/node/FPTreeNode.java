package org.project.modules.association.apriori.node;

import java.util.HashSet;
import java.util.Set;

public class FPTreeNode {

	private String name = null;
	
	private int count = 0;
	
	private FPTreeNode next = null;
	
	private FPTreeNode parent = null;
	
	private Set<FPTreeNode> children = null;
	
	public FPTreeNode() {
		
	}
	
	public FPTreeNode(String name, int count) {
		super();
		this.name = name;
		this.count = count;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incrementCount() {
		this.count += 1;
	}
	
	public FPTreeNode getNext() {
		return next;
	}

	public void setNext(FPTreeNode next) {
		this.next = next;
	}

	public FPTreeNode getParent() {
		return parent;
	}

	public void setParent(FPTreeNode parent) {
		this.parent = parent;
	}

	public Set<FPTreeNode> getChildren() {
		if (null == children) {
			children = new HashSet<FPTreeNode>();
		}
		return children;
	}

	public void setChildren(Set<FPTreeNode> children) {
		this.children = children;
	}
	
	public void addChild(FPTreeNode child) {
		getChildren().add(child);
	}
	
	public FPTreeNode findChild(String name) {
		for (FPTreeNode child : getChildren()) {
			if (child.getName().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
}
