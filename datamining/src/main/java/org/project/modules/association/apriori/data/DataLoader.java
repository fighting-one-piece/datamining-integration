package org.project.modules.association.apriori.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class DataLoader {
	
	public static Data load(String path) {
		Data data = null;
		try {
			data = load(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static Data load(InputStream inStream) {
		List<Instance> instances = new ArrayList<Instance>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inStream));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				instances.add(DataHandler.extract(line));
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(reader);
		}
		return new Data(instances);
	}
	
}
