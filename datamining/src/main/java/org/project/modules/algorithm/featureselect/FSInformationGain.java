package org.project.modules.algorithm.featureselect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.common.document.Document;
import org.project.common.document.DocumentHelper;
import org.project.common.document.DocumentSet;
import org.project.utils.MathUtils;

/**
 * 特征选择-信息增益
 */
public class FSInformationGain extends AbstractFeatureSelect {

	@Override
	public void handle(DocumentSet documentSet) {
		List<Document> documents = documentSet.getDocuments();
		int document_count = documents.size();
		Map<String, List<Document>> cateToDocs = 
				DocumentHelper.docsInCategoriesStatistics(documents);
		List<String> categories = new ArrayList<String>();
		double pcSum = 0; 
		for (Map.Entry<String, List<Document>> entry : cateToDocs.entrySet()) {
			categories.add(entry.getKey());
			double c_document_count = entry.getValue().size();
			double pc = c_document_count / document_count;
			pcSum += pc * MathUtils.log2(pc);
		}
		Map<String, Double> wordsGain = new HashMap<String, Double>();
		Collection<String> words = DocumentHelper.wordsInDocs(documents, true);
		System.out.println("words: " + words.size());
		for (String word : words) {
			List<Document> wInDocs = DocumentHelper.wordInDocs(word, documents);
			List<Document> wNotInDocs = DocumentHelper.wordNotInDocs(word, documents);
			double w_in_doc_count = wInDocs.size();
			double w_notin_doc_count = wNotInDocs.size();
			double pctSum = 0, pnctSum = 0;
			for (String category : categories) {
				List<Document> cInDocs = DocumentHelper.categoryInDocs(category, wInDocs);
				List<Document> cNotInDocs = DocumentHelper.categoryInDocs(category, wNotInDocs);
				double c_in_doc_count = cInDocs.size();
				double c_notin_doc_count = cNotInDocs.size();
				double pct = c_in_doc_count / w_in_doc_count;
				if (pct != 0) pctSum += pct * MathUtils.log2(pct);
				double pnct = c_notin_doc_count / w_notin_doc_count;
				if (pnct != 0) pnctSum += pnct * MathUtils.log2(pnct);
			}
			System.out.println(pctSum + ":" + pnctSum);
			double pt = w_in_doc_count / document_count;
			double pnt = w_notin_doc_count / document_count;
			double gain = -pcSum + pt * pctSum + pnt * pnctSum;
			wordsGain.put(word, gain);
		}
		printTopN(sortMap(wordsGain), 500);
		documentSet.setFeatureSelect(wordsGain);
	}
	
	public static void main(String[] args) {
		new FSInformationGain().build();
	}

}
