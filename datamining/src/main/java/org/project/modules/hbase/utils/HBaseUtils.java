package org.project.modules.hbase.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.project.modules.hbase.udf.DoubleColumnInterpreter;

public class HBaseUtils {
	
	protected static Logger logger = Logger.getLogger(HBaseUtils.class);
	
	private static HBaseAdmin admin = null;
	
	private static HTablePool tablePool = null;
	
	private static Configuration configuration = null;
	
	/** 初始化配置 **/
	static {
		System.setProperty("hadoop.home.dir", "D:/develop/data/hadoop/hadoop-1.0.4");
//		System.setProperty("HADOOP_MAPRED_HOME", "D:/develop/data/hadoop/hadoop-2.2.0");
		configuration = new Configuration();
		/** 与hbase/conf/hbase-site.xml中hbase.master配置的值相同 */
		configuration.set("hbase.master", "192.168.10.20:60000");
		/** 与hbase/conf/hbase-site.xml中hbase.zookeeper.quorum配置的值相同 */
		configuration.set("hbase.zookeeper.quorum", "192.168.10.20");
		/** 与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同 */
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		//configuration = HBaseConfiguration.create(configuration);
	}
	
	static {
		try {
			admin = new HBaseAdmin(configuration);
			tablePool = new HTablePool(configuration, 10);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	/** 创建一张表*/
	public static void creatTable(String tableName, String[] familys) {
		try {
			if (admin.tableExists(tableName)) {
				logger.info("table "+ tableName + " already exists!");
			} else {
				HTableDescriptor tableDesc = new HTableDescriptor(tableName);
				for (int i = 0; i < familys.length; i++) {
					tableDesc.addFamily(new HColumnDescriptor(familys[i]));
				}
				admin.createTable(tableDesc);
				logger.info("create table " + tableName + " success.");
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} 
	}
	
	/** 表注册Coprocessor*/
	public static void addTableCoprocessor(String tableName, String coprocessorClassName) {
		try {
			admin.disableTable(tableName);
			HTableDescriptor htd = admin.getTableDescriptor(Bytes.toBytes(tableName));
			htd.addCoprocessor(coprocessorClassName);
			admin.modifyTable(Bytes.toBytes(tableName), htd);
			admin.enableTable(tableName);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	/** 统计表行数*/
	public static long rowCount(String tableName, String family) {
		AggregationClient ac = new AggregationClient(configuration);  
		Scan scan = new Scan();
		scan.addFamily(Bytes.toBytes(family));
		scan.setFilter(new FirstKeyOnlyFilter());
		long rowCount = 0;
		try {
			rowCount = ac.rowCount(Bytes.toBytes(tableName), new LongColumnInterpreter(), scan);
		} catch (Throwable e) {
			logger.info(e.getMessage(), e);
		}  
		return rowCount;
	}
	
	/** 统计表行数*/
	@SuppressWarnings("resource")
	public static long rowCount(String tableName) {
		long rowCount = 0;
		try {
			HTable table = new HTable(configuration, tableName);
			Scan scan = new Scan();
//			scan.setFilter(new KeyOnlyFilter());
			scan.setFilter(new FirstKeyOnlyFilter());
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result result : resultScanner) {
				rowCount += result.size();
			}
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
		return rowCount;
	}

	/** 删除表*/
	public static void deleteTable(String tableName) {
		try {
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
			logger.info("delete table " + tableName + " success.");
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} 
	}

	/** 插入一行记录*/
	@SuppressWarnings("resource")
	public static void putRecord(String tableName, String rowKey, String family, String qualifier, String value) {
		try {
			HTable table = new HTable(configuration, tableName);
			Put put = new Put(Bytes.toBytes(rowKey));
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			table.put(put);
			logger.info("insert recored " + rowKey + " to table " + tableName + " success.");
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	/** 批量插入记录*/
	public static void putRecords(String tableName, List<Put> puts) {
		HTable table = null;
		try {
			table = new HTable(configuration, tableName);
			table.put(puts);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			try {
				table.flushCommits();
			} catch (Exception e1) {
				logger.info(e1.getMessage(), e1);
			}
		}
	}
	
	/** 删除一行记录*/
	@SuppressWarnings("resource")
	public static void deleteRecord(String tableName, String... rowKeys) {
		try {
			HTable table = new HTable(configuration, tableName);
			List<Delete> list = new ArrayList<Delete>();
			Delete delete = null;
			for (String rowKey : rowKeys) {
				delete = new Delete(rowKey.getBytes());
				list.add(delete);
			}
			if (list.size() > 0) {
				table.delete(list);
			}
			logger.info("delete recoreds " + rowKeys + " success.");
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
	}

	/** 查找一行记录*/
	@SuppressWarnings({ "resource"})
	public static Result getRecord(String tableName, String rowKey) {
		try {
			HTable table = new HTable(configuration, tableName);
			Get get = new Get(rowKey.getBytes());
			get.setMaxVersions();
			return table.get(get);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
		return null;
	}

	/** 查找所有记录*/
	@SuppressWarnings({"resource" })
	public static ResultScanner getRecords(String tableName) {
		try {
			HTable table = new HTable(configuration, tableName);
			return table.getScanner(new Scan());
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
		return null;
	}
	
	/** 查找所有记录*/
	@SuppressWarnings({"resource" })
	public static ResultScanner getRecords(String tableName, Scan scan) {
		try {
			HTable table = new HTable(configuration, tableName);
			return table.getScanner(scan);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
		return null;
	}
	
	public static void printRecord(Result result) {
		for (KeyValue kv : result.raw()) {
			System.out.print(new String(kv.getRow()) + " ");
			System.out.print(new String(kv.getFamily()) + ":");
			System.out.print(new String(kv.getQualifier()) + " ");
			System.out.print(kv.getTimestamp() + " ");
			System.out.println(new String(kv.getValue()));
		}
	}
	
	public static void printRecords(ResultScanner resultScanner) {
		for (Result result : resultScanner) {
			printRecord(result);
		}
	}
	
	/** 求和*/
	public static double sum(String tableName, String family, String qualifier) {
		AggregationClient ac = new AggregationClient(configuration);  
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
		double sum = 0;
		try {
			sum = ac.sum(Bytes.toBytes(tableName), new DoubleColumnInterpreter(), scan);
		} catch (Throwable e) {
			logger.info(e.getMessage(), e);
		}  
		return sum;
	}
	
	/** 求平均值*/
	public static double avg(String tableName, String family, String qualifier) {
		AggregationClient ac = new AggregationClient(configuration);  
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
		double avg = 0;
		try {
			avg = ac.avg(Bytes.toBytes(tableName), new DoubleColumnInterpreter(), scan);
		} catch (Throwable e) {
			logger.info(e.getMessage(), e);
		}  
		return avg;
	}
	
	public static void closePool() {
		try {
			tablePool.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
