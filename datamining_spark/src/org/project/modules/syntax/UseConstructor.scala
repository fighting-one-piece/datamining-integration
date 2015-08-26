package org.project.modules.syntax

class Teacher(val name : String, val age : Int) {
  println("this is the primary constructor")
  // _ 是占位符，没任何含义
  var gender : String = _
  private var paramA : String = _
  //只能在当前实例内访问
  private[this] var paramB : String = _
  def this(name : String, age : Int, gender : String) {
    this(name, age)
    this.gender = gender
  }
  
  def accessParamA = paramA
}

class Outer(val name : String) {
  outer => 
    class Inner(val name : String) {
      def access() {
        println("outer : " + outer.name + " inner : " + this.name)
      }
    }
}


object UseConstructor {
  
  def main(args : Array[String]) {
	  val teacher = new Teacher("zhangsan", 22)
	  println(teacher.name)
	  println(teacher.age)
	  println(teacher.gender)
	  println(teacher.accessParamA)
	  
	  val outer = new Outer("zhangsan")
	  val inner = new outer.Inner("lisi")
	  inner.access
	  
  }
  
}