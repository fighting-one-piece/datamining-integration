package org.project.modules.algorithm.featureselect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.common.document.Document;
import org.project.common.document.DocumentHelper;
import org.project.common.document.DocumentSet;

/**
 * 特征选择-互信息
 */
public class FSMutualInformation extends AbstractFeatureSelect {

	@Override
	public void handle(DocumentSet documentSet) {
		List<Document> documents = documentSet.getDocuments();
		Map<String, List<Document>> cateToDocs = 
				DocumentHelper.docsInCategoriesStatistics(documents);
		int document_count = documents.size();
		List<String> categories = new ArrayList<String>();
		Map<String, Double> cateToProb = new HashMap<String, Double>();
		for (Map.Entry<String, List<Document>> entry : cateToDocs.entrySet()) {
			categories.add(entry.getKey());
			double c_document_count = entry.getValue().size();
			double pc = c_document_count / document_count;
			cateToProb.put(entry.getKey(), pc);
		}
		Map<String, Double> wordsMI = new HashMap<String, Double>();
		Collection<String> words = DocumentHelper.wordsInDocs(documents, true);
		for (String word : words) {
			double probSum = 0;
			double pt = DocumentHelper.wordInDocsStatistics(word, documents) / document_count;
			for (String category : categories) {
				double pc = cateToProb.get(category);
				List<Document> docs = cateToDocs.get(category);
				double ptc = DocumentHelper.wordInDocsStatistics(word, docs) / docs.size();
				probSum += pc * Math.log(ptc / pt);
			}
			wordsMI.put(word, probSum);
		}
		documentSet.setFeatureSelect(wordsMI);
	}
	
	public static void main(String[] args) {
		new FSMutualInformation().build();
	}
	
}
