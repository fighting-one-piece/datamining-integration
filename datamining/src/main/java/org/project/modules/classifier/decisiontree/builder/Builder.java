package org.project.modules.classifier.decisiontree.builder;

import org.project.modules.classifier.decisiontree.data.Data;

public interface Builder {

	/**
	 * 构造决策树
	 * @param data
	 * @return
	 */
	public Object build(Data data);
}
