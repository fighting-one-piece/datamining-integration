package org.project.modules.syntax

class UseTrait {
  
}

//类似与接口，通过with实现多个继承
trait Logger {
  def log(msg:String) {
    println("log: " + msg)
  }
  
  def a(x:String)
}

class ConcreteLogger extends Logger {
  
  def a(x:String) {
    println("concrete a: " + x)
  }
  
  def concreteLog() {
    log("this is concrete logger")
  }
}

trait FLogger {
  def log(msg:String) {
    println("log: " + msg)
  }
  
}

trait FConcreteLogger extends Logger {
  
  override def log(msg:String) {
    println("this is concrete logger: " + msg)
  }
}

abstract class Account {
  def save
}

class UAccount extends Account with Logger {
  
  def a(x:String) {
    println("UAccount a: " + x)
  }
  
  def save {
    log("UAccount save money")
  }
}

object UseTrait extends App {
  val clog = new ConcreteLogger
  clog.concreteLog
  clog.a("aaaa")
  val account = new UAccount
  account.save
  val account1 = new UAccount with FConcreteLogger
  account1.save
}