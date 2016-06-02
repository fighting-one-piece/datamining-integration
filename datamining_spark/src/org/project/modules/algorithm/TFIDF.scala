package org.project.modules.algorithm

import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.rdd.RDD

object TFIDF {

  def main(args : Array[String]) {
    val sc : SparkContext = null

    // Load documents (one per line).
    val documents: RDD[Seq[String]] = sc.textFile("").map(_.split(" ").toSeq)

    val hashingTF = new HashingTF()
    val tf : RDD[org.apache.spark.mllib.linalg.Vector] = hashingTF.transform(documents)

    import org.apache.spark.mllib.feature.IDF

    // Continue from the previous example
    tf.cache()
    val idf = new IDF().fit(tf)
    val tfidf : RDD[org.apache.spark.mllib.linalg.Vector] = idf.transform(tf)
  }
  
  
}