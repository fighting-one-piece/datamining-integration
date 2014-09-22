package org.project.modules.sql

import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.catalyst.types.StructType
import org.apache.spark.sql.catalyst.types.StructField
import org.apache.spark.sql.catalyst.types.StringType
import org.apache.spark.sql.catalyst.expressions.Row
import org.apache.spark.sql.SchemaRDD
import org.apache.spark.sql.hive.HiveContext

case class User(id:String, name:String, age:String)

object SparkSQL {

  def main(args:Array[String]) {
	 val sc = new SparkContext("spark://centos.host1:7077", "SparkSQL")
	 val sqlContext = new SQLContext(sc)
	 
	 //Explicit Apply Schema
	 val schemaTxt = "id name age"  
	 val schema1 = StructType(schemaTxt.split(" ").map(fieldName => StructField(fieldName, StringType, true)))  
	 val usersRDD1 = sc.textFile("/user/hadoop/data/temp/user.txt")
	 		.map(_.split(" ")).map(u => Row(u(0).trim(), u(1).trim(), u(2).trim()))  
	 val users1 = sqlContext.applySchema(usersRDD1, schema1)  
	 users1.registerTempTable("user1");
	 sqlContext.sql("select * from user1").map(u => "User1 Name: " + u(1)).collect().foreach(println)

	 val structFields = new Array[StructField](3)
	 structFields.update(0, StructField("id", StringType, true))
	 structFields.update(1, StructField("name", StringType, true))
	 structFields.update(2, StructField("age", StringType, true))
	 val schema2 = StructType(structFields)
	 val usersRDD2 = sc.textFile("/user/hadoop/data/temp/user.txt")
	 		.map(_.split(" ")).map(u => Row(u(0).trim(), u(1).trim(), u(2).trim()))  
	 val users2 = sqlContext.applySchema(usersRDD2, schema2)  
	 users2.registerTempTable("user2");
	 sqlContext.sql("select * from user2").map(u => "User2 Name: " + u(1)).collect().foreach(println)
	 
	 //Implicit Convert Schema 
	 import sqlContext.createSchemaRDD
	 val usersRDD3 = sc.textFile("/user/hadoop/data/temp/user.txt")
	 		.map(line => line.split(" ")).map(data => User(data(0).trim(), data(1).trim(), data(2).trim()))
	 usersRDD3.registerTempTable("user3");
	 sqlContext.sql("select * from user3").map(u => "User3 Name: " + u(1)).collect().foreach(println)
	 
	 //Parquet File
	 users1.saveAsParquetFile("/user/hadoop/data/temp/user.parquet")
	 val usersRDD4 = sqlContext.parquetFile("/user/hadoop/data/temp/user.parquet")
	 usersRDD4.registerTempTable("user4")
	 sqlContext.sql("select * from user4").map(u => "User4 Name: " + u(1)).collect().foreach(println)
	 
	 //JSON File
     //{"id":"1","name":"zhangsan","age":"18"}{"id":"2","name":"lisi","age":"19"}{"id":"3","name":"wangwu","age":"17"}
	 val usersRDD5 = sqlContext.jsonFile("/user/hadoop/data/temp/user.json")
	 usersRDD5.registerTempTable("user5")
	 sqlContext.sql("select * from user5").map(u => "User5 Name: " + u(1)).collect().foreach(println)
	 
	 //Query Operation
	 sqlContext.sql("select name,age from user5").map{ 
      case Row(name:String, age:String) => s"name: $name, age: $age"}.collect.foreach(println)
      
     sqlContext.sql("select name,age from user5").map{ 
      data => s"name: ${data(0)}, age: ${data(1)}"}.collect.foreach(println)
	 
     import sqlContext._
     
     val count = sqlContext.sql("select count(*) from user4").collect().head.getLong(0)
     println(s"User4 Count: $count")
     
     //DSL
     usersRDD4.where('age > 18).orderBy('id.asc).collect().foreach(println)
     
     usersRDD4.where('age > 18).orderBy('id.asc).select('name as 'n).collect().foreach(println)
     
     usersRDD4.where('age > 18).orderBy('id.asc).select('name as 'n,'age as 'a).collect().foreach(println)
     
     //Hive
	 val hiveContext = new HiveContext(sc)
	 
	 hiveContext.sql("use hive")  
	 hiveContext.sql("show tables").collect().foreach(println)  
	 
	 hiveContext.hql("select id,name from a").insertInto("b");
  }
  
  
}