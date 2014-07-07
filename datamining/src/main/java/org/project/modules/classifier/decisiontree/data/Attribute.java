package org.project.modules.classifier.decisiontree.data;

public class Attribute {
	
	private String name = null;
	
	private String value = null;

	public Attribute() {
		
	}
	
	public Attribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
