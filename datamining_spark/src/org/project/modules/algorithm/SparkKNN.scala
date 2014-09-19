package org.project.modules.algorithm

import org.apache.spark.SparkContext

object SparkKNN extends Serializable {

  def main(args: Array[String]) {
    if (args.length != 4) {
    	println("error, please input three path.");
    	println("1 train set path.");
		println("2 test set path.");
		println("3 output path.");
		println("4 k value.");
		System.exit(1)
    }
    
    val sc = new SparkContext("spark://centos.host1:7077", "Spark KNN")
    
    val trainSet = sc.textFile(args(0)).map(line => {
      var datas = line.split(" ")
      (datas(0), datas(1), datas(2))
    })
    var bcTrainSet = sc.broadcast(trainSet.collect())
    var bcK = sc.broadcast(args(3).toInt)
    
    val testSet = sc.textFile(args(1))
    
    val resultSet = testSet.map(line => {
      var datas = line.split(" ")
      var x = datas(0).toDouble
      var y = datas(1).toDouble
      var trainDatas = bcTrainSet.value
      var set = Set[Tuple6[Double, Double, Double, Double, Double, String]]()
      trainDatas.foreach(trainData => {
    	var tx = trainData._1.toDouble
    	var ty = trainData._2.toDouble
    	var distance = Math.sqrt(Math.pow(x - tx, 2) + Math.pow(y - ty, 2))
    	println(x+"-"+y+"-"+tx+"-"+ty+"-"+distance+"-"+trainData._3)
    	set += Tuple6(x, y, tx, ty, distance, trainData._3)
      })
      var list = set.toList
      var sortList = list.sortBy(item => item._5)
      sortList.foreach(item => println(item))
      var categoryCountMap = Map[String, Int]()
      var k = bcK.value
      for (i <- 0 to (k - 1)) {
        var category = sortList(i)._6
        var count = categoryCountMap.getOrElse(category, 0) + 1
        categoryCountMap += (category -> count)
      }
      var rCategory = ""
      var maxCount = 0
      categoryCountMap.foreach(item => {
        println(item._1 + "-" + item._2.toInt)
        if (item._2.toInt > maxCount) {
          maxCount = item._2.toInt
          rCategory = item._1
        }
      })
      (x, y, rCategory)
    })
      
    resultSet.saveAsTextFile(args(2))
    
    System.exit(0)
  }
}