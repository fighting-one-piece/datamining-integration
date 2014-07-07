package org.project.modules.association.apriori.builder;

import java.util.List;

import org.project.modules.association.apriori.data.ItemSet;

public interface Builder {
	
	//构建频繁项集
	public void buildFrequencyItemSet();
	
	//构建关联规则
	public void buildAssociationRules();
	
	//获取频繁项集
	public List<List<ItemSet>> obtainFrequencyItemSet();
}
