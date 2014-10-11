package org.project.modules.syntax

class UseApply {

}

class ApplyTest {
  def apply() = "this apply in class"
  def test() {
    println("apply test")
  }
}

object ApplyTest {
  def apply() = new ApplyTest
  def static() {
    println("this is a static method")
  }
}

object UserApply extends App {
  ApplyTest.static
  val at = ApplyTest()
  at.test
  val at1 = new ApplyTest
  println(at1())
}

