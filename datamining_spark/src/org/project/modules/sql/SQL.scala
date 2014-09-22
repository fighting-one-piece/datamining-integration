package org.project.modules.sql

import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.catalyst.expressions.Row
import org.apache.spark.sql.hive.HiveContext

//case class User(id: String, name: String, age: String)

object SQL {

  def main(args: Array[String]) {
    val sc = new SparkContext("spark://centos.host1:7077", "SparkSQL")
    //    val sqlContext = new SQLContext(sc)

    //    import sqlContext._
    //    
    //    val usersRDD4 = sqlContext.parquetFile("/user/hadoop/data/temp/user.parquet")
    //    
    //    usersRDD4.groupBy('age)('id > 0).collect().foreach(println)

    //    usersRDD4.registerTempTable("user4")
    //    sqlContext.sql("select * from user4 where age > 18").map(u => "User4 Name: " + u(1)).collect().foreach(println)

    val hiveContext = new HiveContext(sc)

    //	 hiveContext.sql("use hive")  
    hiveContext.sql("show tables").collect().foreach(println)

    hiveContext.hql("select id,name from a").insertInto("b");
    
  }

}