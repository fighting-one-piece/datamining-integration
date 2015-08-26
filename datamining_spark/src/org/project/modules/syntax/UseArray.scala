package org.project.modules.syntax

import scala.collection.mutable.ArrayBuffer
import scala.util.Sorting

object UseArray {

  def main(args : Array[String]) {
    val arrayBuffer = new ArrayBuffer[Int]()
    arrayBuffer += (1, 2)
    arrayBuffer ++= Array(3, 4)
    //移除开头N个元素
    arrayBuffer.trimStart(1)
    //移除结尾N个元素
    arrayBuffer.trimEnd(1)
    //在指定下标前插入元素
    arrayBuffer.insert(0, 1)
    //在指定下标前插入多个元素
    arrayBuffer.insert(3, 4, 5, 6)
    //移除指定下标元素
    arrayBuffer.remove(5)
    //移除指定下标N个元素
    //rrayBuffer.remove(5, 2)
    val list = arrayBuffer.toList
    for (element <- list) printf(" %d ", element)
    println()
    val array = arrayBuffer.toArray
    for (element <- array) printf(" %d ", element)
    println()
    for (i <- 0 until array.length) printf(" %d - %d", i, array(i))
    println()
    //步长
    for (i <- 0 until (array.length, 2)) printf(" %d - %d", i, array(i))
    println()
    //倒序
    for (i <- (0 until array.length).reverse) printf(" %d - %d", i, array(i))
    println()
    val newArray = for (element <- array) yield element * 2
    for (element <- newArray) printf(" %d ", element)
    println()
    val newArray1 = for (element <- array if element % 2 == 0) yield element * 2
    for (element <- newArray1) printf(" %d ", element)
    println()
    array.filter(_ % 2 == 0).map(_ * 2).foreach(printf(" %d ", _))
    array.filter {_ % 2 == 0}.map{2 * _}.foreach(printf(" %d ", _))
    println()
    //求和、最大值、最小值
    printf(" sum %d ", array.sum)
    printf(" max %d ", array.max)
    printf(" min %d ", array.min)
    //排序
    //val sortedArrayBuffer = arrayBuffer.sorted(_<_)
    Sorting.quickSort(array)
    println(array.mkString("[", " ", "]"))
    //多维矩阵
    val matrix = Array.ofDim[Double](3, 4)
    //矩阵赋值，第一个参数为行，第二个参数为列
    matrix(0)(0) = 1
    //不规则数组
    val triangle = new Array[Array[Int]](5)
    for(i <- 0 until triangle.length) {
      triangle(i) = new Array[Int](i)
    }
    
    
    
  }
  
  
  
}