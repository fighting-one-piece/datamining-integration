package org.project.modules.algorithm

import org.apache.spark.mllib.classification.{ NaiveBayes, NaiveBayesModel }
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.SparkContext

object SparkNaiveBayes {

  def main(args: Array[String]) {
    
	val sc = new SparkContext("spark://centos.host1:7077", "Spark NativeBayes")
    val data = sc.textFile("data/mllib/sample_naive_bayes_data.txt")
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }
    // Split data into training (60%) and test (40%).
    val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0)
    val test = splits(1)

//    val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")
//
//    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
//    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
//
//    // Save and load model
//    model.save(sc, "myModelPath")
//    val sameModel = NaiveBayesModel.load(sc, "myModelPath")
  }

}