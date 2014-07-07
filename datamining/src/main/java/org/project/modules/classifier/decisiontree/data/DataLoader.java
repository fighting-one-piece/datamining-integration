package org.project.modules.classifier.decisiontree.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class DataLoader {
	
	public static Data load(String path) {
		Data data = null;
		try {
			data = load(new FileInputStream(new File(path)), false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static Data loadWithId(String path) {
		Data data = null;
		try {
			data = load(new FileInputStream(new File(path)), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static Data load(InputStream inStream, boolean containId) {
		Set<String> attributes = new HashSet<String>();
		List<Instance> instances = new ArrayList<Instance>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inStream));
			String line = reader.readLine();
			Instance instance = null;
			while (!("").equals(line) && null != line) {
				instance = containId ? DataHandler.extractWithId(line, attributes)
						: DataHandler.extract(line, attributes);
				instances.add(instance);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(reader);
		}
		return new Data(attributes.toArray(new String[0]), instances);
	}
	
	public static Data LoadRandom(String path, int attributeNum) {
		return loadRandom(load(path), attributeNum);
	}
	
	public static Data loadRandom(Data data, int attributeNum) {
		String[] attributes = data.getAttributes();
		List<Instance> instances = data.getInstances();
		Random random = new Random();
		Set<String> randomAttributeSet = new HashSet<String>();
		while (randomAttributeSet.size() < attributeNum) {
			randomAttributeSet.add(attributes[random.nextInt(attributes.length)]);
		}
		int instance_len = instances.size();
		int minRandomDataCount = (instance_len / 3) * 2;
		int randomDataCount = minRandomDataCount + 
				random.nextInt(instance_len - minRandomDataCount);
		//不存在相同的数据，即便存在重复数据整体应该也没有什么影响
		Set<Instance> instanceSet = new HashSet<Instance>();
		while (instanceSet.size() < randomDataCount) {
			instanceSet.add(instances.get(random.nextInt(instances.size())));
		}
		System.out.println("random attribute len: " + randomAttributeSet.size());
		System.out.println("random instances len: " + instanceSet.size());
		return new Data(randomAttributeSet.toArray(new String[0]), 
				new ArrayList<Instance>(instanceSet));
	}
	
}
