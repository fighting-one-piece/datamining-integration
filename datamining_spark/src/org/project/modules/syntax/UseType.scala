package org.project.modules.syntax

import scala.io.BufferedSource
import scala.io.Source

class Infix_Type[A, B]

object Infix {
  def >>:(data : String) : Infix.type = { println(data); Infix}
}

class Self {
  //this 别名
  self =>
  val tmp:String = "self"
  def m() : String = this.tmp + self.tmp   
}

trait TLogger {
  def log(msg : String)
}

trait Auth {
  //别名，使用的时候必须要实现TLogger
  auth : TLogger =>
  def act(msg : String) {
    log(msg)
  }
}

object DI extends Auth with TLogger {
  override def log(msg : String) {
    println(msg)
  }
}

trait Reader {
  //抽象类型
  type In <: java.io.Serializable
  type Contents
  def read(in : In) : Contents
}

class FileReader extends Reader {
  type In = String
  type Contents = BufferedSource
  def read(in : In) : Contents = Source.fromFile(in)
}


object UseType {
  "spark">>:"hadoop">>:Infix
  val infix: Int Infix_Type String = null
 
}