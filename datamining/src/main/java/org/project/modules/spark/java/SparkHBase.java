package org.project.modules.spark.java;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;
 
public class SparkHBase implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	public Log log = LogFactory.getLog(SparkHBase.class);
 
    /**
     * 将scan编码，该方法copy自 org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil
     *
     * @param scan
     * @return
     * @throws IOException
     */
    public static String convertScanToString(Scan scan) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        scan.write(dos);
        return Base64.encodeBytes(out.toByteArray());
    }
 
    @SuppressWarnings("serial")
	public void start() {
        //初始化sparkContext，这里必须在jars参数里面放上Hbase的jar，
        // 否则会报unread block data异常
        JavaSparkContext sc = new JavaSparkContext("spark://nowledgedata-n3:7077", "hbaseTest",
                "/home/hadoop/software/spark-0.8.1",
                new String[]{"target/ndspark.jar", "target\\dependency\\hbase-0.94.6.jar"});
 
        //使用HBaseConfiguration.create()生成Configuration
        // 必须在项目classpath下放上hadoop以及hbase的配置文件。
        Configuration conf = HBaseConfiguration.create();
        //设置查询条件，这里值返回用户的等级
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("195861-1035177490"));
        scan.setStopRow(Bytes.toBytes("195861-1072173147"));
        scan.addFamily(Bytes.toBytes("info"));
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("levelCode"));
 
        try {
            //需要读取的hbase表名
            String tableName = "usertable";
            conf.set(TableInputFormat.INPUT_TABLE, tableName);
            conf.set(TableInputFormat.SCAN, convertScanToString(scan));
 
            //获得hbase查询结果Result
			JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(conf,
                    TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
            
            //从result中取出用户的等级，并且每一个算一次
            JavaPairRDD<Integer, Integer> levels = hBaseRDD.mapToPair(
                    new PairFunction<Tuple2<ImmutableBytesWritable, Result>, Integer, Integer>() {

						@Override
                        public Tuple2<Integer, Integer> call(Tuple2<ImmutableBytesWritable, Result> 
                        	immutableBytesWritableResultTuple2) throws Exception {
                            byte[] o = immutableBytesWritableResultTuple2._2().getValue(
                                    Bytes.toBytes("info"), Bytes.toBytes("levelCode"));
                            if (o != null) {
                                return new Tuple2<Integer, Integer>(Bytes.toInt(o), 1);
                            }
                            return null;
                        }
                    });
 
            //数据累加
			JavaPairRDD<Integer, Integer> counts = levels.reduceByKey(new Function2<Integer, Integer, Integer>() {
                public Integer call(Integer i1, Integer i2) {
                    return i1 + i2;
                }
            });
             
            //打印出最终结果
            List<Tuple2<Integer, Integer>> output = counts.collect();
            for (@SuppressWarnings("rawtypes") Tuple2 tuple : output) {
                System.out.println(tuple._1 + ": " + tuple._2);
            }
 
        } catch (Exception e) {
            log.warn(e);
        }
 
    }
    
    @SuppressWarnings("unused")
	public static void operation() {
    	JavaSparkContext sc = new JavaSparkContext("", "hbaseTest",
    			                System.getenv("SPARK_HOME"), System.getenv("JARS"));
    			Configuration conf = HBaseConfiguration.create();
    			Scan scan = new Scan();
    			scan.addFamily(Bytes.toBytes("cf"));
    			scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("airName"));
    			try {
    			        String tableName = "flight_wap_order_log";
    			        conf.set(TableInputFormat.INPUT_TABLE, tableName);
//    			        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
//    			        String scanToString = Base64.encodeBytes(proto.toByteArray());
    			        String scanToString = convertScanToString(scan);
    			        conf.set(TableInputFormat.SCAN, scanToString);
    			        JavaPairRDD<ImmutableBytesWritable, Result> myRDD = 
    			        		sc.newAPIHadoopRDD(conf,  TableInputFormat.class, 
    			        				ImmutableBytesWritable.class, Result.class);
    			} catch (Exception e) {
    			            e.printStackTrace();
    			}
    }
 
    /**
     * spark如果计算没写在main里面,实现的类必须继承Serializable接口，<br>
     * </>否则会报 Task not serializable: java.io.NotSerializableException 异常
     */
    public static void main(String[] args) throws InterruptedException {
 
        new SparkHBase().start();
 
        System.exit(0);
    }
}
