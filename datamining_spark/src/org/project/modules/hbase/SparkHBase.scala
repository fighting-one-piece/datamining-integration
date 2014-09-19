package org.project.modules.hbase

import org.apache.spark.SparkContext
import org.apache.spark._
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

object SparkHBase extends Serializable {

  def main(args: Array[String]) {
    val sc = new SparkContext("spark://centos.host1:7077", "SparkHBase")

    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.property.clientPort", "2181");
    conf.set("hbase.zookeeper.quorum", "centos.host1");
    conf.set("hbase.master", "centos.host1:60000");
//    conf.set("hbase.mapreduce.inputtable", "user")
    conf.addResource("/home/hadoop/software/hbase-0.92.2/conf/hbase-site.xml")
    conf.set(TableInputFormat.INPUT_TABLE, "user")

    val admin = new HBaseAdmin(conf)
    //	var tableName = "user"
    //    if (!admin.isTableAvailable(tableName)) {
    //      print("Table Not Exists! Create Table")
    //      val tableDesc = new HTableDescriptor(tableName)
    //      tableDesc.addFamily(new HColumnDescriptor("basic".getBytes()));
    //      admin.createTable(tableDesc)
    //    } else {
    //      print("Table Already Exists!")
    //      val columnDesc = new HColumnDescriptor("extend");
    //      admin.disableTable(Bytes.toBytes(tableName));
    //      admin.addColumn(tableName, columnDesc);
    //      admin.enableTable(Bytes.toBytes(tableName));
    //    }

    if (!admin.isTableAvailable("test")) {
      print("Table Not Exists! Create Table")
      val tableDesc = new HTableDescriptor("test")
      tableDesc.addFamily(new HColumnDescriptor("basic".getBytes()));
      admin.createTable(tableDesc)
    }

    val table = new HTable(conf, "user");
    for (i <- 1 to 5) {
      var put = new Put();
      put = new Put(Bytes.toBytes("row" + i));
      put.add(Bytes.toBytes("basic"), Bytes.toBytes("name"), Bytes.toBytes("value " + i));
      table.put(put);
    }
    table.flushCommits();

    val hbaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    val count = hbaseRDD.count()
    println("HBase RDD Count:" + count)
    hbaseRDD.cache()

    val res = hbaseRDD.take(count.toInt)
    for (j <- 1 until count.toInt) {
      println("j: " + j)
      var rs = res(j - 1)._2
      var kvs = rs.raw
      for (kv <- kvs)
        println("rowkey:" + new String(kv.getRow()) +
          " cf:" + new String(kv.getFamily()) +
          " column:" + new String(kv.getQualifier()) +
          " value:" + new String(kv.getValue()))

      //	    kvs.foreach(kv => println("rowkey:"+ new String(kv.getRow) + 
      //	              " cf:"+new String(kv.getFamily()) + 
      //	              " column:" + new String(kv.getQualifier) + 
      //	              " value:"+new String(kv.getValue())))
    }

    System.exit(0)
  }
}