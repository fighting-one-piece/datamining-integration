package org.project.modules.syntax

object UseBounds {

  
  class Pair[T <: Comparable[T]](val a: T, val b: T) {
    def bigger = if (a.compareTo(b) > 0) a else b
  }
  
  class Pair_Lower[T](val a: T, val b: T) {
    def replace[R >: T](c: R) = new Pair_Lower[R](c, b)
  }
  
  def main(args: Array[String]) {
    val pair = new Pair("Spark", "Hadoop")
    println(pair.bigger)
  }
  
}