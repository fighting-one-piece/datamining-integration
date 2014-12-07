package org.project.modules.graphx

import org.apache.spark.SparkContext
import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.graphx.GraphLoader
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd.RDD

object Graphx {

  def main(args:Array[String]) {
    
    val sc = new SparkContext("spark://centos.host1:7077", "Spark Graphx")
    
     //创建点RDD
    val users: RDD[(Long, (String, String))] = sc.parallelize(Array(
        (3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")),
        (5L, ("franklin", "prof")), (2L, ("istoica", "prof"))))
    //创建边RDD
    val relationships: RDD[Edge[String]] = sc.parallelize(Array(
        Edge(3L, 7L, "collab"), Edge(5L, 3L, "advisor"),
        Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi")))
    //定义一个默认用户，避免有不存在用户的关系
    val defaultUser = ("John Doe", "Missing")
    //构造Graph
    val g:Graph[(String, String), String] = Graph(users, relationships)
    
    g.vertices.collect.foreach(println)
    
    
  }
}