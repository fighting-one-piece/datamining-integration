package org.project.modules.classifier.regression.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

public class DataSetHandler {

	public static DataSet load(String path) {
		DataSet dataSet = null;
		try {
			dataSet = load(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return dataSet;
	}
	
	public static DataSet load(InputStream inStream) {
		List<Instance> instances = new ArrayList<Instance>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inStream));
			String line = reader.readLine();
			Instance instance = null;
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				instance = new Instance();
				instance.setFeature1(Double.parseDouble(tokenizer.nextToken()));
				instance.setFeature2(Double.parseDouble(tokenizer.nextToken()));
				instance.setCategory(Double.parseDouble(tokenizer.nextToken()));
				instances.add(instance);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(reader);
		}
		return new DataSet(instances);
	}
}
