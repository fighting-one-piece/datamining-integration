package org.project.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FileUtils {
	
	private FileUtils() {
		
	}
	
	public static File create(String path) {
		File file = new File(path);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdir();
		}
		return file;
	}
	
	public static File[] obtainFiles(String path) {
		List<File> files = new ArrayList<File>();
		List<File> dirs = new ArrayList<File>();
		File file = new File(path);
		if (file.isDirectory()) {
			dirs.add(file);
		}
		while (dirs.size() > 0) {
			for (File temp : dirs.remove(0).listFiles()) {
				if (temp.isDirectory()) {
					dirs.add(temp);
				} else {
					files.add(temp);
				}
			}
		}
		return files.toArray(new File[0]);
	}
	
	public static void obtainFiles(String path, List<File> files) {
		File file = new File(path);
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				obtainFiles(f.getPath(), files);
			}
		} else {
			files.add(file);
		}
	}
	
	public static String[] obtainFilePaths(String path) {
		List<String> filePaths = new ArrayList<String>();
		List<File> dirs = new ArrayList<File>();
		File file = new File(path);
		if (file.isDirectory()) {
			dirs.add(file);
		}
		while (dirs.size() > 0) {
			for (File temp : dirs.remove(0).listFiles()) {
				if (temp.isDirectory()) {
					dirs.add(temp);
				} else {
					filePaths.add(temp.getPath());
				}
			}
		}
		return filePaths.toArray(new String[0]);
	}

	public static String obtainOSTmpPath() {
		String os = System.getProperty("os.name").toLowerCase();
		String tmpPath = null;
		if (os.contains("windows")) {
			tmpPath = System.getProperty("java.io.tmpdir");
		} else if (os.contains("linux")) {
			tmpPath = System.getProperty("user.home") + File.separator 
					+ "temp" + File.separator;
		}
		return tmpPath;
	}
	
	public static String obtainTmpTxtPath() {
		return obtainOSTmpPath() + IdentityUtils.generateUUID() + ".txt";
	}
	
	public static void addLineNum(String input, String output) {
		InputStream in = null;
		BufferedReader reader = null;
		OutputStream out = null;
		BufferedWriter writer = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			out = new FileOutputStream(new File(output));
			writer = new BufferedWriter(new OutputStreamWriter(out));
			String line = reader.readLine();
			StringBuilder sb = null;
			long lineNum = 1;
			while (null != line) {
				sb = new StringBuilder();
				sb.append(lineNum++).append(" ").append(line);
				writer.write(sb.toString());
				writer.newLine();
				line = reader.readLine();
			}
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(writer);
		}
	}
	
	public static List<String> readLines(String path) {
		List<String> lines = new ArrayList<String>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = new FileInputStream(new File(path));
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line) {
				lines.add(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
		}
		return lines;
	}
	
	public static String readContent(String path) {
		StringBuilder sb = new StringBuilder();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = new FileInputStream(new File(path));
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line) {
				sb.append(line).append("\n");
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
		}
		return sb.toString();
	}
	
	public static String writeToTmpFile(String content) {
		String tmpPath = obtainTmpTxtPath();
		OutputStream out = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(new File(tmpPath));
			bw = new BufferedWriter(new OutputStreamWriter(out));
			bw.write(content);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(bw);
		}
		return tmpPath;
	}
	
}
