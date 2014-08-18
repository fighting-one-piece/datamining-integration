package org.project.modules.classifier.decisiontree;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.DataSplit;
import org.project.modules.classifier.decisiontree.data.DataSplitItem;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.utils.DateUtils;
import org.project.utils.FileUtils;
import org.project.utils.HDFSUtils;
import org.project.utils.JSONUtils;
import org.project.utils.ShowUtils;

public class DataTest {

	@Test
	public void a() {
		String path = "d:\\trainset_10000_l.txt";
		Data data = DataLoader.loadWithId(path);
		Map<String, Map<Object, Integer>> a = 
				new HashMap<String, Map<Object, Integer>>();
		for (Instance instance : data.getInstances()) {
			Map<String, Object> attrs = instance.getAttributes();
			for (Map.Entry<String, Object> entry : attrs.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				Map<Object, Integer> b = a.get(key);
				if (null == b) {
					b = new HashMap<Object, Integer>();
					a.put(key, b);
				}
				Integer c = b.get(value);
				b.put(value, null == c ? 1 : c + 1);
			}
		}
		for (Map.Entry<String, Map<Object, Integer>> e : a.entrySet()) {
			System.out.print(e.getKey() + "-->");
			for (Map.Entry<Object, Integer> f : e.getValue().entrySet()) {
				System.out.print(f.getKey() + "--" + f.getValue() + ":");
			}
			System.out.println();
		}
		System.out.println(a.size());
		System.out.println(data.getAttributes().length);
	}
	
	@Test
	public void load() {
		String path = "d:\\trainset_1000_l.txt";
		Set<String> attributes = new HashSet<String>();
		List<Instance> instances = new ArrayList<Instance>();
		InputStream in = null;
		BufferedReader reader = null;
		long start = System.currentTimeMillis();
		try {
			in = new FileInputStream(new File(path));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			Instance instance = null;
			int index = 0;
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				instance = new Instance();
				instance.setId(Long.parseLong(tokenizer.nextToken()));
				instance.setCategory(tokenizer.nextToken());
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					instance.setAttribute(entry[0], entry[1]);
//					instance.getAttributeset().add(new Attribute(entry[0], entry[1]));
					if (!attributes.contains(entry[0])) {
						attributes.add(entry[0]);
					}
				}
				instances.add(instance);
				line = reader.readLine();
				System.out.println(index++);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		long end = System.currentTimeMillis();
		System.out.println("spend time: " + (end - start));
		Data data = new Data(attributes.toArray(new String[0]), instances);
		System.out.println(data.getAttributes().length);
		System.out.println(data.getInstances().size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void jsonInstance() {
		Instance instance = new Instance();
		instance.setId(1L);
		instance.setCategory(1);
		instance.setAttribute("1", 1);
		instance.setAttribute("2", 2);
		instance.setAttribute("3", 3);
		String jsonData = JSONUtils.object2json(instance);
		System.out.println(jsonData);
		JSONObject jsonObject = JSONUtils.json2Object(jsonData);
		Instance i = new Instance();
		i.setId(jsonObject.getLong("id"));
		i.setCategory(jsonObject.get("category"));
		i.setAttributes(jsonObject.getJSONObject("attributes"));
		i.print();
	}
	
	@Test
	public void addLineNum() throws Exception {
		FileUtils.addLineNum("D:\\trainset_100.txt", "D:\\trainset_100_l.txt");
	}
	
	private Set<String> calculateAttribute(String input) {
		Set<String> attributes = new HashSet<String>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				line = reader.readLine();
				tokenizer.nextToken();
				tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					attributes.add(entry[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return attributes;
	}
	
	private Set<String> getAttribute(String input, int n) {
		Set<String> attributes = new HashSet<String>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				line = reader.readLine();
				tokenizer.nextToken();
				tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					if (attributes.size() != n) {
						attributes.add(entry[0]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return attributes;
	}
	
	@Test
	public void viewAttributeNum() {
		String input = "D:\\trainset_extract_1_l.txt";
		System.out.println(calculateAttribute(input).size());
		input = "D:\\trainset_extract_10_l.txt";
		System.out.println(calculateAttribute(input).size());
		input = "D:\\trainset_5000_l.txt";
		System.out.println(calculateAttribute(input).size());
	}
	
	@Test
	public void generateDataFile() {
		String input = "D:\\trainset_10000_l.txt";
		String output = "D:\\attribute_100_r_10000.txt";
		int attributeNum = 100;
		Set<String> attributes = getAttribute(input, attributeNum);
		System.out.println(attributes.size());
		List<Instance> instances = new ArrayList<Instance>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				Instance instance = new Instance();
				instance.setId(Long.parseLong(tokenizer.nextToken()));
				instance.setCategory(tokenizer.nextToken());
				int index = 0;
				while (tokenizer.hasMoreTokens() && index < attributeNum) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					if (attributes.contains(entry[0])) {
						instance.setAttribute(entry[0], entry[1]);
						System.out.println("token: " + index);
						index++;
					}
				}
				Iterator<String> iter = attributes.iterator();
				while (iter.hasNext() && index < attributeNum) {
					String attribute = iter.next();
					Object value = instance.getAttribute(attribute);
					if (null == value) {
						instance.setAttribute(attribute, "1.0");
						System.out.println("add: " + index);
						index++;
					}
				}
				instances.add(instance);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		DataHandler.writeData(output, new Data(
				attributes.toArray(new String[0]), instances));
	}
	
	@Test
	public void compute() {
		Map<Object, Integer> takeValues = new HashMap<Object, Integer>();
		Map<Object, Integer> values = new HashMap<Object, Integer>();
		values.put("a", 3);
		values.put("b", 4);
		values.put("c", 5);
		double valuesCount = 0;
		for (int count : values.values()) {
			valuesCount += count;
		}
		int k = values.keySet().size();
		int temp = 90;
		for (Map.Entry<Object, Integer> entry : values.entrySet()) {
			int value = entry.getValue();
			Double p = value / valuesCount * 90;
			if (--k > 0) {
				takeValues.put(entry.getKey(), p.intValue());
				temp = temp - p.intValue(); 
			} else {
				takeValues.put(entry.getKey(), temp);
			}
		}
		ShowUtils.printToConsole(takeValues);
	}
	
	@Test
	public void compute1() {
		String path = "d:\\trainset_extract_10.txt";
		Data data = DataLoader.load(path);
		System.out.println("data attributes:　" + data.getAttributes().length);
		String p = "d:\\trainset_extract_1.txt";
		Data testData = DataLoader.load(p);
		System.out.println("testdata attributes:　" + testData.getAttributes().length);
		DataHandler.computeFill(testData, data, 1.0);
		DataHandler.writeData("d:\\a.txt", testData);
	}
	
	@Test
	public void split() {
		String path = "d:\\trains14_id.txt";
		Data data = DataLoader.loadWithId(path);
		data.setSplitAttribute("age");
		data.setSplitPoints(new String[]{"middle_aged",
				"youth,senior"});
		DataSplit ds = DataHandler.split(data);
		System.out.println("~~~~~~~");
		for(Instance i : data.getInstances()) {
			i.print();
		}
		System.out.println("~~~~~~~");
		for (DataSplitItem item : ds.getItems()) {
			System.out.println("---");
			for(Instance i : item.getInstances()) {
				i.print();
			}
			System.out.println("---");
		}
	}
	
	@Test
	public void generateTrainSet() {
		String input = "d:\\trainset_extract_100.txt";
		String output = "d:\\trainset.txt";
		int num = 10;
		BufferedReader reader = null;
		InputStream in = null;
		BufferedWriter writer = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			out = new FileOutputStream(new File(output));
			writer = new BufferedWriter(new OutputStreamWriter(out));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				int index = 0;
				while((++index) <= num) {
					writer.write(line);
					writer.newLine();
				}
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
	
	@Test
	public void readTrainSet() {
		String input = "d:\\trainset.txt";
		BufferedReader reader = null;
		InputStream in = null;
		Map<Integer, List<String>> datas = 
				new HashMap<Integer, List<String>>();
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			int index = 0;
			while (!("").equals(line) && null != line) {
				int key = (index++) % 10;
				List<String> lines = datas.get(key);
				if (null == lines) {
					lines = new ArrayList<String>();
					datas.put(key, lines);
				}
				lines.add(line);
				line = reader.readLine();
			}
			System.out.println(index);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		System.out.println(datas.size());
	}
	
	@Test
	public void extractTrainSet() {
		String input = "d:\\trainset_l.txt";
		String output = "d:\\trainset_l_10.txt";
		int linenum = 10;
		BufferedReader reader = null;
		InputStream in = null;
		BufferedWriter writer = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			out = new FileOutputStream(new File(output));
			writer = new BufferedWriter(new OutputStreamWriter(out));
			String line = reader.readLine();
			int index = 0;
			while (!("").equals(line) && null != line) {
				if((++index) > linenum) break;
				writer.write(line);
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
	
	@Test
	public void extractTrainSetByCategory() {
		String input = "d:\\trainset.txt";
		String output = "d:\\trainset_linenum_10.txt";
		int linenum = 10;
		BufferedReader reader = null;
		InputStream in = null;
		BufferedWriter writer = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			out = new FileOutputStream(new File(output));
			writer = new BufferedWriter(new OutputStreamWriter(out));
			Map<String, Integer> map = new HashMap<String, Integer>();
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				String category = tokenizer.nextToken();
				Integer value = map.get(category);
				value = null == value ? 1 : value + 1;
				map.put(category, value);
				if (value <= linenum) {
					writer.write(line);
					writer.newLine();
				}
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
	
	@Test
	public void read() throws Exception {
		Configuration conf = new Configuration();
		conf.addResource(new Path(
				"D:\\develop\\data\\hadoop\\hadoop-1.0.4\\conf\\core-site.xml"));
		String input = "hdfs://centos.host1:9000//user/hadoop/data/dt/015/output/part-r-00000";
		InputStream in = null;
		BufferedReader reader = null;
		try {
			Path inputPath = new Path(input);
			FileSystem fs = inputPath.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, inputPath);
			in = fs.open(hdfsPaths[0]);
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				System.out.println(line);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}
	
	@Test
	public void read1() throws Exception {
		Configuration conf = new Configuration();
		conf.addResource(new Path(
				"D:\\develop\\data\\hadoop\\hadoop-1.0.4\\conf\\core-site.xml"));
		String input = "hdfs://centos.host1:9000//user/hadoop/data/dt/012/output/part-r-00000";
		try {
			Path inputPath = new Path(input);
			FileSystem fs = inputPath.getFileSystem(conf);
			FileStatus fileStatus = fs.getFileStatus(inputPath);
			System.out.println(fileStatus);
			System.out.println(fileStatus.getBlockSize());
			System.out.println(fileStatus.getPath());
			BlockLocation[] blockLocations = fs.getFileBlockLocations(
					fileStatus, 0, fileStatus.getLen());
			System.out.println(blockLocations.length);
			for (BlockLocation blockLocation : blockLocations) {
				System.out.println(blockLocation.getOffset());
				ShowUtils.printToConsole(blockLocation.getNames());
				ShowUtils.printToConsole(blockLocation.getHosts());
				ShowUtils.printToConsole(blockLocation.getTopologyPaths());
				System.out.println(blockLocation.getLength());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	@Test
	public void write() throws Exception {
		Configuration conf = new Configuration();
		conf.addResource(new Path(
				"D:\\develop\\data\\hadoop\\hadoop-1.0.4\\conf\\core-site.xml"));
		String input = "d:\\trainset_extract_100.txt";
		String output = "hdfs://centos.host1:9000/user/hadoop/data/temp/a.txt";
		
		InputStream in = new FileInputStream(new File(input));
		Path outputPath = new Path(output);
		FileSystem fs = outputPath.getFileSystem(conf);
		OutputStream out = fs.create(outputPath);;
		IOUtils.copy(in, out);
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(out);
	}
	
	@Test
	public void write1() throws Exception {
		Configuration conf = new Configuration();
		conf.addResource(new Path(
				"D:\\develop\\data\\hadoop\\hadoop-1.0.4\\conf\\core-site.xml"));
		String input = "d:\\trainset_extract_100.txt";
		String output = "hdfs://centos.host1:9000/user/hadoop/data/temp/a.txt";
		
		InputStream in = new FileInputStream(new File(input));
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Path outputPath = new Path(output);
		FileSystem fs = outputPath.getFileSystem(conf);
		OutputStream out = fs.create(outputPath);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		String line = reader.readLine();
		while (!("").equals(line) && null != line) {
			writer.write(line);
			writer.newLine();
			line = reader.readLine();
		}
		writer.flush();
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(reader);
		IOUtils.closeQuietly(out);
		IOUtils.closeQuietly(writer);
	}
	
	@Test
	public void writeTempLog() {
		String output = "d:\\log.txt";
		BufferedWriter writer = null;
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(output));
			writer = new BufferedWriter(new OutputStreamWriter(out));
			StringBuilder sb = null;
			String[] tables = new String[]{"user", "role", "group", "perm", "ur", "gr", "rp"};
			String[] users = new String[]{"zs", "ls", "ww", "ml", "wq", "qy", "zk", "qw", "oi", "as"};
			Random random = new Random();
			for (int i = 0; i < 10000; i++) {
				sb = new StringBuilder();
				sb.append(DateUtils.date2String(DateUtils.obtainRandomHourDate(), "yyyyMMddhh")).append("\t");
				sb.append(tables[random.nextInt(7)]).append("\t");
				sb.append(users[random.nextInt(10)]).append("\t");
				sb.append(random.nextInt(100));
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
	
	@Test
	public void readTempLog() {
		String input = "d:\\log.txt";
		BufferedReader reader = null;
		InputStream in = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			Map<String, Integer> map = new HashMap<String, Integer>();
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				String date = tokenizer.nextToken();
				String table = tokenizer.nextToken();
				String key = date + "_" + table;
				Integer value = map.get(key);
				value = null == value ? 1 : value + 1;
				map.put(key, value);
				line = reader.readLine();
			}
			ShowUtils.printToConsole(map);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}
	
}
