package org.project.modules.hbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.project.modules.hbase.solr.SolrManager;

public class SolrTest {

	private HttpSolrServer solrServer = null;

	@SuppressWarnings("static-access")
	@Before
	public void init() {
		solrServer = SolrManager.getInstance().obtainServer();
	}
	
	@Test
	public void testAdd() {
		try {
			SolrInputDocument inDocument = new SolrInputDocument();
			inDocument.addField("id", "1");
			inDocument.addField("title", "我是中国人");
			inDocument.addField("content", "我的祖国,中国共产党");
			inDocument.addField("url", "www.iteye.com");
			solrServer.add(inDocument);
			solrServer.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddCollection() {
		try {
			List<SolrInputDocument> inDocuments = new ArrayList<SolrInputDocument>();
			SolrInputDocument inDocument = new SolrInputDocument();
			inDocument.addField("id", "2");
			inDocument.addField("title", "我是美国人");
			inDocuments.add(inDocument);
			inDocument = new SolrInputDocument();
			inDocument.addField("id", "3");
			inDocument.addField("title", "我是英国人");
			inDocuments.add(inDocument);
			solrServer.add(inDocuments);
			solrServer.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddBean() {
		try {
			solrServer.addBean(new SolrEntity("4", "我是法国人"));
			solrServer.commit();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddBeans() {
		try {
			List<SolrEntity> entities = new ArrayList<SolrEntity>();
			entities.add(new SolrEntity("5", "我是德国人"));
			entities.add(new SolrEntity("6", "我是意大利人"));
			solrServer.addBeans(entities);
			solrServer.commit();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryDocument() {
		try {
			SolrQuery solrQuery = new SolrQuery();
			//solrQuery.setQuery("content:*歌*");
			solrQuery.setQuery("content:共产党*");
			solrQuery.setStart(0);
			solrQuery.setRows(20);
			QueryResponse resp = solrServer.query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			System.out.println("total number: " + docList.getNumFound());
			for (SolrDocument doc : docList) {
				System.out.println("content: " + doc.getFieldValue("content"));
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryBean() {
		try {
			SolrQuery solrQuery = new SolrQuery("中");
			QueryResponse resp = solrServer.query(solrQuery);
			List<SolrEntity> entities = resp.getBeans(SolrEntity.class);
			System.out.println("entity size: " + entities.size());
			for (SolrEntity entity : entities) {
				System.out.println(entity.getId() + " : " + entity.getTitle());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryHighLight() {
		try {
			SolrQuery solrQuery = new SolrQuery("我");
			solrQuery.setStart(0).setRows(5);
			solrQuery.setHighlight(true);
			solrQuery.setHighlightSimplePre("<span class='highlight'>");
			solrQuery.setHighlightSimplePost("</span>");
			solrQuery.setParam("hl.fl", "subject");
			QueryResponse resp = solrServer.query(solrQuery);
			SolrDocumentList docList = resp.getResults();
			for (SolrDocument doc : docList) {
				String id = (String) doc.getFieldValue("id");
				System.out.println(resp.getHighlighting().get(id).get("subject"));
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIndex() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();  
        HttpGet request = new HttpGet("http://www.1ting.com");  
          
        HttpResponse response = client.execute(request);  
        System.out.println("Response Code: " +  
        response.getStatusLine().getStatusCode());  
          
        BufferedReader rd = new BufferedReader(  
            new InputStreamReader(response.getEntity().getContent()));  
        String line = "";  
        while((line = rd.readLine()) != null) {  
        	try {
    			SolrInputDocument inDocument = new SolrInputDocument();
    			inDocument.addField("id", line.length());
    			inDocument.addField("subject_ik", line.length());
    			inDocument.addField("content_ik", line);
    			solrServer.add(inDocument);
    			solrServer.commit();
    		} catch (SolrServerException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} 
        }  
	}

	@Test
	public void testDelete() {
		try {
			solrServer.deleteByQuery("subject:意大利");
			solrServer.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeleteAll() {
		try {
			solrServer.deleteByQuery("*:*");
			solrServer.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
