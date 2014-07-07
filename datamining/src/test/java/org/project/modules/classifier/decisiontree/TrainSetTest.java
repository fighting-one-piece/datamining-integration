package org.project.modules.classifier.decisiontree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class TrainSetTest {

	public static void main(String[] args) {
//		String filePath = "d:\\trainset_extract_10.txt";
//		Object[] datas = SampleUtils.readDatasFromFile(filePath);
//		Map<String, Integer> attributeMap = (Map<String, Integer>) datas[3];
//		double attribute_all_count = attributeMap.values().size();
//		System.out.println("all count: " + attribute_all_count);
//		System.out.println("all count: " + attributeMap.keySet().size());
//		int i = 0, j = 0, k = 0;
//		for (Map.Entry<String, Integer> entry : attributeMap.entrySet()) {
//			int count = entry.getValue();
//			if (count == 1) {
//				i++;
//			} else if (count == 2) {
//				j++;
//			} else if (count <= 10) {
//				k++;
//			}
//		}
//		System.out.println("1: " + i + " 2: " + j + " 5: " + k);
	}
	
	private static Set<String> obtainAttrs() {
		Set<String> attributes = new HashSet<String>();
//		String filePath = "d:\\trainset_1000.txt";
//		Object[] datas = SampleUtils.readDatasFromFile(filePath);
//		Map<String, Integer> attributeMap = (Map<String, Integer>) datas[3];
//		double attribute_all_count = attributeMap.values().size();
//		System.out.println("all count: " + attribute_all_count);
//		for (Map.Entry<String, Integer> entry : attributeMap.entrySet()) {
//			String attribute = entry.getKey();
//			int count = entry.getValue();
//			if (count < 50) continue;
//			attributes.add(attribute);
//		}
		return attributes;
	}
	
	public static void b() {
		String trainSetPath = "d:\\trainset_1000.txt";
		Set<String> attrs = obtainAttrs();
		int attrs_len = attrs.size();
		System.out.println("attrs_len: " + attrs_len);
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(trainSetPath))));
			String line = reader.readLine();
			int j = 0;
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				tokenizer.nextToken();
				int i = 0;
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					if (attrs.contains(entry[0])) {
						i++;
					}
				}
				if (i > 100) {
					lines.add(line);
					j++;
				}
				line = reader.readLine();
			}
			System.out.println(j);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		int lastIndex = trainSetPath.lastIndexOf(".");
		trainSetPath.substring(0, lastIndex);
		trainSetPath.substring(lastIndex);
		StringBuilder file = new StringBuilder();
		file.append(trainSetPath.substring(0, lastIndex)).append("_")
			.append("extract").append(trainSetPath.substring(lastIndex));
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(file.toString()))));
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
