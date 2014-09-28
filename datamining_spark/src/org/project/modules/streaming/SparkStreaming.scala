package org.project.modules.streaming

import org.apache.log4j.Level
import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext._
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat

object SparkStreaming {

  def main(args: Array[String]) {

    val sc = new SparkContext("spark://centos.host1:7077", "Spark Streaming")

    //创建StreamingContext，20秒一个批次
    val ssc = new StreamingContext(sc, Seconds(20))

    //获得一个DStream来负责TCP连接(监听端口:地址)
    val serverIP = "localhost"
    val serverPort = 9999
    val lines = ssc.socketTextStream(serverIP, serverPort)

    val rdd1 = lines.flatMap(_.split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
    //打印到控制台
    rdd1.print()
    
    //获得一个InputDStream来负责监听文件目录
    val dataDirectory = "/user/hadoop/data/temp/streaming/";

    val inputDStream1 = ssc.fileStream[LongWritable, Text, TextInputFormat](dataDirectory)
    val rdd2 = inputDStream1.flatMap(_._2.toString().split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
    rdd2.print()
    
    val inputDStream2 = ssc.textFileStream(dataDirectory)
    val rdd3 = inputDStream2.flatMap(_.toString().split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
    rdd3.print()
    
    //特定的窗口操作，窗口操作涉及两个参数：一个是滑动窗口的宽度(Window Duration)；另一个是窗口滑动的频率(Slide Duration)
    val inputDStream3 = ssc.fileStream[LongWritable, Text, TextInputFormat](dataDirectory)
    val rdd4 = inputDStream3.flatMap(_._2.toString().split(" ")).map(word => (word, 1)).reduceByKeyAndWindow(
        (x:Int, y:Int) => (x + y), Seconds(40), Seconds(20))
    rdd4.print()
    
    //保存流的内容，文件默认会保存在用户的目录下
    //保存流的内容为SequenceFile, 文件目录名 : "prefix-TIME_IN_MS.suffix" rdd-1411894750000.seq 
    rdd4.saveAsObjectFiles("/user/hadoop/data/temp/rdd", "seq")    
    //保存流的内容为TextFile, 文件目录名 : "prefix-TIME_IN_MS.suffix" rdd-1411894750000.txt
    rdd4.saveAsTextFiles("/user/hadoop/data/temp/rdd", "txt")       
    //保存流的内容为HadoopFile, 文件目录名 : "prefix-TIME_IN_MS.suffix" rdd-1411894750000.hadoop
    //这个API暂时没有正确使用出来
    //rdd4.saveAsHadoopFiles("/user/hadoop/data/temp/rdd", "hadoop")
    //rdd4.saveAsHadoopFiles("/user/hadoop/data/temp/rdd", "hadoop", Text.class, IntWritable.class, TextOutputFormat.class, conf)
    
    //开始运行
    ssc.start()
    //计算完毕退出
    ssc.awaitTermination()

    sc.stop()
  }
  
}