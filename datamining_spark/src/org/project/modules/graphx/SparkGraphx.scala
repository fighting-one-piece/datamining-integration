package org.project.modules.graphx

import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.graphx.GraphLoader
import org.apache.spark.storage.StorageLevel

object SparkGraphx {

  def main(args:Array[String]) {
    
    val sc = new SparkContext("spark://centos.host1:7077", "Spark Graphx")
    
    var path = "/user/hadoop/data/temp/graph/graph.txt"
    var minEdgePartitions = 1
    var canonicalOrientation = false // if sourceId < destId this value is true
    val graph = GraphLoader.edgeListFile(sc, path, canonicalOrientation, minEdgePartitions, 
        StorageLevel.MEMORY_ONLY, StorageLevel.MEMORY_ONLY)
    
    val verticesCount = graph.vertices.count
    println(s"verticesCount: $verticesCount")
    
//    graph.vertices.collect().foreach(println)
    
    val edgesCount = graph.edges.count
    println(s"edgesCount: $edgesCount")
    
//    graph.edges.collect().foreach(println)
    
    val pageRankGraph = graph.pageRank(0.001)
    
    pageRankGraph.vertices.sortBy(_._2, false).saveAsTextFile("/user/hadoop/data/temp/graph/graph.pr")

    pageRankGraph.vertices.top(5)(Ordering.by(_._2)).foreach(println)
    
    
//    val pageRank = graph.outerJoinVertices(pageRankGraph.vertices) {
//    		case (uid, attrList, Some(pr)) => (pr, attrList)
//    		case (uid, attrList, None) => (0.0, attrList)
//    }
    
//    println(pageRank.vertices.top(5)(Ordering.by(_._2._1)).mkString(","))
    
    //TriangleCount主要用途之一是用于社区发现 保持sourceId小于destId
    val graph1 = GraphLoader.edgeListFile(sc, path, true)
    
    val triangleCountGraph = graph1.triangleCount()
    
    triangleCountGraph.vertices.sortBy(_._2, false).saveAsTextFile("/user/hadoop/data/temp/graph/graph.tc")
    
    triangleCountGraph.vertices.top(5)(Ordering.by(_._2)).foreach(println)
    
  }
}