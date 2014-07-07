package org.project.modules.association.apriori.data;

import java.util.StringTokenizer;


public class DataHandler {

	/**
	 * 抽取文本信息
	 * @param line 文本
	 * @param attributes 特征属性集
	 * @return
	 */
	public static Instance extract(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		Instance instance = new Instance();
		instance.setId(Long.parseLong(tokenizer.nextToken()));
		while (tokenizer.hasMoreTokens()) {
			String value = tokenizer.nextToken();
			instance.setValues(value.split(","));
		}
		return instance;
	}
}
