package org.project.modules.classifier.bayes.builder;

import java.util.List;
import java.util.Map;

import org.project.common.document.Document;
import org.project.common.document.DocumentHelper;
import org.project.modules.classifier.bayes.data.DataSet;


public class MultinomialBuilder extends AbstractBuilder {
	
	@Override
	public void build() {
		DataSet dataSet = obtainDataSet();
		List<Document> trainData = dataSet.getTrainData();
		Map<String, Map<String, Integer>> cmap = 
				DocumentHelper.wordsInCategoriesStatistics(trainData);
		int w_all_count = 0;
		for (Map.Entry<String, Map<String, Integer>> entry : cmap.entrySet()) {
			Map<String, Integer> wmap = entry.getValue();
			for (Map.Entry<String, Integer> kv : wmap.entrySet()) {
				w_all_count += kv.getValue();
			}
		}
		List<Document> testData = dataSet.getTestData();
		for (Document doc : testData) {
			double max = 0;
			String category = null;
			for (Map.Entry<String, Map<String, Integer>> entry : cmap.entrySet()) {
				Map<String, Integer> wmap = entry.getValue();
				double cw_count = 0, cw_all_count = 0;
				for (Map.Entry<String, Integer> kv : wmap.entrySet()) {
					cw_count += 1;
					cw_all_count += kv.getValue();
				}
				cw_all_count += cw_count;
				double ptc = 0;
				for (String word : doc.getWordSet()) {
					double t_count = null == wmap.get(word) ? 1 : wmap.get(word) + 1;
					ptc = ptc == 0 ? t_count / cw_all_count : ptc * (t_count / cw_all_count);
				}
				double pc = cw_all_count / w_all_count;
				double p = ptc * pc;
				System.out.println(ptc + ":" + pc + ":" + p);
				if (p > max) {
					max = p;
					category = entry.getKey();
				}
			}
			System.out.println("doc category: " + doc.getCategory() + " predict: " + category);
		}
	}
	
	public static void main(String[] args) {
		new MultinomialBuilder().build();
	}
}
