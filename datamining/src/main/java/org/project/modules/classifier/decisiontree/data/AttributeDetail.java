package org.project.modules.classifier.decisiontree.data;

public class AttributeDetail {
	
	private Long id = null;
	
	private String name = null;
	
	private String value = null;

	private String category = null;
	
	public AttributeDetail() {
		
	}
	
	public AttributeDetail(Long id, String name, String value, String category) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}
