package org.project.modules.akka.remote;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.kernel.Bootable;
import akka.remote.RemoteScope;

import com.typesafe.config.ConfigFactory;

public class RemoteServer implements Bootable {
	
	private ActorSystem actorSystem = null;
	
	private ActorRef actorRef = null;
	
	public RemoteServer() {
		actorSystem = ActorSystem.create("rs", ConfigFactory.load().getConfig("server"));
//		actorRef = actorSystem.actorOf(Props.create(RemoteActor.class), "remoteActor");
		Address address = new Address("akka", "rs", "127.0.0.1", 9999);
		actorRef = actorSystem.actorOf(new Props(new Deploy(
				new RemoteScope(address)), RemoteActor.class, null), "remoteActor");
	}

	@Override
	public void startup() {
	}

	@Override
	public void shutdown() {
		actorSystem.shutdown();
	}
	
	public static void main(String[] args) {
		RemoteServer remoteServer = new RemoteServer();
	}

}
