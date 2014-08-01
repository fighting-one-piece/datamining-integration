package org.project.modules.hbase;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.project.modules.hbase.utils.HBaseUtils;

public class HBaseUtilsTest {
	
	@Test
	public void testCreateTable() {
		HBaseUtils.creatTable("user", new String[]{"basic","detail"});
	}
	
	@Test
	public void testInsert() {
		HBaseUtils.putRecord("user", "0120140722a", "basic", "name", "user01");
		HBaseUtils.putRecord("user", "0220140722b", "basic", "name", "user02");
		HBaseUtils.putRecord("user", "0320140723c", "basic", "name", "user03");
		HBaseUtils.putRecord("user", "0420140724d", "basic", "name", "user04");
		HBaseUtils.putRecord("user", "0520140725e", "basic", "name", "user05");
		HBaseUtils.putRecord("user", "0620140726f", "basic", "name", "user06");
		HBaseUtils.putRecord("user", "0720140727g", "basic", "name", "user07");
		HBaseUtils.putRecord("user", "0820140728h", "basic", "name", "user08");
		HBaseUtils.putRecord("user", "0920140729i", "basic", "name", "user09");
		HBaseUtils.putRecord("user", "1020140722j", "basic", "name", "user10");
		HBaseUtils.putRecord("user", "1120140723k", "basic", "name", "user11");
		HBaseUtils.putRecord("user", "1220140724l", "basic", "name", "user12");
		HBaseUtils.putRecord("user", "1320140721m", "basic", "name", "user13");
		HBaseUtils.putRecord("user", "1420140721n", "basic", "name", "user14");
		HBaseUtils.putRecord("user", "1520140722o", "basic", "name", "user15");
		HBaseUtils.putRecord("user", "1620140728p", "basic", "name", "user16");
		HBaseUtils.putRecord("user", "1720140725q", "basic", "name", "user17");
		HBaseUtils.putRecord("user", "0120140722a", "basic", "age", "17");
		HBaseUtils.putRecord("user", "0220140722b", "basic", "age", "18");
		HBaseUtils.putRecord("user", "0320140723c", "basic", "age", "19");
		HBaseUtils.putRecord("user", "0420140724d", "basic", "age", "17");
		HBaseUtils.putRecord("user", "0520140725e", "basic", "age", "18");
		HBaseUtils.putRecord("user", "0620140726f", "basic", "age", "17");
		HBaseUtils.putRecord("user", "0720140727g", "basic", "age", "18");
		HBaseUtils.putRecord("user", "0820140728h", "basic", "age", "19");
		HBaseUtils.putRecord("user", "0920140729i", "basic", "age", "18");
		HBaseUtils.putRecord("user", "1020140722j", "basic", "age", "18");
		HBaseUtils.putRecord("user", "1120140723k", "basic", "age", "19");
		HBaseUtils.putRecord("user", "1220140724l", "basic", "age", "18");
		HBaseUtils.putRecord("user", "1320140721m", "basic", "age", "18");
		HBaseUtils.putRecord("user", "1420140721n", "basic", "age", "17");
		HBaseUtils.putRecord("user", "1520140722o", "basic", "age", "18");
		HBaseUtils.putRecord("user", "1620140728p", "basic", "age", "18");
		HBaseUtils.putRecord("user", "1720140725q", "basic", "age", "19");
	}
	
	@Test
	public void testGet() {
		Scan scan = new Scan();
		scan.setBatch(1000);
		scan.setCaching(1000);
		scan.setMaxVersions(1);
		scan.setStartRow(Bytes.toBytes("10101"));
		scan.setStopRow(Bytes.toBytes("11101"));
		long start = System.currentTimeMillis();
		ResultScanner resultScanner = HBaseUtils.getRecords("doc", scan);
		HBaseUtils.printRecords(resultScanner);
		long end = System.currentTimeMillis();
		System.out.println("spend time: " + (end - start) / 1000);
	}
	
	@Test
	public void testGetWithStartAndStop() {
		Scan scan = new Scan();
		scan.setStartRow("0120140722".getBytes());
		scan.setStopRow("1720140725".getBytes());
		ResultScanner resultScanner = HBaseUtils.getRecords("user", scan);
		HBaseUtils.printRecords(resultScanner);
	}
	
	@Test
	public void testGetWithStartAndStopAndFilter() {
		Scan scan = new Scan();
		scan.setStartRow("0120140722".getBytes());
		scan.setStopRow("1720140725".getBytes());
		scan.setFilter(new PrefixFilter("0".getBytes()));
		ResultScanner resultScanner = HBaseUtils.getRecords("user", scan);
		HBaseUtils.printRecords(resultScanner);
	}
	
	@Test
	public void testGetWithFilter() {
		Scan scan = new Scan();
		scan.setFilter(new KeyOnlyFilter());
		ResultScanner resultScanner = HBaseUtils.getRecords("user", scan);
		HBaseUtils.printRecords(resultScanner);
	}
	
	@Test
	public void testGetWithFilter1() {
		Scan scan = new Scan();
		scan.setFilter(new FirstKeyOnlyFilter());
		ResultScanner resultScanner = HBaseUtils.getRecords("user", scan);
		HBaseUtils.printRecords(resultScanner);
	}
	
	@Test
	public void testTableRowCountFilter() {
		long rowCount = HBaseUtils.rowCount("user");
		System.out.println("rowCount: " + rowCount);
	}
	
	@Test
	public void testTableRowCount() {
		String coprocessorClassName = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
		HBaseUtils.addTableCoprocessor("user", coprocessorClassName);
		long rowCount = HBaseUtils.rowCount("user", "basic");
		System.out.println("rowCount: " + rowCount);
	}

	@Test
	public void testAll() {
		try {
			String tablename = "scores";
			HBaseUtils.deleteTable(tablename);
			String[] familys = { "grade", "course" };
			HBaseUtils.creatTable(tablename, familys);

			// add record zkb
			HBaseUtils.putRecord(tablename, "zkb", "grade", "", "5");
			HBaseUtils.putRecord(tablename, "zkb", "course", "", "90");
			HBaseUtils.putRecord(tablename, "zkb", "course", "math", "97");
			HBaseUtils.putRecord(tablename, "zkb", "course", "art", "87");
			// add record baoniu
			HBaseUtils.putRecord(tablename, "baoniu", "grade", "", "4");
			HBaseUtils.putRecord(tablename, "baoniu", "course", "math", "89");

			System.out.println("===========get one record========");
			Result result = HBaseUtils.getRecord(tablename, "zkb");
			HBaseUtils.printRecord(result);

			System.out.println("===========show all record========");
			HBaseUtils.getRecords(tablename);

			System.out.println("===========del one record========");
			HBaseUtils.deleteRecord(tablename, "baoniu");
			ResultScanner resultScanner = HBaseUtils.getRecords(tablename);
			HBaseUtils.printRecords(resultScanner);

			System.out.println("===========show all record========");
			HBaseUtils.getRecords(tablename);
			HBaseUtils.printRecords(resultScanner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
