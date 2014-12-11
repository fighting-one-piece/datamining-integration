package org.project.modules.hadoop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FileOperationTest {
	
	public static String getTitle(String URL) {
		try {
			URL url = new URL(URL);
			URLConnection urlcon = url.openConnection();
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "gbk");
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(isr);
			String tmps = null;
			int start = -1;
			int end = -1;
			int startz = -1;
			while ((tmps = br.readLine()) != null) {
				if (startz >= 0) {
					sb.append(tmps);
				} else {
					// 开始找title
					start = tmps.indexOf("<title>");
					end = tmps.indexOf("/title>");
					// System.out.println(tmps);
					if (start >= 0) {
						// 找到
						if (end >= 0) {
							return tmps.substring(start + 7, end - 1);// 防止乱码
						}
						sb.append(tmps);
						startz = start;
						continue;
					}// 没找到
					continue;
				}
				// 找 /title
				end = sb.indexOf("/title>");
				if (end >= 0) {// 找到
					startz = sb.indexOf("<title>") + 7;
					return sb.substring(startz, end - 1);
				}
				// 没找到

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "";
	}
	
	public static void main(String[] args) {
		System.out.println(getTitle("http://news.163.com/14/1211/18/AD746G3F00014JB5.html"));
	}
}
