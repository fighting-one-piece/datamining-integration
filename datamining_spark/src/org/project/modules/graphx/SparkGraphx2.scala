package org.project.modules.graphx

import org.apache.spark._
import org.apache.spark.SparkContext
import org.apache.spark.graphx._
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.rdd.RDD

object SparkGraphx2 {

  def main(args: Array[String]) {

    val sc = new SparkContext("spark://centos.host1:7077", "Spark Graphx")

    //通过GraphGenerators构建一个随机图
    val numVertices = 100
    val numEParts = 2
    val mu = 4.0
    val sigma = 1.3
    val graph: Graph[Double, Int] = GraphGenerators.logNormalGraph(
      sc, numVertices, numEParts, mu, sigma).mapVertices((id, _) => id.toDouble)
      
    graph.triplets.collect.foreach(triplet => println(triplet.srcId + "-" + triplet.srcAttr + "-" + 
        triplet.attr + "-" + triplet.dstId + "-" + triplet.dstAttr))

    //mapReduceTriplets函数使用样例
    //计算年龄大于自己的关注者的总人数和总年龄
    val olderFollowers: VertexRDD[(Int, Double)] = graph.mapReduceTriplets[(Int, Double)](
      //Map函数
      triplet => {
        if (triplet.srcAttr > triplet.dstAttr) {
          Iterator((triplet.dstId, (1, triplet.srcAttr)))
        } else {
          Iterator.empty
        }
      },
      //Reduce函数
      (a, b) => (a._1 + b._1, a._2 + b._2)
    )
    //计算年龄大于自己的关注者的平均年龄
    val avgAgeOfOlderFollowers: VertexRDD[Double] =
      olderFollowers.mapValues((id, value) => value match {case (count, totalAge) => totalAge / count })
    
    avgAgeOfOlderFollowers.collect.foreach(println(_))

    //定义一个Reduce函数来计算图中最大度的点
    def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
      if (a._2 > b._2) a else b
    }
    val maxInDegree: (VertexId, Int) = graph.inDegrees.reduce(max)
    println(s"maxInDegree: $maxInDegree")
    val maxOutDegree: (VertexId, Int) = graph.outDegrees.reduce(max)
    println(s"maxOutDegree: $maxOutDegree")
    val maxDegrees: (VertexId, Int) = graph.degrees.reduce(max)
    println(s"maxDegrees: $maxDegrees")

    //计算邻居相关函数，这些操作是相当昂贵的，需要大量的重复信息作为他们的通信，因此相同的计算还是推荐用mapReduceTriplets 
    val neighboorIds:VertexRDD[Array[VertexId]] = graph.collectNeighborIds(EdgeDirection.Out)
    val neighboors:VertexRDD[Array[(VertexId, Double)]] = graph.collectNeighbors(EdgeDirection.Out);

    //Pregel API。计算单源最短路径
    val graph1 = GraphGenerators.logNormalGraph(sc, numVertices, numEParts, mu, sigma).mapEdges(e => e.attr.toDouble)
    //定义一个源值 点
    val sourceId: VertexId = 42
    //初始化图的所有点，除了与指定的源值点相同值的点为0.0以外，其他点为无穷大
    val initialGraph = graph1.mapVertices((id, _) => if (id == sourceId) 0.0 else Double.PositiveInfinity)
    //Pregel有两个参数列表，第一个参数列表包括的是：初始化消息、迭代最大数、边的方向(Out)。第二个参数列表包括的是：用户定义的接受消息、计算消息、联合合并消息的函数。
    val sssp = initialGraph.pregel(Double.PositiveInfinity)(
      //点程序
      (id, dist, newDist) => math.min(dist, newDist),
      //发送消息
      triplet => {
        if (triplet.srcAttr + triplet.attr < triplet.dstAttr) {
          Iterator((triplet.dstId, triplet.srcAttr + triplet.attr))
        } else {
          Iterator.empty
        }
      },
      //合并消息
      (a, b) => math.min(a, b)
      )
    println(sssp.vertices.collect.mkString("\n"))

    //aggregateUsingIndex操作
    val setA: VertexRDD[Int] = VertexRDD(sc.parallelize(0L until 100L).map(id => (id, 1)))
    val rddB: RDD[(VertexId, Double)] = sc.parallelize(0L until 100L).flatMap(id => List((id, 1.0), (id, 2.0)))
    val setB: VertexRDD[Double] = setA.aggregateUsingIndex(rddB, _ + _)
    val setC: VertexRDD[Double] = setA.innerJoin(setB)((id, a, b) => a + b)

    sc.stop()
    
  }

}