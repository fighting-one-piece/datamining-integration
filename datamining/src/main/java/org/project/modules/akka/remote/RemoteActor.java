package org.project.modules.akka.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class RemoteActor extends UntypedActor {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(RemoteActor.class);

	@Override
	public void onReceive(Object message) throws Exception {
		LOG.info("message : {} self : {} sender : {} ", message, getSelf(), getSender());
		getSender().tell(message, null);
	}

}
