package org.project.modules.classifier.bayes.builder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.modules.classifier.bayes.data.DataSet;
import org.project.utils.RandomUtils;

public abstract class AbstractBuilder implements Builder {
	
	protected List<Document> documents = new ArrayList<Document>();
	
	public AbstractBuilder() {
		initData();
	}
	
	protected void initData() {
		try {
			String path = AbstractBuilder.class.getClassLoader().getResource("测试").toURI().getPath();
			documents = DocumentLoader.loadDocList(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	protected DataSet obtainDataSet() {
		DataSet dataSet = new DataSet();
		Map<String, List<Document>> cateToDocs = new HashMap<String, List<Document>>();
		for (Document document : documents) {
			String category = document.getCategory();
			List<Document> docs = cateToDocs.get(category);
			if (null == docs) {
				docs = new ArrayList<Document>();
				cateToDocs.put(category, docs);
			}
			docs.add(document);
		}
		for (Map.Entry<String, List<Document>> entry : cateToDocs.entrySet()) {
			List<Document> docs = entry.getValue();
			for (int i = 0, len = docs.size(); i < 5; i++, len--) {
				dataSet.getTestData().add(docs.remove(RandomUtils.nextInt(len)));
			}
			dataSet.getTrainData().addAll(docs);
		}
		return dataSet;
	}

	public abstract void build();
}
