package org.project.modules.syntax

object UseMap {

  def main(args : Array[String]) {
    val imMap = scala.collection.immutable.Map("one" -> 1, "two" -> 2, "three" -> 3)
    for ((k, v) <- imMap) println((k, v))
    
    println(imMap.get("one"))
    println(imMap.getOrElse("onee", 2))
    
//    imMap += ("one" -> 11)
    
    val imMap1 = for ((k, v) <- imMap) yield (k, v * 2)
    for ((k, v) <- imMap1) println((k, v))
        
    val map = scala.collection.mutable.Map("one" -> 1, "two" -> 2, "three" -> 3)
    map += ("one" -> 11)
    map -= "one"
    for ((k, v) <- map) println((k, v))

    
    val map1 = for ((k, v) <- map) yield (k, v * 2)
    for ((k, v) <- map1) println((k, v))
  
  }
  
}