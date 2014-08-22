package org.project.modules.association.similar;

import org.project.common.distance.CosineDistance;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.common.document.DocumentUtils;

public class Similar {

	public static void main(String[] args) throws Exception {
		String path = Similar.class.getClassLoader().getResource("测试").toURI().getPath();
		DocumentSet dataSet = DocumentLoader.loadDocumentSet(path);
		DocumentUtils.calculateTFIDF_0(dataSet.getDocuments());
		DocumentUtils.calculateSimilarity(dataSet.getDocuments(), new CosineDistance());
	}
}
