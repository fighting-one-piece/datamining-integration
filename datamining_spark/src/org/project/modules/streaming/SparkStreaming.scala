package org.project.modules.streaming

import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds

object SparkStreaming {

  def main(args: Array[String]) {

    val sc = new SparkContext("spark://centos.host1:7077", "Spark Streaming")

    // 创建StreamingContext，1秒一个批次
    val ssc = new StreamingContext(sc, Seconds(1))
    
    // 获得一个DStream负责连接 监听端口:地址
    val serverIP = ""
    val serverPort = 100
    val lines = ssc.socketTextStream(serverIP, serverPort)

    // 对每一行数据操作
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
//    val wordCounts = pairs.reduce(((k, v1),(k, v2)) => (k, v1 + v2))
//    val wordCounts = pairs.reduceByKey(_ + _)

    // 输出结果
//    wordCounts.print()

    ssc.start() // 开始
    ssc.awaitTermination()// 计算完毕退出

  }
}