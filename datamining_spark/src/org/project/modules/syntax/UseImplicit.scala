package org.project.modules.syntax

class Implicit {

}

class AA {
  
}

class RichA(a:AA) {
  
  def rich() {
    println("rich method")
  }
  
}

object UseImplicit extends App {
  implicit def a2RichA(a:AA) = new RichA(a)
  val a = new AA
  a.rich
  
  def param(implicit name:String) {
    println(name)
  }
  
  implicit val name:String = "implicit param"
    
  param
  param("explicit")
  
  implicit class Calculator(x:Int) {
    def add(y:Int):Int = x + y
  }
  
  println(2.add(2))
}