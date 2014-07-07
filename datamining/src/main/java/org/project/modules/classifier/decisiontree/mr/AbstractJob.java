package org.project.modules.classifier.decisiontree.mr;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

public abstract class AbstractJob {

	protected Configuration conf = null;
	
	protected AbstractJob() {
		
	}
	
	public abstract void run(String[] args);
	
	protected Configuration getConfiguration() {
		if (null == conf) {
			conf = new Configuration();
		}
		return conf;
	}
	
	/**
	 * 获取数量最多的类型
	 * @param splits
	 * @return
	 */
	protected Object obtainMaxCategory(Data data) {
		return obtainMaxCategory(data.getSplits());
	}
	
	/**
	 * 获取数量最多的类型
	 * @param splits
	 * @return
	 */
	protected Object obtainMaxCategory(Map<Object, List<Instance>> splits) {
		int max = 0;
		Object maxCategory = null;
		for (Entry<Object, List<Instance>> entry : splits.entrySet()) {
			int cur = entry.getValue().size();
			if (cur > max) {
				max = cur;
				maxCategory = entry.getKey();
			}
		}
		return maxCategory;
	}
	
	/** 对数据集预先判断处理*/
	protected Object preHandle(Data data) {
		String[] attributes = data.getAttributes();
		if (null == attributes || attributes.length == 0) {
			return obtainMaxCategory(data);
		}
		List<Instance> instances = data.getInstances();
		if (instances.size() == 1) {
			return instances.get(0).getCategory();
		} else if (instances.size() > 1) {
			boolean isEqual = true;
			Object category = instances.get(0).getCategory();
			for (Instance instance : instances) {
				if (!category.equals(instance.getCategory())) {
					isEqual = false;
				}
			}
			if (isEqual) return category;
		}
		return null;
	}
	
}
