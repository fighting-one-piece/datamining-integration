package org.project.modules.algorithm.lcs;

public class LCS {

    //最长公共子串
    public static String lcsub(String s1, String s2) {
        if (s1 == null || s1.isEmpty() || s2 == null || s2.isEmpty()) {
            return "";
        }

        int len1 = s1.length();
        int len2 = s2.length();

        int[][] match = new int[len1][len2];
        int maxLength = 0; // 子字符串的最大长度
        int lastIndex = 0; // 最大子字符串中最后一个字符的索引

        for (int i = 0; i < len1; i++) {
            for (int j = 0; j < len2; j++) {

                if (s2.charAt(j) == s1.charAt(i)) {
                    if (i > 0 && j > 0 && match[i-1][j-1] != 0) {
                        match[i][j] = match[i-1][j-1] + 1;
                    } else {
                        match[i][j] = 1;
                    }

                    if (match[i][j] > maxLength) {
                        maxLength = match[i][j];
                        lastIndex = i;
                    }
                } else {
                    match[i][j] = 0;
                }
            }
        }


        if (maxLength == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        // 根据最大索引的位置，回朔出最长子字符串
        for (int i = lastIndex-maxLength+1; i <= lastIndex; i++) {
            sb.append(s1.charAt(i));
        }

        return sb.toString();
    }

    //最长公共子序列
    public static String lcseq(String x,String y) {

        // 设置字符串长度
        int substringLength1 = x.length();
        int substringLength2 = y.length(); // 具体大小可自行设置

        // 构造二维数组记录子问题x[i]和y[i]的LCS的长度
        int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];

        // 从后向前，动态规划计算所有子问题。也可从前到后。
        for (int i = substringLength1 - 1; i >= 0; i--) {
            for (int j = substringLength2 - 1; j >= 0; j--) {
                if (x.charAt(i) == y.charAt(j))
                    opt[i][j] = opt[i + 1][j + 1] + 1;// 状态转移方程
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);// 状态转移方程
            }
        }

        int i = 0, j = 0;
        String z = "";
        while (i < substringLength1 && j < substringLength2) {
            if (x.charAt(i) == y.charAt(j)) {
                z += x.charAt(i);
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1])
                i++;
            else
                j++;
        }
        return z;
    }
}
