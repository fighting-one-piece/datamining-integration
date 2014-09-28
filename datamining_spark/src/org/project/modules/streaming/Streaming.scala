
package org.project.modules.streaming

import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.project.modules.log.SparkLog
import org.apache.spark.streaming.StreamingContext._
import org.apache.log4j.Level
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.hadoop.conf.Configuration

object Streaming {

  def main(args: Array[String]) {

    val sc = new SparkContext("spark://centos.host1:7077", "Spark Streaming")

    //创建StreamingContext，1秒一个批次
    val ssc = new StreamingContext(sc, Seconds(20))
    
    val dataDirectory = "/user/hadoop/data/temp/streaming/";
    val inputDStream = ssc.fileStream[LongWritable, Text, TextInputFormat](dataDirectory)
    val rdd = inputDStream.flatMap(_._2.toString().split(" ")).map(word => (word, 1)).reduceByKeyAndWindow(
        (x:Int, y:Int) => (x + y), Seconds(40), Seconds(20))
        
    rdd.print()
    
    //保存流的内容为SequenceFile, 文件名 : "prefix-TIME_IN_MS[.suffix]".
    rdd.saveAsObjectFiles("/user/hadoop/data/temp/rdd", "seq")    
    //保存流的内容为文本文件, 文件名 : "prefix-TIME_IN_MS[.suffix]".
    rdd.saveAsTextFiles("/user/hadoop/data/temp/rdd", "txt")     
    
//    val conf = new Configuration();
//    rdd.saveAsHadoopFiles("rdd", "hadoop", Text.class, IntWritable.class, TextOutputFormat.class, conf)
    
    //开始运行
    ssc.start()
    //计算完毕退出
    ssc.awaitTermination()

    sc.stop()
  }
}