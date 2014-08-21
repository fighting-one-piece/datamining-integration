package org.project.modules.algorithm.featureselect;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.project.common.document.Document;
import org.project.common.document.DocumentHelper;
import org.project.common.document.DocumentSet;

/**
 * 特征选择-期望交叉熵
 */
public class FSExpectedCrossEntropy extends AbstractFeatureSelect {

	@Override
	public void handle(DocumentSet documentSet) {
		List<Document> documents = documentSet.getDocuments();
		int document_count = documents.size();
		Map<String, Integer> wordToCount = 
				DocumentHelper.wordsInDocsStatistics(documents);
		double total_word_count = 0;
		for (Map.Entry<String, Integer> entry : wordToCount.entrySet()) {
			total_word_count += entry.getValue();
		}
		Map<String, Integer> categoryToCount = 
				DocumentHelper.categoriesInDocsStatistics(documents);
		Set<String> categories = new HashSet<String>();
		for (Map.Entry<String, Integer> entry : categoryToCount.entrySet()) {
			categories.add(entry.getKey());
		}
		Map<String, List<Document>> categoryToDocs = 
				DocumentHelper.docsInCategoriesStatistics(documents);
		Map<String, Double> wordsEce = new HashMap<String, Double>();
		Collection<String> words = DocumentHelper.wordsInDocs(documents, true);
		for (String word : words) {
			double pt = wordToCount.get(word).doubleValue() / total_word_count;
			List<Document> wInDocs = DocumentHelper.wordInDocs(word, documents);
			double w_in_doc_count = wInDocs.size();
			double sum = 0, cd = 0, dd = 0;
			for (String category : categories) {
				List<Document> cInDocs = DocumentHelper.categoryInDocs(category, wInDocs);
				double pc = categoryToCount.get(category).doubleValue() / document_count;
				double c_in_doc_count = cInDocs.size();
				double pct = c_in_doc_count / w_in_doc_count;
				if (pct != 0) sum += pct * Math.log10(pct / pc);
				List<Document> cDocs = categoryToDocs.get(category);
				List<Document> wDocs = DocumentHelper.wordInDocs(word, cDocs);
				double nct = wDocs.size(); 
				cd += nct / w_in_doc_count;
				dd += nct / cDocs.size();
			}
			double ece = cd * dd * pt * sum;
			wordsEce.put(word, ece);
		}
//		printTopN(sortMap(wordsEce), 500);
		documentSet.setSelectedFeatures(wordsEce);
	}
	
	public static void main(String[] args) {
		new FSExpectedCrossEntropy().build();
	}
}
