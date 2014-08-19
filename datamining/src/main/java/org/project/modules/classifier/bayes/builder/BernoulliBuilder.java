package org.project.modules.classifier.bayes.builder;

import java.util.List;
import java.util.Map;

import org.project.common.document.Document;
import org.project.common.document.DocumentHelper;
import org.project.modules.classifier.bayes.data.DataSet;

public class BernoulliBuilder extends AbstractBuilder {

	@Override
	public void build() {
		DataSet dataSet = obtainDataSet();
		List<Document> trainData = dataSet.getTrainData();
		Map<String, List<Document>> cmap = DocumentHelper.docsInCategoriesStatistics(trainData);
		int c_all_count = trainData.size();
		List<Document> testData = dataSet.getTestData();
		for (Document doc : testData) {
			double max = 0;
			String category = null;
			for (Map.Entry<String, List<Document>> entry : cmap.entrySet()) {
				List<Document> docs = entry.getValue();
				double c_count = docs.size() + 2;
				double ptc = 0;
				for (String word : doc.getWordSet()) {
					double t_count = DocumentHelper.wordInDocsStatistics(word, docs) + 1;
					ptc = ptc == 0 ? t_count / c_count : ptc * (t_count / c_count);
				}
				double pc = c_count / c_all_count;
				double p = ptc * pc;
				System.out.println(ptc + ":" + pc + ":" + p);
				if (p > max) {
					max = p;
					category = entry.getKey();
				}
			}
			System.out.println(doc.getName());
			System.out.println("doc category: " + doc.getCategory() + " predict: " + category);
		}
	}
	
	public static void main(String[] args) {
		new BernoulliBuilder().build();
	}

}
