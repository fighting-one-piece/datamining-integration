package org.project.modules.classifier.decisiontree.data;

import java.util.ArrayList;
import java.util.List;

public class DataSplit {
	
	/** 数据分割条目集合*/
	private List<DataSplitItem> items = null;
	
	public void addItem(DataSplitItem item) {
		getItems().add(item);
	}
	
	public DataSplitItem getItemBySplitPoint(String splitPoint) {
		for (DataSplitItem item : getItems()) {
			if (splitPoint.equalsIgnoreCase(item.getSplitPoint())) {
				return item;
			}
		}
		return null;
	}

	public List<DataSplitItem> getItems() {
		if (null == items) {
			items = new ArrayList<DataSplitItem>();
		}
		return items;
	}

	public void setItems(List<DataSplitItem> items) {
		this.items = items;
	}

	
}
