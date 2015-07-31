package org.project.modules.syntax

object UseCurring extends App {

  def multiple(x: Int, y: Int) = x * y
  def multiple(x: Int) = (y: Int) => x * y
  println(multiple(1)(2))
  val m = multiple(1)
  println(m(2))
 
  def curring(x: Int)(y: Int) = x * y
  println(multiple(1)(2))
  println(multiple(1))
  
  val a = Array("Hello", "Spark")
  val b = Array("hello", "spark")
  println(a.corresponds(b)(_.equalsIgnoreCase(_)))
}