package org.project.modules.syntax

class AbstractClass {

}

abstract class A {
  def method
}

class B extends A {
  def method { 
    println("B extends A");
  }
}

object AbstractClass extends App {
	val b = new B
	b.method
}