package org.project.modules.classifier.decisiontree.data;

import java.util.ArrayList;
import java.util.List;

public class DataSplitItem {

	/** 当前分割对应的文件路径*/
	private String path = null;
	/** 当前分割对应的属性值*/
	private String splitPoint = null;
	/** 当前分割对应的数据集*/
	private List<Instance> instances = null;
	
	public DataSplitItem() {
		
	}
	
	public DataSplitItem(String path, String splitPoint,
			List<Instance> instances) {
		super();
		this.path = path;
		this.splitPoint = splitPoint;
		this.instances = instances;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSplitPoint() {
		return splitPoint;
	}

	public void setSplitPoint(String splitPoint) {
		this.splitPoint = splitPoint;
	}

	public List<Instance> getInstances() {
		if (null == instances) {
			instances = new ArrayList<Instance>();
		}
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
	
}
