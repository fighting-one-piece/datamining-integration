package org.project.modules.algorithm.genetic.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.apache.solr.util.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.project.utils.FileUtils;
import org.project.utils.IdentityUtils;
import org.project.utils.WordUtils;
import org.project.utils.http.HttpClientUtils;
import org.project.utils.http.HttpUtils;
import org.project.utils.jdbc.JDBCUtils;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;

public class DataLoader {
	
	protected static Logger logger = Logger.getLogger(DataLoader.class);
	
	public static void main(String[] args) throws Exception {
		String path = DataLoader.class.getClassLoader().getResource("新闻").toURI().getPath();
		System.out.println(path);
		File[] files = FileUtils.obtainFiles(path);
		for (File file : files) {
			System.out.println(file.getName());
		}
		loadURLToFile();
//		load("D:\\resources\\20-news-18828");
	}
	
	public static DataSet load(String path) {
		DataSet data = new DataSet();
		File[] files = FileUtils.obtainFiles(path);
		Seg seg = new ComplexSeg(Dictionary.getInstance());
		for (File file : files) {
			Document document = new Document();
			document.setCategory(file.getParentFile().getName());
			document.setName(file.getName());
			document.setWords(WordUtils.splitFile(file, seg));
			data.getDocs().add(document);
		}
		return data;
	}
	
	public static void loadURLToFile() {
		String sql = "select url from doc where channel = 'finance' and source = '163' limit 0,100";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try{
			conn = JDBCUtils.obtainConnection();
			pstmt = conn.prepareStatement(sql);
			result = pstmt.executeQuery();
			while(result.next()) {
				String url = result.getString(1);
				String content = HttpClientUtils.get(url, HttpUtils.ENCODE_GB2312);
				org.jsoup.nodes.Document document = Jsoup.parse(content);
				String text = document.select("body").text();
				String context = Jsoup.clean(text, Whitelist.none()).replace("&nbsp;"," ").replace("&middot;", ".");
				System.out.println(context);
				writeFile("财经", context);
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} finally{
			JDBCUtils.returnConnection(conn);
			JDBCUtils.release(null, pstmt, result);
		}
	}
	
	public static void writeFile(String category, String context) {
		OutputStream out = null;
		BufferedWriter bw = null;
		try {
			String path = DataLoader.class.getClassLoader().getResource("新闻").toURI().getPath();
			path += File.separator + category + File.separator;
			File parent = new File(path);
			if (!parent.exists()) parent.mkdirs();
			out = new FileOutputStream(new File(path + IdentityUtils.generateUUID()));
			bw = new BufferedWriter(new OutputStreamWriter(out));
			bw.write(context);
			bw.flush();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} finally{
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(bw);
		}
	}
	
}
