package org.project.modules.classifier.regression.data;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
	
	private List<Instance> instances = null;
	
	public DataSet(List<Instance> instances) {
		super();
		this.instances = instances;
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
	
	public double[][] obtainDatas() {
		List<Instance> instanceList = getInstances();
		int len = instanceList.size();
		double[][] datas = new double[len][];
		Instance instance = null;
		for (int i = 0; i < len; i++) {
			instance = instanceList.get(i);
			double[] data = new double[2];
			data[0] = instance.getFeature1();
			data[1] = instance.getFeature2();
			datas[i] = data;
		}
		return datas;
	}
	
	public double[] obtainCategories() {
		List<Instance> instanceList = getInstances();
		int len = instanceList.size();
		double[] categories = new double[len];
		Instance instance = null;
		for (int i = 0; i < len; i++) {
			instance = instanceList.get(i);
			categories[i] = instance.getCategory();
		}
		return categories;
	}
	
	
}
