package org.project.modules.algorithm.emotion;

import org.project.utils.ShowUtils;
import org.project.utils.WordUtils;

public class Emotion {
	
	//褒义词
	public static final int COMMENDATORY = 1;
	//贬义词
	public static final int DEROGRATORY = 2;
	//中性词
	public static final int NEUTRAL = 3;
	
	public static int judge(String content) {
		//把内容划分为句子
		String[] sentences = content.split("[,.，。]");
		//判断每个句子的情感
		double cWeight = 0;
		for (String sentence : sentences) {
			double sWeight = 0;
			//判断句子是否是感叹句
			boolean isExclamatorySentence = WordUtils.isExclamatorySentence(sentence);
			String[] words = WordUtils.splitByNlp(sentence);
			ShowUtils.printToConsole(words);
			for (int i = 0, len = words.length; i < len; i++) {
				String word = words[i];
				//判断是否是情感词
				boolean isEmotion = WordUtils.isEmotionWord(word);
				if (!isEmotion) continue;
				double weight = WordUtils.getWordWeight(word);
				int negativeNum = 0;
				String negativeWord = null;
				int index = new Integer(i);
				while (index > 0) {
					String preWord = words[i - 1];
					if (WordUtils.isEmotionWord(preWord)) break;
					//判断副词
					boolean isAdverbs = WordUtils.isAdverbsWord(preWord);
					if (isAdverbs) {
						double aWeight = WordUtils.getWordWeight(preWord);
						weight = weight * aWeight;
					}
					boolean isNegative = WordUtils.isNegativeWord(preWord);
					if (isNegative) {
						if (null == negativeWord) negativeWord = preWord;
						negativeNum += 1;
					}
					index--;
				}
				//判断否定词
				if (negativeNum % 2 == 1) {
					double nWeight = WordUtils.getWordWeight(negativeWord);
					weight = weight * nWeight;
				}
				//判断感叹词
				if (isExclamatorySentence) {
					double iWeight = WordUtils.getInterjectionWeight();
					weight = weight * iWeight;
				}
				sWeight += weight;
			}
			System.out.println("sWeight: " + sWeight);
			cWeight += sWeight;
		}
		System.out.println("cWeight: " + cWeight);
		return cWeight == 0 ? NEUTRAL : (cWeight > 0 ? COMMENDATORY : DEROGRATORY);
	}
	
	public static void main(String[] args) {
		String content = "我不讨厌这个明星";
		System.out.println(judge(content));
		content = "我不是非常喜欢这个明星!";
		System.out.println(judge(content));
		System.exit(0);
	}
	
}
