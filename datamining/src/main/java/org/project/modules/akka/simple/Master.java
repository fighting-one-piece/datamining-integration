package org.project.modules.akka.simple;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Operation) {
			ActorRef worker = getContext().actorOf(Props.create(Worker.class));
			worker.tell(message, getSelf());
		} else if (message instanceof OperationResult) {
			OperationResult operationResult = (OperationResult) message;
			System.out.println(operationResult.toString());
			getContext().stop(getSender());
		} else {
			unhandled(message);
		}
	}

}
