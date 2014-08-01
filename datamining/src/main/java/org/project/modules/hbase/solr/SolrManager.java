package org.project.modules.hbase.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class SolrManager {

	private static final String solrUrl = "http://centos.host1:8080/solr-4.5.1";
	private static HttpSolrServer solrServer = null;
	
	static {
		initServer();
	}
	
	private static void initServer() {
		solrServer = new HttpSolrServer(solrUrl);
		solrServer.setAllowCompression(true);
		solrServer.setConnectionTimeout(100 * 1000);
	}
	
	private SolrManager(){}
	
	private static class SolrManagerHolder {
		private static SolrManager instance = new SolrManager();
	}
	
	public static SolrManager getInstance() {
		return SolrManagerHolder.instance;
	}
	
	public static HttpSolrServer obtainServer() {
		return solrServer;
	}
}
