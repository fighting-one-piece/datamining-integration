package org.project.modules.a

import org.apache.spark.SparkContext
import org.apache.spark._
import org.apache.spark.rdd.NewHadoopRDD
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.HTable;

object SparkHBase {

  def main(args: Array[String]) {
    val sc = new SparkContext("spark://centos.host1:7077", "HBaseScala")

    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.property.clientPort", "2181"); 
    conf.set("hbase.zookeeper.quorum", "centos.host1"); 
	conf.set("hbase.master", "centos.host1:60000"); 
	conf.set("hbase.mapreduce.inputtable", "user")
	conf.addResource("/home/hadoop/software/hbase-0.92.2/conf/hbase-site.xml") 
    //conf.set(TableInputFormat.INPUT_TABLE, tableName)

    val admin = new HBaseAdmin(conf)
	var tableName = "user"
    if (!admin.isTableAvailable(tableName)) {
      print("Table Not Exists! Create Table")
      val tableDesc = new HTableDescriptor(tableName)
      tableDesc.addFamily(new HColumnDescriptor("basic".getBytes()));
      admin.createTable(tableDesc)
    } else {
      print("Table Already Exists!")
      val columnDesc = new HColumnDescriptor("basic");
      admin.disableTable(Bytes.toBytes(tableName));
      admin.addColumn(tableName, columnDesc);
      admin.enableTable(Bytes.toBytes(tableName));
    }

    val table = new HTable(conf, "user");
    for (i <- 0 to 5) {
      var put = new Put();
      put = new Put(Bytes.toBytes("row" + i));
      put.add(Bytes.toBytes("basic"), Bytes.toBytes("name"), Bytes.toBytes("value " + i));
      table.put(put);
    }
    table.flushCommits();

    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    val count = hBaseRDD.count()
    print("HBase RDD Count:" + count)
    System.exit(0)
  }
}