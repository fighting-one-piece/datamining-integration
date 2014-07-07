package org.project.modules.association.apriori.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.project.modules.association.apriori.data.Data;
import org.project.modules.association.apriori.data.DataLoader;
import org.project.modules.association.apriori.data.Instance;
import org.project.modules.association.apriori.data.ItemSet;
import org.project.modules.association.apriori.node.FPTreeNode;
import org.project.modules.association.apriori.node.FPTreeNodeHelper;
import org.project.utils.ShowUtils;

public class FPGrowthBuilder extends AbstractBuilder {

	public FPGrowthBuilder() {
		super();
	}
	
	@Override
	public Data loadData() {
		return DataLoader.load("d:\\apriori.txt");
	}
	
	//创建头表
	public List<FPTreeNode> buildHeadTables(Data data) {
		//统计各项出现频次
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Instance instance : data.getInstances()) {
			for (String value : instance.getValues()) {
				Integer mValue = map.get(value);
				map.put(value, null == mValue ? 1 : mValue + 1);
			}
		}
		//过滤掉未满足最小支持度的项
		List<Map.Entry<String, Integer>> entries = 
				new ArrayList<Map.Entry<String, Integer>>(); 
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() >= minSupport) {
				entries.add(entry);
			}
		}
		//根据出现频次排序项
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return ((Integer) o2.getValue()).compareTo((Integer) o1.getValue());
			}
		});
		//数据集的过滤重排
		for (Instance instance : data.getInstances()) {
			instance.replaceValues(entries);
			ShowUtils.print(instance.getValues());
		}
		//建立头表
		List<FPTreeNode> headerTables = new ArrayList<FPTreeNode>();
		for (Map.Entry<String, Integer> entry : entries) {
			headerTables.add(new FPTreeNode(entry.getKey(), entry.getValue()));
		}
		return headerTables;
	}
	
	//创建FPGrowthTree
	public FPTreeNode buildFPGrowthTree(Data data, List<FPTreeNode> headerTables) {
		FPTreeNode rootNode = new FPTreeNode();
		for (Instance instance : data.getInstances()) {
			LinkedList<String> items = instance.getValuesList();
			FPTreeNode tempNode = rootNode;
			//如果节点已经存在则加1
			FPTreeNode childNode = tempNode.findChild(items.peek());
			while (!items.isEmpty() && null != childNode) {
				childNode.incrementCount();
				tempNode = childNode;
				items.poll();
				childNode = tempNode.findChild(items.peek());
			}
			//如果节点不存在则新增
			addNewTreeNode(tempNode, items, headerTables);
		}
		return rootNode;
	}
	
	//新增树节点
	private void addNewTreeNode(FPTreeNode parent, LinkedList<String> items, 
			List<FPTreeNode> headerTables) {
		while (items.size() > 0) {
			String item = items.poll();
			FPTreeNode child = new FPTreeNode(item, 1);
			child.setParent(parent);
			parent.addChild(child);
			//建立节点之间的关联关系
			for (FPTreeNode headerTable : headerTables) {
				if (item.equals(headerTable.getName())) {
					while (null != headerTable.getNext()) {
						headerTable = headerTable.getNext();
					}
					headerTable.setNext(child);
					break;
				}
			}
			addNewTreeNode(child, items, headerTables);
		}
	}
	
	//构建频繁项集
	public void build(Data data, List<String> postfixs) {
		List<FPTreeNode> headerTables = buildHeadTables(data);
		FPTreeNode treeNode = buildFPGrowthTree(data, headerTables);
		FPTreeNodeHelper.print(treeNode, 0);
		if (treeNode.getChildren().size() == 0) {
			return;
		}
		//收集频繁项集
		List<ItemSet> frequency = new ArrayList<ItemSet>();
		frequencies.add(frequency);
		for (FPTreeNode header : headerTables) {
			ItemSet itemSet = new ItemSet(header.getName(), header.getCount());
			if(null != postfixs){
				for (String postfix : postfixs) {
					itemSet.add(postfix);
				}
			}
			frequency.add(itemSet);
		}
		//进入下一步迭代
		for (FPTreeNode headerTable : headerTables) {
			List<String> newPostfix = new LinkedList<String>();
			newPostfix.add(headerTable.getName());
			if (null != postfixs) {
				newPostfix.addAll(postfixs);
			}
			Data newData = new Data();
			FPTreeNode startNode = headerTable.getNext();
			while (null != startNode) {
				List<String> prefixNodes = new ArrayList<String>();
				FPTreeNode parent = startNode;
				while (null != (parent = parent.getParent()).getName()) {
					prefixNodes.add(parent.getName());
				}
				int count = startNode.getCount();
				while (count-- > 0 && prefixNodes.size() > 0) {
					Instance instance = new Instance();
					instance.setValues(prefixNodes.toArray(new String[0]));
					newData.getInstances().add(instance);
				}
				startNode = startNode.getNext();
			}
			build(newData, newPostfix);
		}
	}
	
	public void print(List<List<ItemSet>> itemSetss) {
		System.out.println("Frequency Item Set");
		System.out.println(itemSetss.size());
		for (List<ItemSet> itemSets : itemSetss) {
			for (ItemSet itemSet : itemSets) {
				System.out.print(itemSet.getSupport() + "\t");
				System.out.println(itemSet.getItems());
			}
		}
	}
	
	@Override
	protected List<ItemSet> getLastFrequency() {
		List<ItemSet> frequency = new ArrayList<ItemSet>();
		for (List<ItemSet> fre : frequencies) {
			for (ItemSet item : fre) {
				if (item.getItems().size() == 3) {
					frequency.add(item);
				}
			}
		}
		return frequency;
	}
	
	@Override
	public void buildFrequencyItemSet() {
		build(data, null);
		print(frequencies);
	}
	
	public static void main(String[] args) {
		FPGrowthBuilder fpg = new FPGrowthBuilder();
		fpg.buildFrequencyItemSet();
		fpg.buildAssociationRules();
	}

}