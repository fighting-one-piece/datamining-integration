package org.project.modules.association.apriori.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.project.modules.association.apriori.data.Data;
import org.project.modules.association.apriori.data.DataLoader;
import org.project.modules.association.apriori.data.Instance;
import org.project.modules.association.apriori.data.ItemSet;
import org.project.utils.ShowUtils;

public class AprioriBuilder extends AbstractBuilder {
	
	/** 候选集集合*/
	private List<List<ItemSet>> candidates = null;
	
	public AprioriBuilder() {
		super();
		candidates = new ArrayList<List<ItemSet>>();
	}
	
	@Override
	public Data loadData() {
		return DataLoader.load("d:\\apriori.txt");
	}
	
	/** 生成频繁一项集*/
	private void frequency_1_itemset_gen() {
		List<ItemSet> frequency = new ArrayList<ItemSet>();
		List<ItemSet> candidate = new ArrayList<ItemSet>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Instance instance : data.getInstances()) {
			for (String value : instance.getValues()) {
				Integer mValue = map.get(value);
				map.put(value, null == mValue ? 1 : mValue + 1);
			}
		}
		ShowUtils.print(map);
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			candidate.add(new ItemSet(entry.getKey(), entry.getValue()));
			if (entry.getValue() >= minSupport) {
				frequency.add(new ItemSet(entry.getKey(), entry.getValue()));
			}
		}
		candidates.add(candidate);
		frequencies.add(frequency);
	}
	
	/** 生成频繁K项集*/
	private void frequency_k_itemset_gen(int k) {
		Iterator<ItemSet> f1Iter = frequencies.get(k - 2).iterator();
		Iterator<ItemSet> f2Iter = frequencies.get(0).iterator();
		List<ItemSet> candidate = new ArrayList<ItemSet>();
		while (f1Iter.hasNext()) {
			ItemSet item1 = f1Iter.next();
			while (f2Iter.hasNext()) {
				ItemSet item2 = f2Iter.next();
				ItemSet temp = new ItemSet();
				temp.getItems().addAll(item1.getItems());
				if (!temp.getItems().containsAll(item2.getItems())) {
					temp.getItems().addAll(item2.getItems());
					boolean isContain = false;
					for (ItemSet itemSet : candidate) {
						if (itemSet.getItems().containsAll(temp.getItems())) {
							isContain = true;
						}
					}
					if (!isContain) {
						candidate.add(temp);
					}
				}
			}
			f2Iter = frequencies.get(0).iterator();
		}
		candidates.add(candidate);
		List<ItemSet> frequency = new ArrayList<ItemSet>();
		for (ItemSet itemSet : candidate) {
			int support = calculateSupport(data, itemSet.getItemsArray());
			if (support >= minSupport) {
				itemSet.setSupport(support);
				frequency.add(itemSet);
			}
		}
		frequencies.add(frequency);
	}
	
	public void buildFrequencyItemSet() {
		frequency_1_itemset_gen();
		print(candidates, true);
		print(frequencies, false);
		for (int k = 2; frequencies.get(k - 2).size() > 0; k++) {
			frequency_k_itemset_gen(k);
			print(candidates, true);
			print(frequencies, false);
		}
	}
	
	public void print(List<List<ItemSet>> itemSetss, boolean isCandidate) {
		System.out.println((isCandidate ?  "Candidate" : "Frequency") + " Item Set");
		System.out.println(itemSetss.size());
		for (List<ItemSet> itemSets : itemSetss) {
			print(itemSets);
		}
	}
	
	public void print(List<ItemSet> itemSets) {
		System.out.println("----------");
		for (ItemSet itemSet : itemSets) {
			System.out.print(itemSet.getSupport() + "\t");
			System.out.println(itemSet.getItems());
		}
		System.out.println("----------");
	}
	
	public static void main(String[] args) {
		AprioriBuilder ab = new AprioriBuilder();
		ab.buildFrequencyItemSet();
		ab.buildAssociationRules();
	}
}
