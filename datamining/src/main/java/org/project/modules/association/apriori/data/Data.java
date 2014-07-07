package org.project.modules.association.apriori.data;

import java.util.ArrayList;
import java.util.List;

public class Data {

	private List<Instance> instances = null;
	
	public Data() {
	}
	
	public Data(List<Instance> instances) {
		this.instances = instances;
	}

	public List<Instance> getInstances() {
		if (null == instances) {
			instances = new ArrayList<Instance>();
		}
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
	
	
}
