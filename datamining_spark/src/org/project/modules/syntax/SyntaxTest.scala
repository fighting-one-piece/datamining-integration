package org.project.modules.syntax

import scala.collection.mutable.HashMap

object SyntaxTest {

  def main(args: Array[String]) {
    
    val arr = Array("one", "two", "three") 
    arr.foreach(println)
    
    val map = new HashMap[Int, String]()
    map += (1 -> "one")
    map += (2 -> "two")
    
    map.foreach(println)
    map.clear()
    map.foreach(println)
    
    arr.foreach(println)
  }
}