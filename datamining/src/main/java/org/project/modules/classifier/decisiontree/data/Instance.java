package org.project.modules.classifier.decisiontree.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 
 ** 样本实例，包含多个属性和一个指明样本所属分类的分类值 
 **/
public class Instance implements Cloneable {
	
	/** 实例行号*/
	private Long id = null;
	/** 实例类型*/
	private Object category = null;
	/** 实例属性键值对*/
	private Map<String, Object> attributes = null;
	/** 实例属性集合*/
	private Set<Attribute> attributeset = null;
	/** 实例权重*/
	private double weight = 0.0d;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Object getCategory() {
		return category;
	}
	
	public void setCategory(Object category) {
		this.category = category;
	}
	
	public Object getAttribute(String name) {
		return getAttributes().get(name);
	}

	public void setAttribute(String name, Object value) {
		getAttributes().put(name, value);
	}
	
	public void removeAttribute(String name) {
		getAttributes().remove(name);
	}
	
	public Map<String, Object> getAttributes() {
		if (null == attributes) {
			attributes = new HashMap<String, Object>();
		}
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public Set<Attribute> getAttributeset() {
		if (null == attributeset) {
			attributeset = new HashSet<Attribute>();
		}
		return attributeset;
	}

	public void setAttributeset(Set<Attribute> attributeset) {
		this.attributeset = attributeset;
	}

	public String toString() {
		return attributes.toString();
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Override
	protected Instance clone() throws CloneNotSupportedException {
		Instance newInstance = new Instance();
		newInstance.setId(getId());
		newInstance.setCategory(getCategory());
		Map<String, Object> newAttributes = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : getAttributes().entrySet()) {
			newAttributes.put(entry.getKey(), entry.getValue());
		}
		newInstance.setAttributes(newAttributes);
		return newInstance;
	}
	
	public void print() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append("\t");
		sb.append(category).append("\t");
		for (Map.Entry<String, Object> entry : getAttributes().entrySet()) {
			sb.append(entry.getKey()).append(":");
			sb.append(entry.getValue()).append("\t");
		}
		System.out.println(sb.toString());
	}
	
}
