package org.project.modules.streaming

import org.apache.spark.streaming.flume._  
import org.apache.spark.streaming.StreamingContext  
import org.apache.spark.SparkContext  
import org.apache.spark.streaming.Seconds  
import org.apache.spark.storage.StorageLevel  
  
object SparkStreamingFlume1 {  
    
  def main(args: Array[String]) {  
      
    if (args.length < 2) {    
      print("please enter host and port")  
      System.exit(1)  
    }    
      
    val sc = new SparkContext("spark://centos.host1:7077", "Spark Streaming Flume Integration")  
  
    //创建StreamingContext，20秒一个批次  
    val ssc = new StreamingContext(sc, Seconds(20))  
      
    val hostname = args(0)  
    val port = args(1).toInt  
    val storageLevel = StorageLevel.MEMORY_ONLY  
    val flumeStream = FlumeUtils.createStream(ssc, hostname, port, storageLevel)  
      
    flumeStream.count().map(cnt => "Received " + cnt + " flume events." ).print()  
      
    //开始运行  
    ssc.start()  
    //计算完毕退出  
    ssc.awaitTermination()  
  
    sc.stop()  
  }  
  
}  