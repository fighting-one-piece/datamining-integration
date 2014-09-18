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
		DataSet dataSet = new DataSet();
		List<double[]> datas = new ArrayList<double[]>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inStream));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				double[] data = new double[2];
				data[0] = Double.parseDouble(tokenizer.nextToken());
				data[1] = Double.parseDouble(tokenizer.nextToken());
				datas.add(data);
				dataSet.getCategories().add(Integer.parseInt(tokenizer.nextToken()));
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(reader);
		}
		dataSet.setDatas(datas.toArray(new double[0][0]));
		return dataSet;
	}
}
