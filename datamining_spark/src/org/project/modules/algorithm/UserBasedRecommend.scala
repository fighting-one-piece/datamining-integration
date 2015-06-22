package org.project.modules.algorithm

import org.apache.spark.SparkContext
import scala.collection.mutable.HashMap

case class Rating(userId:String, movieId:String, rating:String)

object UserBasedRecommend extends Serializable {

  def main(args:Array[String]) {
    
    val sc = new SparkContext("spark://centos.host1:7077", "User Based Recommend")
    
    val ratings = sc.textFile("/user/hadoop/data/temp/recommend/input1/ratings.dat")
    ratings.map(line => line.split("::")).map(data => Rating(data(0), data(1), data(2)))
    
    
    
  }
  
  
}