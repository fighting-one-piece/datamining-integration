package org.project.modules.hbase;

import org.apache.solr.client.solrj.beans.Field;

public class SolrEntity {

	private String id = null;
	private String title = null;

	public SolrEntity() {
		super();
	}

	public SolrEntity(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public String getId() {
		return id;
	}
	@Field
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	@Field("subject")
	public void setTitle(String title) {
		this.title = title;
	}


}
