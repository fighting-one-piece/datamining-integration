package org.project.modules.syntax

class HelloScala {

}

class Person {
  var name:String = _
  val age:Int = 18
  private[this] var male:String = "f"
}

class User(var name:String, val age:Int) {
  println("this is user constructor")
  println(s"${this.name} - ${this.age}")
  
  var gender:String = _
  
  def this(name:String, age:Int, gender:String) {
    this(name, age)
    this.gender = gender
  }
}

class Admin(name:String, age:Int, gender:String, val permission:String) extends User(name, age, gender) {
  println("this is admin constructor, permission: " + permission)
  
  override def toString() = "this is admin toString method!"
}

object HelloScala {
  
  def helloScala() {
    println("hello scala!")
  }
  
  def hello(name:String):String = {
    "hello " + name
  }
  
  def helloDefault(name:String = "scala"):String = {
    "hello " + name
  }
  
  def add1 = (x:Int, y:Int) => (x + y)
  
  val add2 = (x:Int, y:Int) => (x + y)
  
  def add3(x:Int)(y:Int) = (x + y)
  
  def add4(x:Int*) = {
    x.foreach(println)
    var sum = 0;
    for (i <- 1 to x.length) {
      sum += x(i-1)
    }
    println(sum)
  }
  
  def exp() {
    val v = 1
    val result = if (v > 0) 1 else 0
    println(result)
    var (a, b) = (10, 1)
    while (a > 0) {
      b = b + a
      a = a - 1
    }
    println(b)
    for (i <- 1 to 10) {
      println(i)
    }
    for (i <- 1 until 10) {
      println(i)
    }
    for (i <- 1 to 10 if i % 2 == 0) {
      println(i)
    }
  }
  
  def main(args:Array[String]) {
    println("Hello Scala")
    println(helloScala)
    println(hello("scala"))
    println(add1(1, 2))
    println(add2(1, 2))
    println(add3(1)(2))
    add4(1,2,3,4,5)
    println(helloDefault())
    exp
    val person = new Person
    person.name = "zhangsan"
    println(s"${person.name} - ${person.age}")
    val user = new User("lisi", 18)
    println(user.name)
    val user1 = new User("wangwu", 20, "f")
    println(user1.gender)
    val admin = new Admin("admin", 21, "m", "crud")
    println(admin.toString)
  }
  
}