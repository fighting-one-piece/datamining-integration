package org.project.modules.association.apriori.data;

import java.util.Iterator;
import java.util.TreeSet;

public class ItemSet {

	private TreeSet<String> items = null;
	
	private int support = 0;
	
	public ItemSet() {
	}
	
	public ItemSet(TreeSet<String> items) {
		this.items = items;
	}
	
	public ItemSet(String item, int support) {
		getItems().add(item);
		this.support = support;
	}
	
	public ItemSet(TreeSet<String> item, int support) {
		this.items = item;
		this.support = support;
	}

	public TreeSet<String> getItems() {
		if (null == items) {
			items = new TreeSet<String>();
		}
		return items;
	}
	
	public String[] getItemsArray() {
		return items.toArray(new String[0]);
	}

	public void setItems(TreeSet<String> item) {
		this.items = item;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}
	
	public boolean isMerge(ItemSet other) {
		if (null == other || other.getItems().size() != getItems().size()) {
			return false;
		}
		Iterator<String> iIter = getItems().iterator();
		Iterator<String> oIter = other.getItems().iterator();
		int size = getItems().size();
		while (iIter.hasNext() && oIter.hasNext() && --size > 0) {
			if (!iIter.next().equals(oIter.next())) {
				return false;
			}
		}
		return !(getItems().last().equals(other.getItems().last()));
	}
	
	public boolean isEqual(ItemSet other) {
		if (null == other || other.getItems().size() != getItems().size()) {
			return false;
		}
		Iterator<String> iIter = getItems().iterator();
		Iterator<String> oIter = other.getItems().iterator();
		int count = 0;
		while (iIter.hasNext()) {
			String iValue = iIter.next();
			while (oIter.hasNext()) {
				String oValue = oIter.next();
				if (oValue.equals(iValue)) {
					count++;
				}
			}
			oIter = other.getItems().iterator();
		}
		if (count == other.getItems().size()) {
			return true;
		}
		return false;
	}
	
	public void add(String value) {
		getItems().add(value);
	}
	
	public void addAll(TreeSet<String> values) {
		getItems().addAll(values);
	}
	
}
