package org.project.modules.algorithm.emotion;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.project.common.document.Document;
import org.project.common.document.DocumentLoader;
import org.project.common.document.DocumentSet;
import org.project.modules.algorithm.featureselect.FSChiSquare;
import org.project.modules.algorithm.featureselect.IFeatureSelect;

public class SemanticOrientationPointMutualInformation {
	
	//褒义词
	private static Set<String> commendatories = null;
	//贬义词
	private static Set<String> derogratories = null;
	
	static {
		commendatories = new HashSet<String>();
		String commendatoryStr = "英俊 美丽 优雅 活泼 时尚 聪明 能干 健康 勤劳 阳光 好学 俏丽 忠心 善良 坚强 独立 团结 优美 义气 智慧 大度 豁达 开朗 富有 专心 勤劳 乐观 可爱 热心 孝顺 妩媚 丽人 矜持 佳丽 柔美 婉丽 娉婷 婉顺 娇柔 可爱 温柔 体贴 贤惠 贤慧 才干 人才  袅娜 赞美 赞赏";
		StringTokenizer tokenizer = new StringTokenizer(commendatoryStr);
		while (tokenizer.hasMoreTokens()) {
			commendatories.add(tokenizer.nextToken());
		}
		derogratories = new HashSet<String>();
		String derogratoryStr = "矮小 猥琐 萎靡 奸诈 歹毒 毒辣 丑陋 弱智 愚笨 愚蠢 阴暗 贬斥 否定 憎恨 轻蔑 责骂 叛逆 汉奸 低能 恶心 阴险 白痴 变态 三八 腐败 呆板 呆滞 土气 无能 懒惰 慵懒 庸才 废物 风骚 下贱 病夫 脆弱 俗气 小气 贫穷 贫贱 花心 悲观 市井 小人 幼稚";
		tokenizer = new StringTokenizer(derogratoryStr);
		while (tokenizer.hasMoreTokens()) {
			derogratories.add(tokenizer.nextToken());
		}
	}
	
	//初始化文本
	private DocumentSet initData() {
		DocumentSet documentSet = null;
		try {
			String path = SemanticOrientationPointMutualInformation.class.getClassLoader().getResource("微测").toURI().getPath();
			documentSet = DocumentLoader.loadDocumentSetByThread(path);
			reduceDimensionsByCHI(documentSet);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return documentSet;
	}
	
	//开方检验特征选择降维
	public void reduceDimensionsByCHI(DocumentSet documentSet) {
		IFeatureSelect featureSelect = new FSChiSquare();
		featureSelect.handle(documentSet);
		List<Document> documents = documentSet.getDocuments();
		for (Document document : documents) {
			Map<String, Double> chiWords = document.getChiWords();
			List<Map.Entry<String, Double>> list = sortMap(chiWords);
			int len = list.size() < 100 ? list.size() : 100;
			String[] words = new String[len];
			for (int i = 0; i < len; i++) {
				words[i] = list.get(i).getKey();
			}
			document.setWords(words);
		}
	}
	
	public List<Map.Entry<String, Double>> sortMap(Map<String, Double> map) {
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
	
	/**
	 * 计算点间互信息
	 * @param word1
	 * @param word2
	 * @param documents
	 * @return
	 */
	private double calculatePMI(String word1, String word2, List<Document> documents) {
		double w1w2Num = statisticsWordsInDocs(documents, word1, word2);
		if (w1w2Num == 0) return 0;
		double w1Num = statisticsWordsInDocs(documents, word1);
		double w2Num = statisticsWordsInDocs(documents, word2);
		return Math.log(documents.size() * w1w2Num / (w1Num * w2Num)) / Math.log(2);
	}
	
	/**
	 * 统计词出现文档数
	 * @param documents
	 * @param words
	 * @return
	 */
	private double statisticsWordsInDocs(List<Document> documents, String... words) {
		double sum = 0;
		for (Document document : documents) {
			Set<String> wordSet = document.getWordSet();
			int num = 0;
			for (String word : words) {
				if (wordSet.contains(word)) num++;
			}
			if (num == words.length) sum++; 
		}
		return sum;
	}
	
	public int judgeEmotion(double value) {
		return value == 0 ? Emotion.NEUTRAL : (value > 0 ? Emotion.COMMENDATORY : Emotion.DEROGRATORY); 
	}
	
	public void build() {
		DocumentSet documentSet = initData();
		Set<String> cs = new HashSet<String>();
		Set<String> ds = new HashSet<String>();
		List<Document> documents = documentSet.getDocuments();
		for (Document document : documents) {
			String[] words = document.getWords();
			for (String word : words) {
				double c = 0.0;
				for (String commendatory : commendatories) {
					c += calculatePMI(word, commendatory, documents);
				}
				if (c != 0) System.out.println("c: " + c);
				double d = 0.0;
				for (String derogratory : derogratories) {
					d += calculatePMI(word, derogratory, documents);
				}
				double value = c - d;
				if (value != 0) System.out.println("value: " + value);
				if (value > 0) {
					cs.add(word);
					commendatories.add(word);
				} else if (value < 0) {
					ds.add(word);
					derogratories.add(word);
				}
			}
		}
		System.out.println(cs);
		System.out.println(ds);
		System.out.println(commendatories);
		System.out.println(derogratories);
	}
	
	public static void main(String[] args) {
		new SemanticOrientationPointMutualInformation().build();
		System.exit(0);
	}
	
}
