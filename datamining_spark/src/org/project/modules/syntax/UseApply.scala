package org.project.modules.syntax

class UseApply {

}

class ApplyTest {
  def apply() = "this apply in class"
  def test() {
    println("apply test")
  }
}

//伴生对象，相当于类的静态方法
object ApplyTest {
  def apply() = new ApplyTest
  def static() {
    println("this is a static method")
  }
}

object UserApply extends App {
  ApplyTest.static
  //类名后面加括号，相当于调用伴生对象的apply方法
  val at = ApplyTest()
  at.test
  val at1 = new ApplyTest
  //对象加括号相当于调用对象的apply方法
  println(at1())
}

