package org.project.modules.association.similar;

import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentUtils;

public class Similar {

	public static void main(String[] args) throws Exception {
		String path = Similar.class.getClassLoader().getResource("测试").toURI().getPath();
		DocumentSet dataSet = DocumentLoader.loadDocSet(path);
		DocumentUtils.calculateTFIDF(dataSet.getDocs());
		DocumentUtils.calculateSimilarity(dataSet.getDocs());
	}
}
