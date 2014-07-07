package org.project.modules.classifier.decisiontree.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 最佳属性*/
public class BestAttribute {

	/** 对应最佳属性的索引*/
	private int index = -1;
	/** 对应最佳属性的值*/
	private Object value = null;
	/** 分支属性分裂信息映射*/
	private Map<Object, Map<Object, List<Instance>>> splits = null;
	
	public BestAttribute() {
		
	}
	
	public BestAttribute(int index, Object value,
			Map<Object, Map<Object, List<Instance>>> splits) {
		this.index = index;
		this.value = value;
		this.splits = splits;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Map<Object, Map<Object, List<Instance>>> getSplits() {
		if (null == splits) {
			splits = new HashMap<Object, Map<Object, List<Instance>>>();
		}
		return splits;
	}
	
	public void setSplits(Map<Object, Map<Object, List<Instance>>> splits) {
		this.splits = splits;
	}
	
}
