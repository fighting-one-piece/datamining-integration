package org.project.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StringUtils {

	public static String maxSubSeq(String x, String y) {
		return maxSubSeq(x.toCharArray(), y.toCharArray());
	}

	public static String maxSubSeq(char[] arr1, char[] arr2) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i <= arr1.length; i++) {
			int sum = 0;// 计长度
			String subSeq = "";// 子序列
			int m = 0;
			for (int j = i; j < arr1.length; j++) {
				for (int k = m; k < arr2.length; k++) {
					if (arr1[j] == arr2[k]) {
						m = k + 1;// 标记位置
						sum++;// 计字符串长度
						subSeq += arr1[j];// 每个字符相加
						break;
					}
				}
			}
			map.put(subSeq, sum);
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		if (list.size() == 0) return null;
		return list.get(0).getKey();
	}

	public static void main(String[] args) {
		String x = "ABCBDABACEFGDH";
		String y = "BDCABABDFGH";
		System.out.println(maxSubSeq(x.toCharArray(), y.toCharArray()));
	}
}
