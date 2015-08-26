package org.project.modules.syntax

import scala.collection.mutable.ListBuffer

object UseList {

  def main(args : Array[String]) = {
    var list = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    printList(list)
    printList(increment(list))
    printList(increment_more_effective(list))
    printList(increment_most_effective(list))
  }
  
  def increment(list : List[Int]) : List[Int] = list match {
    case List() => List()
    case head :: tail => head + 1 :: increment(tail)
  }
  
  def increment_more_effective(list : List[Int]) : List[Int] = {
    var result = List[Int]()
    for (element <- list) result = result ::: List(element + 1)
    result
  }
  
  def increment_most_effective(list : List[Int]) : List[Int] = {
    var buffer = new ListBuffer[Int]
    for (element <- list) buffer += element + 1
    buffer.toList
  }
  
  def printList(list : List[Int]) {
    for (element <- list) printf(" %d ", element)
    println()
  }
  
  
  
}