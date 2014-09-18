package org.project.modules.classifier.adaboost.data;

import java.util.ArrayList;
import java.util.List;

import org.project.utils.FileUtils;

public class DataSet {
	
	private List<double[]> datas = null;
	
	private List<Double> categories = null;
	
	public List<double[]> getDatas() {
		if (null == datas) {
			datas = new ArrayList<double[]>();
		}
		return datas;
	}

	public void setDatas(List<double[]> datas) {
		this.datas = datas;
	}

	public List<Double> getCategories() {
		if (null == categories) {
			categories = new ArrayList<Double>();
		}
		return categories;
	}

	public void setCategories(List<Double> categories) {
		this.categories = categories;
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 200; i++) {
			double x1 = 1 + Math.random() * 2; 
			double y1 = 1 + Math.random() * 2; 
			if ((x1 - 2) * (x1 - 2) + (y1 - 2) * (y1 - 2) - 1 <= 0) {
				sb.append(x1).append("\t");
				sb.append(y1).append("\t");
				sb.append(1d).append("\n");
			}
			double x2 = Math.random() * 4;
			double y2 = Math.random() * 4;
			if ((x2 - 2) * (x2 - 2) + (y2 - 2) * (y2 - 2) - 4 < 0
					&& (x2 - 2) * (x2 - 2) + (y2 - 2) * (y2 - 2) - 1 > 0) { 
				//规范化处理
				sb.append(-x2).append("\t");
				sb.append(-y2).append("\t");
				sb.append(-1d).append("\n");
			}
			
		}
		System.out.println(FileUtils.writeToTmpFile(sb.toString()));
		sb = new StringBuilder();
		for (int j = 0; j < 400; j++) {
			double x1 = 1 + Math.random() * 2; 
			double y1 = 1 + Math.random() * 2; 
			if ((x1 - 2) * (x1 - 2) + (y1 - 2) * (y1 - 2) - 1 <= 0) { 
				sb.append(x1).append("\t");
				sb.append(y1).append("\t");
				sb.append(1d).append("\n");
			}
			double x2 = Math.random() * 4;
			double y2 = Math.random() * 4;
			if ((x2 - 2) * (x2 - 2) + (y2 - 2) * (y2 - 2) - 4 < 0
					&& (x2 - 2) * (x2 - 2) + (y2 - 2) * (y2 - 2) - 1 > 0) { 
				sb.append(-x2).append("\t");
				sb.append(-y2).append("\t");
				sb.append(-1d).append("\n");
			}
		}
		System.out.println(FileUtils.writeToTmpFile(sb.toString()));
	}
	
}
