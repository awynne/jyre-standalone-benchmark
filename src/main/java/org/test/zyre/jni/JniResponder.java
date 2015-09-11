package org.test.zyre.jni;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

public class JniResponder extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(JniResponder.class);
	
	private Zyre zyre;
	private String group = "local";
	
	@Override
	public void run() {
		log.debug("responder thread starting");
		zyre = new Zyre();
		zyre.create();
		zyre.join(group);
		
		while(!Thread.currentThread().isInterrupted()) {
			String data = zyre.recv();
		
			if (data == null) {// Interrupted
				log.error("Interrupted during recv()");
				break;
			}
						
			HashMap<String,String> zmsg = Utils.jsonZmsgToMap(data);
			
			// Respond to a shout with a whisper to sender
			if (zmsg.get("event").equals("SHOUT")) {

				String requesterId = zmsg.get("peer");
				String payload = zmsg.get("message");
				String response = "response-to-" + payload;
				
				zyre.whisper(requesterId, response);
			} 
			else {
				//not handling other events
			}
		}
	}

	public void destroyZyre() {
		zyre.destroy();
	}
}
