package org.project.modules.association.apriori.node;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.project.modules.association.apriori.data.ItemSet;

public class AssociationRule {

	private ItemSet left = null;
	
	private ItemSet right = null;
	
	private double confidence = 0.0;
	
	private Set<AssociationRule> children = null;
	
	public AssociationRule() {
		
	}
	
	public AssociationRule(ItemSet left, ItemSet right) {
		super();
		this.left = left;
		this.right = right;
	}

	public ItemSet getLeft() {
		if (null == left) {
			left = new ItemSet();
		}
		return left;
	}

	public void setLeft(ItemSet left) {
		this.left = left;
	}

	public ItemSet getRight() {
		if (null == right) {
			right = new ItemSet();
		}
		return right;
	}

	public void setRight(ItemSet right) {
		this.right = right;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public Set<AssociationRule> getChildren() {
		if (null == children) {
			children = new HashSet<AssociationRule>();
		}
		return children;
	}

	public void setChildren(Set<AssociationRule> children) {
		this.children = children;
	}
	
	public boolean isEqual(AssociationRule o) {
		boolean isEqual = false;
		TreeSet<String> iL = getLeft().getItems();
		TreeSet<String> oL = o.getLeft().getItems();
		TreeSet<String> iR = getRight().getItems();
		TreeSet<String> oR = o.getRight().getItems();
		if (iL.size() == oL.size() && iL.containsAll(oL) &&
				iR.size() == oR.size() && iR.containsAll(oR)) {
			isEqual = true;
		}
		return isEqual;
	}

	
}
