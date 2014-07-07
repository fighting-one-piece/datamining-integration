package org.project.modules.association.apriori.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Instance {
	
	private Long id = null;

	private String[] values = null;
	
	public Instance() {
	}
	
	public Instance(String[] values) {
		this.values = values;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String[] getValues() {
		return values;
	}
	
	public LinkedList<String> getValuesList() {
		LinkedList<String> list = new LinkedList<String>();
		for (String value : values) {
			list.add(value);
		}
		return list;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
	
	public void replaceValues(List<Map.Entry<String, Integer>> entries) {
		List<String> results = new ArrayList<String>();
		for (Map.Entry<String, Integer> entry : entries) {
			String key = entry.getKey();
			for (String value : values) {
				if (key.equals(value)) {
					results.add(key);
					break;
				}
			}
		}
		this.values = results.toArray(new String[0]);
	}
}
