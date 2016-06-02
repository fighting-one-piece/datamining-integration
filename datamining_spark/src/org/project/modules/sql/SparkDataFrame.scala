package org.project.modules.algorithm

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.Row
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkContext
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils
import java.util.Properties

object SparkDataFrame {

  def main(args : Array[String]) {
    val sc = new SparkContext("spark://centos.host1:7077", "Spark Data Frame")
    val sqlContext = new SQLContext(sc)
    
    val url = "jdbc:mysql://192.168.10.1:3306/test"
    val user = "root"
    val password = "123456"
    val dbtable = "user"
      
    val jdbcMap = Map("url" -> url, "user" -> user, "password" -> password, "dbtable" -> dbtable)
    
    val df = sqlContext.read.options(jdbcMap).format("jdbc").load()
    
    
    val properties = new Properties()
    properties.put("user", user)
    properties.put("password", password)
    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
    val tdf = sqlContext.createDataFrame(Seq(("zhangsan", 18),("lisi", 19),("wangwu",20))).toDF("name", "age")
    //Spark 1.5.1版本或者以上版本存在JdbcUtils
    JdbcUtils.saveTable(tdf, url, dbtable, properties)
    
    sc.stop()
  }
  
}