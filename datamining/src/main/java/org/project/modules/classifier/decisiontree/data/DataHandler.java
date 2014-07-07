package org.project.modules.classifier.decisiontree.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.project.utils.FileUtils;

/** 数据处理类*/
public class DataHandler {
	
	/**
	 * 抽取文本信息
	 * @param line 文本
	 * @param attributes 特征属性集
	 * @return
	 */
	public static Instance extract(String line, Set<String> attributes) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		Instance instance = new Instance();
		instance.setCategory(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			String value = tokenizer.nextToken();
			String[] entry = value.split(":");
			instance.setAttribute(entry[0], entry[1]);
			if (!attributes.contains(entry[0])) {
				attributes.add(entry[0]);
			}
		}
		return instance;
	}
	
	/**
	 * 抽取带行号的文本信息
	 * @param line 文本
	 * @param attributes 特征属性集
	 * @return
	 */
	public static Instance extractWithId(String line, Set<String> attributes) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		Instance instance = new Instance();
		instance.setId(Long.parseLong(tokenizer.nextToken()));
		instance.setCategory(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			String value = tokenizer.nextToken();
			String[] entry = value.split(":");
			instance.setAttribute(entry[0], entry[1]);
			if (!attributes.contains(entry[0])) {
				attributes.add(entry[0]);
			}
		}
		return instance;
	}
	
	/** 缺失数据填充默认值*/
	public static void fill(Data data, Object fillValue) {
		fill(data.getInstances(), data.getAttributes(), fillValue);
	}
	
	/** 缺失数据填充默认值*/
	public static void fill(Map<Object, List<Instance>> splits, 
			String[] attributes, Object fillValue) {
		for (List<Instance> instances : splits.values()) {
			fill(instances, attributes, fillValue);
		}
	}
	
	/** 缺失数据填充默认值*/
	public static void fill(List<Instance> instances, String[] attributes, Object fillValue) {
		fillValue = null == fillValue ? 0 : fillValue;
		for (String attribute : attributes) {
			Object attrValue = null;
			for (Instance instance : instances) {
				attrValue = instance.getAttribute(attribute);
				if (null == attrValue) {
					instance.setAttribute(attribute,  fillValue);
				}
			}
		}
	}
	
	/** 移除部分出现次数少的属性并填充其他缺失属性默认值*/
	public static void removeAndFill(Data data, int n, Object fillValue) {
		List<Instance> instances = data.getInstances();
		String[] attributes = data.getAttributes();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Instance instance : instances) {
			Map<String, Object> attrs = instance.getAttributes();
			for (Map.Entry<String, Object> entry : attrs.entrySet()) {
				String key = entry.getKey();
				Integer value = map.get(key);
				map.put(key, null == value ? 1 : value + 1);
			}
		}
		Set<String> removeSet = new HashSet<String>();
		Set<String> remainSet = new HashSet<String>();
		for (Instance instance : instances) {
			Map<String, Object> attrs = instance.getAttributes();
			Object attrValue = null;
			for (int i = 0, attrLen = attributes.length; i < attrLen; i++) {
				attrValue = attrs.get(attributes[i]);
				if (map.get(attributes[i]) < n) {
					removeSet.add(attributes[i]);
					attrs.remove(attributes[i]);
				} else {
					attrs.put(attributes[i], 
							null == attrValue ? fillValue : attrValue);
					remainSet.add(attributes[i]);
				}
			}
		}
		data.setPurningAttributes(removeSet.toArray(new String[0]));
		System.out.println("all attribute size: " + attributes.length);
		System.out.println("remove attribute size: " + removeSet.size());
		System.out.println("remain attribute size: " + remainSet.size());
	}
	
	/** 计算特征值比例填充*/
	public static void computeFill(Data data, Object fillValue) {
		List<Instance> instances = data.getInstances();
		String[] attributes = data.getAttributes();
		Map<String, Map<Object, Integer>> attributeValueStatistics = 
				attributeValueStatistics(instances, attributes);
		computeFill(instances, attributes, attributeValueStatistics, fillValue);
	}
	
	/** 计算特征值比例填充,针对于测试数据集*/
	public static void computeFill(Data testData, Data trainData, Object fillValue) {
		List<Instance> instances = trainData.getInstances();
		String[] attributes = trainData.getAttributes();
		Map<String, Map<Object, Integer>> attributeValueStatistics = 
				attributeValueStatistics(instances, attributes);
		instances = testData.getInstances();
		computeFill(instances, attributes, attributeValueStatistics, fillValue);
	}
	
	public static void computeFill(List<Instance> instances, String[] attributes,
			Map<String, Map<Object, Integer>> attributeValueStatistics, Object fillValue) {
		for (String attribute : attributes) {
			Map<Object, Integer> values = attributeValueStatistics.get(attribute);
			int kCount = values.keySet().size();
			if (kCount <= 1) {
				Object attrValue = null;
				for (Instance instance : instances) {
					attrValue = instance.getAttribute(attribute);
					if (null == attrValue) {
						instance.setAttribute(attribute,  fillValue);
					}
				}
			} else {
				int nullValueCount = 0;
				for (Instance instance : instances) {
					Object value = instance.getAttribute(attribute);
					if (null == value) {
						nullValueCount += 1;
					}
				}
				double valuesCount = 0;
				for (int count : values.values()) {
					valuesCount += count;
				}
				Map<Object, Integer> takeValues = new HashMap<Object, Integer>();
				int temp = nullValueCount;
				for (Map.Entry<Object, Integer> entry : values.entrySet()) {
					Object k = entry.getKey();
					int v = entry.getValue();
					Double p = v / valuesCount * nullValueCount;
					if (--kCount > 0) {
						takeValues.put(k, p.intValue());
						temp = temp - p.intValue(); 
					} else {
						takeValues.put(k, temp);
					}
				}
				Object attrValue = null;
				for (Instance instance : instances) {
					attrValue = instance.getAttribute(attribute);
					if (null == attrValue) {
						for (Map.Entry<Object, Integer> entry : 
							takeValues.entrySet()) {
							Object k = entry.getKey();
							int v = entry.getValue();
							if (v != 0) {
								instance.setAttribute(attribute, k);
								takeValues.put(k, v - 1);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	/** 特征属性统计*/
	public static Set<String> attributeStatistics(List<Instance> instances) {
		Set<String> attributes = new HashSet<String>();
		for (Instance instance : instances) {
			attributes.addAll(instance.getAttributes().keySet());
		}
		return attributes;
	}
	
	/** 特征属性值统计*/
	public static Map<String, Set<String>> attributeValueStatistics(
			List<Instance> instances) {
		Map<String, Set<String>> attributeValueStatistics = 
				new HashMap<String, Set<String>>();
		for (Instance instance : instances) {
			Map<String, Object> children = instance.getAttributes();
			for (Map.Entry<String, Object> entry : children.entrySet()) {
				String key = entry.getKey();
				Set<String> values = attributeValueStatistics.get(key);
				if (null == values) {
					values = new HashSet<String>();
					attributeValueStatistics.put(key, values);
				}
				values.add(String.valueOf(entry.getValue()));
			}
		}
		return attributeValueStatistics;
	}
	
	/** 特征属性值统计*/
	public static Map<String, Map<Object, Integer>> attributeValueStatistics(
			List<Instance> instances, String[] attributes) {
		Map<String, Map<Object, Integer>> attributeValueStatistics = 
				new HashMap<String, Map<Object, Integer>>();
		for (String attribute : attributes) {
			Map<Object, Integer> values = attributeValueStatistics.get(attribute);
			if (null == values) {
				values = new HashMap<Object, Integer>();
				attributeValueStatistics.put(attribute, values);
			}
			for (Instance instance : instances) {
				Object value = instance.getAttribute(attribute);
				if (null == value) continue;
				Integer count = values.get(value);
				values.put(value, null == count ? 1 : count + 1);
			}
		}
		return attributeValueStatistics;
	}
	
	/** *
	 * 投票
	 * @param results
	 * @return
	 */
	public static Object[] vote(List<Object[]> results) {
		int columnNum = results.get(0).length;
		Object[] finalResult = new Object[columnNum];
		for (int i = 0; i < columnNum; i++) {
			Map<Object, Integer> resultCount = new HashMap<Object, Integer>();
			for (Object[] result : results) {
				if (null == result[i]) continue;
				Integer count = resultCount.get(result[i]);
				resultCount.put(result[i], null == count ? 1 : count + 1);
			}
			int max = 0;
			Object maxResult = null;
			for (Map.Entry<Object, Integer> entry : resultCount.entrySet()) {
				if (max < entry.getValue()) {
					max = entry.getValue();
					maxResult = entry.getKey();
				}
			}
			finalResult[i] = maxResult;
		}
		return finalResult;
	}
	
	/** 数据集分割*/
	public static DataSplit split(Data data) {
		DataSplit dataSplit = new DataSplit();
		String splitAttribute = data.getSplitAttribute();
		String[] splitPoints = data.getSplitPoints();
//		List<Instance> copyInstances = data.copyInstances();
		if (null == splitAttribute || null == splitPoints) {
			DataSplitItem item = new DataSplitItem();
			item.setInstances(data.getInstances());
			dataSplit.addItem(item);
			return dataSplit;
		}
		for (String splitPoint : splitPoints) {
			List<Instance> instances = new ArrayList<Instance>();
			for (Instance instance : data.getInstances()) {
				String splitAttributeValue = String.valueOf(
						instance.getAttribute(splitAttribute));
				if (splitPoint.indexOf(splitAttributeValue) != -1) {
					instance.removeAttribute(splitAttribute);
					instances.add(instance);
				}
			}
			String path = FileUtils.obtainRandomTxtPath();
			writeInstances(path, instances);
			DataSplitItem item = new DataSplitItem();
			item.setPath(path);
			item.setSplitPoint(splitPoint);
			item.setInstances(instances);
			dataSplit.addItem(item);
		}
		return dataSplit;
	}
	
	/** 数据集分割*/
	public static DataSet split(Data data, int splitNum, int trainNum, int testNum) {
		List<Instance> instances = data.getInstances();
		System.out.println("data all attributes:　" + data.getAttributes().length);
		System.out.println("data all size:　" + instances.size());
		Iterator<List<Instance>> instancess = DataHandler.split(instances, splitNum);
		Data trainData = new Data();
		while (trainNum > 0) {
			trainData.getInstances().addAll(instancess.next());
			trainNum--;
		}
		Set<String> trainAttributes = DataHandler.attributeStatistics(
				trainData.getInstances());
		trainData.setAttributes(trainAttributes.toArray(new String[0]));
		System.out.println("train data attributes:　" + trainData.getAttributes().length);
		System.out.println("train data instances:　" + trainData.getInstances().size());
		Data testData = new Data();
		while (testNum > 0) {
			testData.getInstances().addAll(instancess.next());
			testNum--;
		}
		Set<String> testAttributes = DataHandler.attributeStatistics(
				testData.getInstances());
		testData.setAttributes(testAttributes.toArray(new String[0]));
		System.out.println("test data attributes:　" + testData.getAttributes().length);
		System.out.println("test data instances:　" + testData.getInstances().size());
		return new DataSet(trainData, testData);
	}
	
	/** 数据集分割*/
	public static Iterator<List<Instance>> split(List<Instance> instances, int splitNum) {
		Map<Integer, List<Instance>> map = new HashMap<Integer, List<Instance>>();
		for (int i = 0, len = instances.size(); i < len; i++) {
			int key = i % splitNum;
			List<Instance> temp = map.get(key);
			if (null == temp) {
				temp = new ArrayList<Instance>();
				map.put(key, temp);
			}
			temp.add(instances.get(i));
		}
		return map.values().iterator();
	}
	
	/** 将数据写入某个文件*/
	public static void writeData(String path, Data data) {
		writeInstances(path, data.getInstances());
	}
	
	/** 将数据写入某个文件*/
	public static void writeInstances(String path, List<Instance> instances) {
		OutputStream out = null;
		BufferedWriter writer = null;
		try {
			File file = FileUtils.create(path);
			out = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			StringBuilder sb = null;
			for (Instance instance : instances) {
				sb = new StringBuilder();
				sb.append(instance.getId()).append("\t");
				sb.append(instance.getCategory()).append("\t");
				Map<String, Object> attrs = instance.getAttributes();
				for (Map.Entry<String, Object> entry : attrs.entrySet()) {
					sb.append(entry.getKey()).append(":");
					sb.append(entry.getValue()).append("\t");
				}
				writer.write(sb.toString());
				writer.newLine();
			}
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(writer);
		}
	}
	
}
