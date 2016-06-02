package org.project.modules.syntax

import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkContext

object UseUDF {

  def main(args:Array[String]) {
	 val sc = new SparkContext("spark://centos.host1:7077", "SparkSQL")
	 val sqlContext = new SQLContext(sc)
	 
	 def len(bookTitle: String):Int = bookTitle.length
	 sqlContext.udf.register("len", len _)
	 val booksWithLongTitle = sqlContext.sql("select title, author from books where len(title) > 10")
	 
	 def lengthLongerThan(bookTitle: String, length: Int): Boolean = bookTitle.length > length
	 sqlContext.udf.register("longLength", lengthLongerThan _)
	 val booksWithLongThanTitle1 = sqlContext.sql("select title, author from books where longLength(title, 10)")
	 
	 val dataFrame = sqlContext.sql("select title, author from books")
	 val booksWithLongThanTitle2 = dataFrame.filter("longLength(title, 10)")
	 
	 import org.apache.spark.sql.functions._
	 val longLength = udf((bookTitle: String, length: Int) => bookTitle.length > length)
	 import sqlContext.implicits._
	 val booksWithLongThanTitle3 = dataFrame.filter(longLength($"title", lit(10)))
	 
  }
  
}