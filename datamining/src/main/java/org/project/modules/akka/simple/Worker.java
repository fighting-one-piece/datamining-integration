package org.project.modules.akka.simple;

import akka.actor.UntypedActor;

public class Worker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Operation) {
			Operation operation = (Operation) message;
			int result = operation.execute();
			getSender().tell(new OperationResult(operation, result), getSelf());
		} else {
			unhandled(message);
		}
	}

}
