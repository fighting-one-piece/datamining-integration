package org.project.modules.mmseg4j;

import java.util.*;
import java.lang.reflect.Method;

import org.tartarus.snowball.*;

public class SplitWords {
	/* 分隔符的集合 */
	private final String delimiters = " \t\n\r\f~!@#$%^&*()_+|`-=\\{}[]:\";'<>?,./'";
	/* 语言 */
	private final String language = "english";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String[] split(String source) {
		/* 根据分隔符分词 */
		StringTokenizer stringTokenizer = new StringTokenizer(source,
				delimiters);
		/* 所有的词 */
		Vector vector = new Vector();
		/* 全大写的词 -- 不用提词干所以单独处理 */
		Vector vectorForAllUpperCase = new Vector();
		/* 根据大写字母分词 */
		flag0: while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			/* 全大写的词单独处理 */
			boolean allUpperCase = true;
			for (int i = 0; i < token.length(); i++) {
				if (!Character.isUpperCase(token.charAt(i))) {
					allUpperCase = false;
				}
			}
			if (allUpperCase) {
				vectorForAllUpperCase.addElement(token);
				continue flag0;
			}
			/* 非全大写的词 */
			int index = 0;
			flag1: while (index < token.length()) {
				flag2: while (true) {
					index++;
					if ((index == token.length())
							|| !Character.isLowerCase(token.charAt(index))) {
						break flag2;
					}
				}
				vector.addElement(token.substring(0, index).toLowerCase());
				token = token.substring(index);
				index = 0;
				continue flag1;
			}
		}
		/* 提词干 */
		try {
			Class stemClass = Class.forName("org.tartarus.snowball.ext."
					+ language + "Stemmer");
			SnowballProgram stemmer = (SnowballProgram) stemClass.newInstance();
			Method stemMethod = stemClass.getMethod("stem", new Class[0]);
			Object[] emptyArgs = new Object[0];
			for (int i = 0; i < vector.size(); i++) {
				stemmer.setCurrent((String) vector.elementAt(i));
				stemMethod.invoke(stemmer, emptyArgs);
				vector.setElementAt(stemmer.getCurrent(), i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 合并全大写的词 */
		for (int i = 0; i < vectorForAllUpperCase.size(); i++) {
			vector.addElement(vectorForAllUpperCase.elementAt(i));
		}
		/* 转为数组形式 */
		String[] array = new String[vector.size()];
		Enumeration enumeration = vector.elements();
		int index = 0;
		while (enumeration.hasMoreElements()) {
			array[index] = (String) enumeration.nextElement();
			index++;
		}
		/* 打印显示 */
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		/* 返回 */
		return array;
	}

	public static void main(String args[]) {
		SplitWords sw = new SplitWords();
		sw.split("These tables are for ARE-Company using only.");
	}
}
