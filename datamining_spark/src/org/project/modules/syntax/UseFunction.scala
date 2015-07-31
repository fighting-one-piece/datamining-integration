package org.project.modules.syntax

object UseFunction {

  def main(args: Array[String]) {
    //偏函数
    def sum(a: Int, b: Int, c: Int) = a + b + c
    println(sum(1, 2, 3))
    
    val func_a = sum _
    println(func_a(1, 2, 3))
    println(func_a.apply(1, 2, 3))
    
    val func_b = sum(1, _: Int, 3)
    println(func_b(2))
    
    //闭包
    val data = List(1, 2, 3, 4, 5, 6)
    var data_sum = 0
    data.foreach(data_sum += _)
    println(data_sum)
    
    def add(x: Int) = (y: Int) => x + y
    val add_a = add(1)
    val add_b = add(10)
    println(add_a(2))
    println(add_b(2))
    
  }
  
}