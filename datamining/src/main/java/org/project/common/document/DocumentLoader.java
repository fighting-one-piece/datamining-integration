package org.project.common.document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.util.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.project.utils.FileUtils;
import org.project.utils.IdentityUtils;
import org.project.utils.WordUtils;
import org.project.utils.http.HttpClientUtils;
import org.project.utils.http.HttpUtils;
import org.project.utils.jdbc.JDBCUtils;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;

public class DocumentLoader {
	
	protected static Logger logger = Logger.getLogger(DocumentLoader.class);
	
	public static void main(String[] args) throws Exception {
		loadURLToFile();
	}
	
	public static DocumentSet loadDocSet(String path) {
		DocumentSet documentSet = new DocumentSet();
		documentSet.setDocuments(loadDocList(path));
		return documentSet;
	}
	
	public static List<Document> loadDocList(String path) {
		List<Document> docs = new ArrayList<Document>();
		File[] files = FileUtils.obtainFiles(path);
		Seg seg = new ComplexSeg(Dictionary.getInstance());
		for (File file : files) {
			Document document = new Document();
			document.setCategory(file.getParentFile().getName());
			document.setName(file.getName());
			document.setWords(WordUtils.splitFile(file, seg));
			docs.add(document);
		}
		return docs;
	}
	
	public static void loadURLToFile() {
		String sql = "select url from doc where channel = 'tech' and source = '163' limit 0,1";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try{
			conn = JDBCUtils.obtainConnection();
			pstmt = conn.prepareStatement(sql);
			result = pstmt.executeQuery();
			while(result.next()) {
//				String url = result.getString(1);
				String url = "http://ent.163.com/14/0819/10/A40NM42F000300B1.html";
				String content = HttpClientUtils.get(url, HttpUtils.ENCODE_GB2312);
				org.jsoup.nodes.Document document = Jsoup.parse(content);
				Elements divs = document.select("div");
				StringBuilder sb = new StringBuilder();
				for (Element div : divs) {
					if(!div.hasAttr("id")) {
						continue;
					}
					String divId = div.attr("id");
					if ("endText".equals(divId)) {
						sb.append(cleanText(div.text())).append("\n");
					} else if ("epContentLeft".equals(divId)) {
						Elements h1s = div.select("h1");
						for (Element h1 : h1s) {
							if(!h1.hasAttr("id")) {
								continue;
							}
							String h1Id = h1.attr("id");
							if ("h1title".equals(h1Id)) {
								sb.append(cleanText(h1.text())).append("\n");
							} 
						}
					}
				}
				System.out.println(sb.toString());
				writeFile("教育", sb.toString());
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} finally{
			JDBCUtils.returnConnection(conn);
			JDBCUtils.release(null, pstmt, result);
		}
	}
	
	public static String cleanText(String text) {
		return Jsoup.clean(text, Whitelist.none()).replace("&nbsp;"," ").replace("&middot;", ".");
	}
	
	public static void writeFile(String category, String context) {
		OutputStream out = null;
		BufferedWriter bw = null;
		try {
			String path = DocumentLoader.class.getClassLoader().getResource("新闻").toURI().getPath();
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
