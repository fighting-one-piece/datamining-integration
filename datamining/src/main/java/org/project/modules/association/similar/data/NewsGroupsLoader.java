package org.project.modules.association.similar.data;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.project.utils.ShowUtils;
import org.project.utils.WordUtils;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;

public class NewsGroupsLoader {
	
	private static Logger logger = Logger.getLogger(NewsGroupsLoader.class);
	
	public static NewsGroups load() {
		return load("D:\\resources\\20-news-18828");
	}

	public static NewsGroups load(String path) {
		NewsGroups newsGroups = new NewsGroups();
		Map<String, Set<String>> category2docs = newsGroups.getCategory2docs();
		Map<String, String[]> doc2words = newsGroups.getDoc2words();
		Seg seg = new ComplexSeg(Dictionary.getInstance());
		File root = new File(path);
		for (File categoryFile : root.listFiles()) {
			String category = categoryFile.getName();
			System.out.println("category: " + category);
			Set<String> docs = category2docs.get(category);
			if (null == docs) {
				docs = new HashSet<String>();
				category2docs.put(category, docs);
			}
			for (File docFile : categoryFile.listFiles()) {
				String doc = docFile.getName();
				System.out.println("doc: " + doc);
				docs.add(doc);
				String[] words = doc2words.get(doc);
				if (null == words) {
					words = WordUtils.splitFile(docFile, seg);
					ShowUtils.print(words);
					doc2words.put(doc, words);
				} else {
					logger.info(doc + " has existed!");
				}
			}
		}
		return newsGroups;
	}
	
}
