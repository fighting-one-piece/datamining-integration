package org.project.modules.akka.simple;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Bootstrap {

	public static void main(String[] args) {
		ActorSystem actorSystem = ActorSystem.create("CaculateSystem");
		final ActorRef master = actorSystem.actorOf(Props.create(Master.class), "Master");
		actorSystem.scheduler().schedule(
				Duration.create(1, TimeUnit.SECONDS), 
				Duration.create(1, TimeUnit.SECONDS), 
				new Runnable() {
					@Override
					public void run() {
						master.tell(new Operation(1, 2, OperationSymbol.ADD), null);
						master.tell(new Operation(3, 2, OperationSymbol.SUB), null);
						master.tell(new Operation(2, 2, OperationSymbol.MUL), null);
						master.tell(new Operation(8, 2, OperationSymbol.DEV), null);
					}
				}, 
				actorSystem.dispatcher());
	}
}
