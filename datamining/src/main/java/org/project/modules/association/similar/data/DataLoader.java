package org.project.modules.association.similar.data;

import java.io.File;

import org.apache.log4j.Logger;
import org.project.utils.FileUtils;
import org.project.utils.WordUtils;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;

public class DataLoader {
	
	protected static Logger logger = Logger.getLogger(DataLoader.class);
	
	public static void main(String[] args) {
		load("D:\\resources\\20-news-18828");
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
			data.getDocuments().add(document);
		}
		return data;
	}
	
}
