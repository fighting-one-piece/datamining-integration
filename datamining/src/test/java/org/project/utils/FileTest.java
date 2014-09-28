package org.project.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;

public class FileTest {

	public static void a() throws Exception {
		InputStream in = null;
		BufferedReader br = null;
		OutputStream out = null;
		BufferedWriter bw = null;
		try {
			in = FileTest.class.getClassLoader().getResourceAsStream("dic/emotion/more.txt");
			br = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (null != line && !"".equals(line)) {
				sb.append(line.trim()).append("|1.5").append("\n");
				line = br.readLine();
			}
			out = new FileOutputStream(new File("D:\\resources\\more.txt"));
			bw = new BufferedWriter(new OutputStreamWriter(out));
			bw.write(sb.toString());
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(bw);
		}
	}
	
	public static void main(String[] args) throws Exception {
		a();
	}
}
