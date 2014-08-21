package org.project.modules.algorithm.featureselect;

import java.util.List;
import java.util.Set;

import org.project.common.document.Document;
import org.project.common.document.DocumentHelper;
import org.project.common.document.DocumentSet;
import org.project.utils.MathUtils;

/**
 * 特征选择-开方检验
 */
public class FSChiSquare extends AbstractFeatureSelect {

	@Override
	public void handle(DocumentSet documentSet) {
		List<Document> documents = documentSet.getDocuments();
		for (Document document : documents) {
			String category = document.getCategory();
			List<Document> categoryInDocs = 
					DocumentHelper.categoryInDocs(category, documents);
			List<Document> categoryNotInDocs = 
					DocumentHelper.categoryNotInDocs(category, documents);
			Set<String> words = document.getWordSet();
			for (String word : words) {
				double a = DocumentHelper.wordInDocsStatistics(word, categoryInDocs);
				double b = DocumentHelper.wordInDocsStatistics(word, categoryNotInDocs);
				double c = DocumentHelper.wordNotInDocsStatistics(word, categoryInDocs);
				double d = DocumentHelper.wordNotInDocsStatistics(word, categoryNotInDocs);
 				double chi = MathUtils.pow2((a*d - b*c)) / ((a+b) * (c+d));
 				if (Double.isNaN(chi)) continue;
				document.getChiWords().put(word, chi);
			}
//			printTopN(sortMap(document.getChiWords()), 20);
		}
	}
	
	public static void main(String[] args) {
		new FSChiSquare().build();
	}

}
