package org.project.modules.akka.remote;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

public class Client {

	public static void main(String[] args) {
		ActorSystem actorSystem = ActorSystem.create("rs", ConfigFactory.load().getConfig("client"));
		actorSystem.actorSelection("akka://rs@127.0.0.1:9999/user/server").tell("hello", null);;
	}
}
