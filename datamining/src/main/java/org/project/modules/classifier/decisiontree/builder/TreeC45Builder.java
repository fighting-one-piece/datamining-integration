package org.project.modules.classifier.decisiontree.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.project.modules.classifier.decisiontree.data.BestAttribute;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

/** C45算法实现的决策树*/
public class TreeC45Builder extends TreeBuilder {

	@Override
	public BestAttribute chooseBestAttribute(Data data) {
		Map<Object, List<Instance>> splits = data.getSplits();
		String[] attributes = data.getAttributes();
		int optIndex = -1; // 最优属性下标
		double maxGainRatio = 0.0; // 最大增益率
		Map<Object, Map<Object, List<Instance>>> optSplits = null; // 最优分支方案
		// 对每一个属性，计算将其作为测试属性的情况下在各分支确定新样本的分类需要的信息量之和，选取最优
		for (int attrIndex = 0; attrIndex < attributes.length; attrIndex++) {
			int allCount = 0; // 统计样本总数的计数器
			// 按当前属性构建Map：属性值->(分类->样本列表)
			Map<Object, Map<Object, List<Instance>>> curSplits = 
					new HashMap<Object, Map<Object, List<Instance>>>();
			for (Entry<Object, List<Instance>> entry : splits.entrySet()) {
				Object category = entry.getKey();
				List<Instance> instances = entry.getValue();
				for (Instance instance : instances) {
					Object attrValue = instance.getAttribute(attributes[attrIndex]);
					Map<Object, List<Instance>> split = curSplits.get(attrValue);
					if (split == null) {
						split = new HashMap<Object, List<Instance>>();
						curSplits.put(attrValue, split);
					}
					List<Instance> splitInstances = split.get(category);
					if (splitInstances == null) {
						splitInstances = new LinkedList<Instance>();
						split.put(category, splitInstances);
					}
					splitInstances.add(instance);
				}
				allCount += instances.size();
			}
			// 计算将当前属性作为测试属性的情况下在各分支确定新样本的分类需要的信息量之和
			double curValue = 0.0; // 计数器：累加各分支
			double splitInfo = 0.0; //分裂信息
			for (Map<Object, List<Instance>> curSplit : curSplits.values()) {
				double perSplitCount = 0;
				for (List<Instance> list : curSplit.values())
					perSplitCount += list.size();
				// 累计当前分支样本数
				double perSplitValue = 0.0; // 计数器：当前分支
				for (List<Instance> list : curSplit.values()) {
					double p = list.size() / perSplitCount;
					perSplitValue -= p * (Math.log(p) / Math.log(2));
				}
				double dj = (perSplitCount / allCount);
				curValue += dj * perSplitValue;
				splitInfo -= dj * (Math.log(dj) / Math.log(2));
			}
			double gainRatio = curValue / splitInfo;
			if (maxGainRatio <= gainRatio) {
				optIndex = attrIndex;
				maxGainRatio = gainRatio;
				optSplits = curSplits;
			}
		}
		return new BestAttribute(optIndex, maxGainRatio, optSplits);
	}

}
