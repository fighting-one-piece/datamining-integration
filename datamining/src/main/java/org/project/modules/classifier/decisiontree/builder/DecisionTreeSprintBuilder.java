package org.project.modules.classifier.decisiontree.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.project.modules.classifier.decisiontree.data.AttributeDetail;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.node.TreeNode;

public class DecisionTreeSprintBuilder extends BuilderAbstractImpl {
	
	@Override
	public Object build(Data data) {
		//对数据集预先判断，特征属性为空时候选取最多数量的类型,数据集全部为统一类型时候直接返回类型
		Object preHandleResult = preHandle(data);
		if (null != preHandleResult) return preHandleResult;
		//创建属性表
		Map<String, List<AttributeDetail>> attributeTableMap = 
				new HashMap<String, List<AttributeDetail>>();
		for (Instance instance : data.getInstances()) {
			String category = String.valueOf(instance.getCategory());
			Map<String, Object> attrs = instance.getAttributes();
			for (Map.Entry<String, Object> entry : attrs.entrySet()) {
				String attrName = entry.getKey();
				List<AttributeDetail> attributeTable = attributeTableMap.get(attrName);
				if (null == attributeTable) {
					attributeTable = new ArrayList<AttributeDetail>();
					attributeTableMap.put(attrName, attributeTable);
				}
				attributeTable.add(new AttributeDetail(instance.getId(), 
						attrName, String.valueOf(entry.getValue()), category));
			}
		}
		//计算属性表的基尼指数
		Set<String> attributes = data.getAttributeSet();
		String splitAttribute = null;
		String minSplitPoint = null;
		double minSplitPointGini = 1.0;
		for (Map.Entry<String, List<AttributeDetail>> entry : attributeTableMap.entrySet()) {
			String attribute = entry.getKey();
			if (!attributes.contains(attribute)) {
				continue;
			}
			List<AttributeDetail> attributeTable = entry.getValue();
			Object[] result = calculateMinGini(attributeTable);
			double splitPointGini = Double.parseDouble(String.valueOf(result[1]));
			if (minSplitPointGini > splitPointGini) {
				minSplitPointGini = splitPointGini;
				minSplitPoint = String.valueOf(result[0]);
				splitAttribute = attribute;
			}
		}
		System.out.println("splitAttribute: " + splitAttribute);
		TreeNode treeNode = new TreeNode(splitAttribute);
		
		//根据分割属性和分割点分割数据集
		attributes.remove(splitAttribute);
		Set<String> attributeValues = new HashSet<String>();
		List<List<Instance>> splitInstancess = new ArrayList<List<Instance>>();
		List<Instance> splitInstances1 = new ArrayList<Instance>();
		List<Instance> splitInstances2 = new ArrayList<Instance>();
		splitInstancess.add(splitInstances1);
		splitInstancess.add(splitInstances2);
		for (Instance instance : data.getInstances()) {
			Object value = instance.getAttribute(splitAttribute);
			attributeValues.add(String.valueOf(value));
			if (value.equals(minSplitPoint)) {
				splitInstances1.add(instance);
			} else {
				splitInstances2.add(instance);
			}
		}
		attributeValues.remove(minSplitPoint);
		StringBuilder sb = new StringBuilder();
		for (String attributeValue : attributeValues) {
			sb.append(attributeValue).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		String[] names = new String[]{minSplitPoint, sb.toString()};
		for (int i = 0; i < 2; i++) {
			List<Instance> splitInstances = splitInstancess.get(i);
			if (splitInstances.size() == 0) continue;
			Data subData = new Data(attributes.toArray(new String[0]),
					splitInstances);
			treeNode.setChild(names[i], build(subData));
		}
		return treeNode;
	}

	/** 计算基尼指数*/
	public Object[] calculateMinGini(List<AttributeDetail> attributeTable) {
		double totalNum = 0.0;
		Map<String, Map<String, Integer>> attrValueSplits = 
				new HashMap<String, Map<String, Integer>>();
		Set<String> splitPoints = new HashSet<String>();
		Iterator<AttributeDetail> iterator = attributeTable.iterator();
		while (iterator.hasNext()) {
			AttributeDetail attribute = iterator.next();
			String attributeValue = attribute.getValue();
			splitPoints.add(attributeValue);
			Map<String, Integer> attrValueSplit = attrValueSplits.get(attributeValue);
			if (null == attrValueSplit) {
				attrValueSplit = new HashMap<String, Integer>();
				attrValueSplits.put(attributeValue, attrValueSplit);
			}
			String category = attribute.getCategory();
			Integer categoryNum = attrValueSplit.get(category);
			attrValueSplit.put(category, null == categoryNum ? 1 : categoryNum + 1);
			totalNum++;
		}
		String minSplitPoint = null;
		double minSplitPointGini = 1.0;
		for (String splitPoint : splitPoints) {
			double splitPointGini = 0.0;
			double splitAboveNum = 0.0;
			double splitBelowNum = 0.0;
			Map<String, Integer> attrBelowSplit = new HashMap<String, Integer>();
			for (Map.Entry<String, Map<String, Integer>> entry : 
				attrValueSplits.entrySet()) {
				String attrValue = entry.getKey();
				Map<String, Integer> attrValueSplit = entry.getValue();
				if (splitPoint.equals(attrValue)) {
					for (Integer v : attrValueSplit.values()) {
						splitAboveNum += v;
					}
					double aboveGini = 1.0;
					for (Integer v : attrValueSplit.values()) {
						aboveGini -= Math.pow((v / splitAboveNum), 2);
					}
					splitPointGini += (splitAboveNum / totalNum) * aboveGini;
				} else {
					for (Map.Entry<String, Integer> e : attrValueSplit.entrySet()) {
						String k = e.getKey();
						Integer v = e.getValue();
						Integer count = attrBelowSplit.get(k);
						attrBelowSplit.put(k, null == count ? v : v + count);
						splitBelowNum += e.getValue();
					}
				}
			}
			double belowGini = 1.0;
			for (Integer v : attrBelowSplit.values()) {
				belowGini -= Math.pow((v / splitBelowNum), 2);
			}
			splitPointGini += (splitBelowNum / totalNum) * belowGini;
			if (minSplitPointGini > splitPointGini) {
				minSplitPointGini = splitPointGini;
				minSplitPoint = splitPoint;
			}
		}
		return new Object[]{minSplitPoint, minSplitPointGini};
	}
	
}
