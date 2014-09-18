package org.project.modules.classifier.regression.data;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
	
	private double[][] datas = null;
	
	private List<Integer> categories = null;
	
	public double[][] getDatas() {
		return datas;
	}

	public void setDatas(double[][] datas) {
		this.datas = datas;
	}

	public List<Integer> getCategories() {
		if (null == categories) {
			categories = new ArrayList<Integer>();
		}
		return categories;
	}

	public void setCategories(List<Integer> categories) {
		this.categories = categories;
	}

	
}
