package org.project.modules.classifier.decisiontree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.classifier.decisiontree.builder.TreeBuilder;
import org.project.modules.classifier.decisiontree.builder.TreeC45Builder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGainWritable;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeKVWritable;
import org.project.modules.classifier.decisiontree.mr.writable.TreeNodeWritable;
import org.project.modules.classifier.decisiontree.node.BranchNode;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.modules.hadoop.mapreduce.a.writable.DateTableWritable;
import org.project.utils.HDFSUtils;
import org.project.utils.JSONUtils;
import org.project.utils.ShowUtils;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;

public class DecisionTreeMRTest {

	public static final String DFS_URL = "hdfs://centos.host1:9000/user/hadoop/data/dt/";
	// public static final String DFS_URL =
	// "hdfs://hadoop-namenode-1896:9000/user/hadoop_hudong/project/rf/";

	private Configuration conf = new Configuration();

	private JsonGenerator jsonGenerator = null;

	private ObjectMapper objectMapper = null;

	@Before
	public void before() {
		conf.addResource(new Path(
				"D:\\develop\\data\\hadoop\\hadoop-1.0.4\\conf\\core-site.xml"));
		objectMapper = new ObjectMapper();
		try {
//			FilterProvider filterProvider = new SimpleFilterProvider()
//					.addFilter("a", SimpleBeanPropertyFilter
//							.serializeAllExcept(new String[]{"attributeValues"}));
//			objectMapper.setFilters(filterProvider);
			SerializationConfig cfg = objectMapper.getSerializationConfig();
			SimpleFilterProvider filterProvider = new SimpleFilterProvider();
			filterProvider.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(
					new String[]{"attributeValues"}));
			cfg.withFilters(filterProvider);
			objectMapper.setSerializationConfig(cfg);

			jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(
					System.out, JsonEncoding.UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void destory() {
		try {
			if (jsonGenerator != null) {
				jsonGenerator.flush();
			}
			if (!jsonGenerator.isClosed()) {
				jsonGenerator.close();
			}
			jsonGenerator = null;
			objectMapper = null;
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void tree2json() {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_10.txt";
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		TreeNode treeNode = (TreeNode) treeBuilder.build(data);
		Set<TreeNode> treeNodes = new HashSet<TreeNode>();
		TreeNodeHelper.splitTreeNode(treeNode, 25, 0, treeNodes);
		for (TreeNode node : treeNodes) {
			StringBuilder sb = new StringBuilder();
			TreeNodeHelper.treeNode2json(node, sb);
			System.out.println(sb.toString());
		}
	}
	
	@Test
	public void json() throws JsonProcessingException, IOException {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trains14.txt";
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		TreeNode tree = (TreeNode) treeBuilder.build(data);
		TreeNodeHelper.print(tree, 0, null);
		System.out.println("jsonGenerator");
		String jsonData = JSONUtils.object2json(tree,
				new String[] { "attributeValues" });
		System.out.println(jsonData);
		System.out.println("jacksonGenerator");
		jsonGenerator.writeObject(tree);
		System.out.println();
		StringBuilder sb = new StringBuilder();
		handle(tree, sb);
		System.out.println(sb.toString());
		
		TreeNode temp = (TreeNode) TreeNodeHelper.json2TreeNode(sb.toString());
		System.out.println(temp.getName());
		ShowUtils.printToConsole(temp.getChildren());
	}
	
	private void handle(TreeNode treeNode, StringBuilder sb) {
		sb.append("{");
		sb.append("\"attribute\":");
		sb.append("\"" + treeNode.getName()).append("\",");
		Map<Object, Object> children = treeNode.getChildren();
		if (children.size() != 0) {
			sb.append("\"children\":");
			sb.append("{");
			int i = 0;
			for (Map.Entry<Object, Object> entry : children.entrySet()) {
				i++;
				Object value = entry.getValue();
				sb.append("\"" + entry.getKey() + "\":");
				if (value instanceof TreeNode) {
//					sb.append("\"" + entry.getKey() + "\":");
					handle((TreeNode) value, sb);
//					if (i != children.size()) sb.append(",");
				} else {
//					sb.append("\"" + entry.getKey() + "\":");
					sb.append("\"" + value + "\"");
//					if (i != children.size()) sb.append(",");
				}
				if (i != children.size()) sb.append(",");
			}
			sb.append("}");
		}
		sb.append("}");
	}
	
	@Test
	public void fastJson() throws IOException {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trains14.txt";
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		TreeNode tree = (TreeNode) treeBuilder.build(data);
		TreeNodeHelper.print(tree, 0, null);
		JSONWriter writer = new JSONWriter(new FileWriter("d:\\tree.json"));
		writer.startArray();
		writer.writeValue(tree);
		writer.endArray();
		writer.close();
		JSONReader reader = new JSONReader(new FileReader("d:\\tree.json"));
		reader.startArray();
		while (reader.hasNext()) {
			TreeNode treeNode = reader.readObject(TreeNode.class);
			System.out.println(treeNode.getName());
			System.out.println(treeNode.getChildren());
		}
		reader.endArray();
		reader.close();
	}
	
	@Test
	public void fastJsonW() throws IOException {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_1.txt";
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		TreeNode tree = (TreeNode) treeBuilder.build(data);
		JSONWriter writer = new JSONWriter(new PrintWriter(System.out));
		writer.startObject();
		writer.writeValue(tree);
		writer.endObject();
		writer.close();
		
	}
	
	@Test
	public void fastJsonR() throws IOException {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trains14.txt";
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		TreeNode tree = (TreeNode) treeBuilder.build(data);
		TreeNodeHelper.print(tree, 0, null);
		StringBuilder sb = new StringBuilder();
		handle(tree, sb);
		String jsonData = sb.toString();
		jsonData = "{" + jsonData + "}";
		System.out.println(jsonData);
		StringReader sreader = new StringReader(jsonData);
		JSONReader reader = new JSONReader(sreader);
		reader.startObject();
		while (reader.hasNext()) {
			TreeNode treeNode = reader.readObject(TreeNode.class);
			System.out.println(treeNode.getName());
			System.out.println(treeNode.getChildren());
			break;
		}
		reader.endObject();
		reader.close();
	}

	@Test
	public void jackson() throws JsonProcessingException, IOException {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_10.txt";
		Data data = DataLoader.loadNoId(trainFilePath);
		DataHandler.fill(data, 0);
		TreeNode tree = (TreeNode) treeBuilder.build(data);
		System.out.println("jsonGenerator");
		// writeObject可以转换java对象，eg:JavaBean/Map/List/Array等
		jsonGenerator.writeObject(tree);
		System.out.println();
	}
	
	@Test
	public void appendFile() {
		InputStream in = null;
		FSDataOutputStream out = null;
		try {
			in = new FileInputStream(new File("D:\\resources\\a.txt"));
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "004/output/part-00000");
			out = fs.append(path);
			IOUtils.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void writeSequenceFile() {
		SequenceFile.Writer writer = null;
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "005/output/part-m-00005");
			writer = SequenceFile.createWriter(fs, conf, path,
					LongWritable.class, TreeNodeWritable.class);
			LongWritable key = new LongWritable(1);
			Builder treeBuilder = new DecisionTreeC45Builder();
			String trainFilePath = "d:\\trainset_extract_1.txt";
			Data data = DataLoader.loadNoId(trainFilePath);
			DataHandler.fill(data, 0);
			TreeNode treeNode = (TreeNode) treeBuilder.build(data);
			Set<TreeNode> treeNodes = new HashSet<TreeNode>();
			TreeNodeHelper.splitTreeNode(treeNode, 25, 0, treeNodes);
			for (TreeNode node : treeNodes) {
				StringBuilder sb = new StringBuilder();
				TreeNodeHelper.treeNode2json(node, sb);
				System.out.println("--" + sb.toString());
				TreeNodeWritable value = new TreeNodeWritable(node);
				writer.append(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void writeSequenceFile1() {
		SequenceFile.Writer writer = null;
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "002/output/part");
			writer = SequenceFile.createWriter(fs, conf, path,
					LongWritable.class, BranchNode.class);
			LongWritable key = new LongWritable(1);
//			String src = "d:\\trains14_id.txt";
			String src = "d:\\trainset_extract_1_l.txt";
			Data data = DataLoader.loadWithId(src);
			DataHandler.fill(data, 1.0);
			TreeBuilder builder = new TreeC45Builder();
			BranchNode treeNode = (BranchNode) builder.build(data);
			ShowUtils.printToConsole(treeNode.getValues());
//			TreeNodeHelper.print(treeNode, 0, null);
			writer.append(key, treeNode);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	@Test
	public void readFile() {
//		String url = "hdfs://centos.host1:9000/user/hadoop/data/dt/temp/input/23ab9cf400b1488c81b08266c99bf291/part-r-00000";
		String url = "hdfs://centos.host1:9000/user/hadoop/data/dt/temp/input/23ab9cf400b1488c81b08266c99bf291/";
		List<String> inputs = new ArrayList<String>();
		Path outputPath = new Path(url);
		try {
			FileSystem fs = outputPath.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, outputPath);
			ShowUtils.printToConsole(paths);
			for(Path path : paths) {
				System.out.println("split input path: " + path);
//				FileSystem pfs = path.getFileSystem(conf);
				FSDataInputStream in = fs.open(path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = reader.readLine();
				while (null != line && !"".equals(line)) {
					System.out.println(line);
					inputs.add(line);
					line = reader.readLine();
				}
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("inputs size: " + inputs.size());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void readSequenceFile() {
		SequenceFile.Reader reader = null;
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "015/output/part-r-00000");
			reader = new SequenceFile.Reader(fs, path, conf);
			DateTableWritable key = (DateTableWritable) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			IntWritable value = new IntWritable();
			while (reader.next(key, value)) {
				System.out.print(key.getDate() + "_" + key.getTable());
				System.out.println(value.get());
				key = new DateTableWritable();
				value = new IntWritable();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void readSequenceFile1() {
		SequenceFile.Reader reader = null;
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "002/output/part");
			reader = new SequenceFile.Reader(fs, path, conf);
			LongWritable key = (LongWritable) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			BranchNode value = new BranchNode();
			while (reader.next(key, value)) {
				TreeNodeHelper.print(value, 0, null);
				key = new LongWritable();
				value = new BranchNode();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void readReducerFile() {
		SequenceFile.Reader reader = null;
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "001/output1/part-r-00002");
			reader = new SequenceFile.Reader(fs, path, conf);
			Text key = (Text) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			AttributeGainWritable value = new AttributeGainWritable();
			while (reader.next(key, value)) {
				System.out.println(value.getAttribute());
				System.out.println(value.getGainRatio());
				value = new AttributeGainWritable();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void readAttributeStatisticsFile() {
		SequenceFile.Reader reader = null;
		try {
			Set<String> attributes = new HashSet<String>();
			Map<String, Map<Object, Integer>> attributeValueStatistics
				= new HashMap<String, Map<Object, Integer>>();
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "013/output/part-r-00000");
			reader = new SequenceFile.Reader(fs, path, conf);
			AttributeKVWritable key = (AttributeKVWritable) 
					ReflectionUtils.newInstance(reader.getKeyClass(), conf);
			IntWritable value = new IntWritable();
			while (reader.next(key, value)) {
				String attributeName = key.getAttributeName();
				attributes.add(attributeName);
				Map<Object, Integer> valueStatistics = 
						attributeValueStatistics.get(attributeName);
				if (null == valueStatistics) {
					valueStatistics = new HashMap<Object, Integer>();
					attributeValueStatistics.put(attributeName, valueStatistics);
				}
				valueStatistics.put(key.getAttributeValue(), value.get());
//				System.out.print(attributeName + " : ");
//				System.out.println(key.getAttributeValue());
				value = new IntWritable();
			}
			System.out.println(attributes.size());
			System.out.println(attributeValueStatistics.size());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	@Test
	public void listFile() {
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "input");
			ShowUtils.printToConsole(HDFSUtils.getPathFiles(fs, path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
