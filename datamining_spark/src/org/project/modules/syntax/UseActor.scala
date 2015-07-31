package org.project.modules.syntax

import scala.actors.Actor

object ActorOne extends Actor {
	
	def act() {
		for (i <- 1 to 10) {
			println("actor one")
		}
	}
}

object ActorTwo extends Actor {
  
	def act() {
		for (i <- 1 to 10) {
			println("actor two")
		}
	}
}

case class MessageA(name : String, age : Int)

class MessageActor extends Actor {
	
	def act() {
		while (true) {
			receive {
				case MessageA(name, age) => {
					println("receive message name : " + name + " - age :" + age)
					sender ! "already receive message success"
				}
				case message : String => {
					println("receive string " + message)
				}
				case _ => println("not parse receive message ")
			}
		}
//		react {
//			case MessageA(name, age) => {
//				println("receive message name : " + name + " - age :" + age)
//				sender ! "already receive message success"
//				act
//			}
//			case message : String => {
//				println("receive string " + message)
//				act
//			}
//			case _ => println("not parse receive message ")
//		}
	}
}

class MessageActor1 extends Actor {
	
	def act() {
		loop {
			react {
				case MessageA(name, age) => println("name : " + name + " - age :" + age)
			}
		}
	}
}


object UseActor {

	def main(args:Array[String]) {
		ActorOne.start
		ActorTwo.start
		
		val messageActor = new MessageActor
		messageActor.start
		
		messageActor ! MessageA("zhangsan", 20)
		messageActor ! "Spark"
		messageActor ! Math.PI
		
//		println(self.receive{case msg => msg})
//		println(self.receiveWithin(1000){case msg => msg})
		
	}
	
  
}