package org.project.modules.algorithm.featureselect;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;

public abstract class AbstractFeatureSelect implements IFeatureSelect {

	protected DocumentSet obtainDocumentSet() {
		DocumentSet documentSet = new DocumentSet();
		try {
			String path = AbstractFeatureSelect.class.getClassLoader().getResource("测试").toURI().getPath();
			documentSet.setDocuments(DocumentLoader.loadDocumentListByThread(path));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return documentSet;
	}
	
	protected void build() {
		handle(obtainDocumentSet());
	}
	
	protected List<Map.Entry<String, Double>> sortMap(Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = 
				new ArrayList<Map.Entry<String, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				if (o1.getValue().isNaN()) {
					o1.setValue(0.0);
				}
				if (o2.getValue().isNaN()) {
					o2.setValue(0.0);
				}
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		return list;
	}
	
	protected void printTopN(List<Map.Entry<String, Double>> list, int n) {
		int index = 0;
		for (Map.Entry<String, Double> entry : list) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			if ((++index) > n) break;
		}
		System.out.println("--------------------");
	}
	
}
