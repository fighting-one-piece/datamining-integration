package org.project.modules.graphx

import org.apache.spark.SparkContext
import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.graphx.GraphLoader
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.VertexRDD

object SparkGraphx {

  def main(args: Array[String]) {

    val sc = new SparkContext("spark://centos.host1:7077", "Spark Graphx")

    // Create an RDD for the vertices
    val users: RDD[(VertexId, (String, String))] = sc.parallelize(Array(
        (3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")),
        (5L, ("franklin", "prof")), (2L, ("istoica", "prof"))))
    // Create an RDD for the edges
    val relationships: RDD[Edge[String]] = sc.parallelize(Array(
        Edge(3L, 7L, "collab"), Edge(5L, 3L, "advisor"),
        Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi")))
    // Define a default user in case there are relationship with missing user
    val defaultUser = ("John Doe", "Missing")
    // Build the initial Graph
    val graph = Graph(users, relationships, defaultUser)
    
    // Count all users which are postdocs
    val fcount1 = graph.vertices.filter { case (id, (name, pos)) => pos == "postdoc" }.count
    println("postdocs users count: " + fcount1)
    // Count all the edges where src > dst
    val fcount2 = graph.edges.filter(edge => edge.srcId > edge.dstId).count
    println("srcId > dstId edges count: " + fcount2)
    val fcount3 = graph.edges.filter { case Edge(src, dst, prop) => src > dst }.count
    println("srcId > dstId edges count: " + fcount3)
    
    // Use the triplets view to create an RDD of facts.
    val facts: RDD[String] = graph.triplets.map(triplet => triplet.srcAttr._1 + " is the " + triplet.attr + " of " + triplet.dstAttr._1)
    facts.collect().foreach(println(_))
    
    // Use the implicit GraphOps.inDegrees operator
    val inDegrees: VertexRDD[Int] = graph.inDegrees
    inDegrees.collect().foreach(println)
    
    // Use the implicit GraphOps.outDegrees operator
    val outDegrees: VertexRDD[Int] = graph.outDegrees
    outDegrees.collect().foreach(println)
    
    // Use the implicit GraphOps.degrees operator
    val degrees: VertexRDD[Int] = graph.degrees;
    degrees.collect().foreach(println)
    
    // Remove missing vertices as well as the edges to connected to them
    val validGraph = graph.subgraph(vpred = (id, attr) => attr._2 != "Missing")
    // The valid subgraph will disconnect users 4 and 5 by removing user 0
    validGraph.vertices.collect.foreach(println(_))
    validGraph.triplets.map(
    triplet => triplet.srcAttr._1 + " is the " + triplet.attr + " of " + triplet.dstAttr._1).collect.foreach(println(_))
    
    val degreeGraph = graph.outerJoinVertices(outDegrees) { (id, oldAttr, outDegOpt) =>
    	outDegOpt match {
    		case Some(outDeg) => outDeg
    		case None => 0 // No outDegree means zero outDegree
    	}
}
    
    var path = "/user/hadoop/data/temp/graph/graph.txt"
    var minEdgePartitions = 1
    var canonicalOrientation = false // if sourceId < destId this value is true
    val graph = GraphLoader.edgeListFile(sc, path, canonicalOrientation, minEdgePartitions,
      StorageLevel.MEMORY_ONLY, StorageLevel.MEMORY_ONLY)

    val verticesCount = graph.vertices.count
    println(s"verticesCount: $verticesCount")

    graph.vertices.collect().foreach(println)

    val edgesCount = graph.edges.count
    println(s"edgesCount: $edgesCount")

    graph.edges.collect().foreach(println)

    //PageRank
    val pageRankGraph = graph.pageRank(0.001)

    pageRankGraph.vertices.sortBy(_._2, false).saveAsTextFile("/user/hadoop/data/temp/graph/graph.pr")

    pageRankGraph.vertices.top(5)(Ordering.by(_._2)).foreach(println)

    //TriangleCount主要用途之一是用于社区发现 保持sourceId小于destId
    val graph1 = GraphLoader.edgeListFile(sc, path, true)

    val triangleCountGraph = graph1.triangleCount()

    triangleCountGraph.vertices.sortBy(_._2, false).saveAsTextFile("/user/hadoop/data/temp/graph/graph.tc")

    triangleCountGraph.vertices.top(5)(Ordering.by(_._2)).foreach(println)

  }
}