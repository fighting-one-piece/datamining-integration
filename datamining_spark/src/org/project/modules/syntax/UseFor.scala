package org.project.modules.syntax

case class FUser(name : String, isMale : Boolean, children : FUser*)

object UseFor {

  def main(args : Array[String]) {
    val zhangsan = new FUser("zhangsan", true)
    val lisi = new FUser("lisi", false)
    val wangwu = new FUser("wangwu", true, zhangsan, lisi)
    val users = List(zhangsan, lisi, wangwu)
    
    val forUsers = for (user <- users if user.isMale; child <- user.children) 
      yield (user.name, child.name)
    println(forUsers)

    val nforUsers = users filter (user => user.isMale) flatMap (user => (
        user.children map (child => (user.name, child.name))))
    println(nforUsers)
    
    val qforUsers = for (user <- users; child <- user.children if child.name startsWith("l")) yield child
    println(qforUsers)
    
    def map[A, B](list : List[A], f : A => B) : List[B] = {
      for (element <- list) yield f(element)
    }
  
    def flatMap[A, B](list : List[A], f : A => List[B]) : List[B] = {
      for (x <- list; y <- f(x)) yield y
    }
    
    def filter[A](list : List[A], f : A => Boolean) : List[A] = {
      for (element <- list if f(element)) yield element
    }
    
  }
}