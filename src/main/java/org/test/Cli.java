package org.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.zyre.jeromq.ZyreRequester;
import org.test.zyre.jeromq.ZyreResponder;
import org.test.zyre.jni.JniRequester;
import org.test.zyre.jni.JniResponder;

public class Cli {
	
	private static final Logger log = LoggerFactory.getLogger(Cli.class);

	/**
	 * Uses commons-cli to parse the command line args
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine line = parser.parse(options, args);
		
			int numResponders = Integer.parseInt( (String) line.getParsedOptionValue("numResponders") );
			int numMsgs = Integer.parseInt( (String) line.getParsedOptionValue("numMsgs") );
			int intervalMs = Integer.parseInt( (String) line.getParsedOptionValue("interval") );
			String impl = (String) line.getParsedOptionValue("zyreImpl");
			log.info("numResponders: " + numResponders + " numMsgs: " + numMsgs + " interval: " + intervalMs + " zyre impl: " + impl);
			
			if (impl.equals("java")) {
				runJava(numResponders, numMsgs, intervalMs);
			}
			else if (impl.equals("jni")) {
				runJni(numResponders, numMsgs, intervalMs);
			}
			else {
				log.error("unknown implementation: " + impl);
			}
				
		}
		catch (ParseException e) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "jyre-standalone-benchmark", options );
		}
		
	}
	
	private static void runJni(int numResponders, int numMsgs, int intervalMs) throws Exception {
		List<JniResponder> responders = new ArrayList<JniResponder>();

		for (int i=0; i < numResponders; i++) {
			JniResponder responder = new JniResponder();
			responder.start();
			responders.add(responder);
			Thread.sleep(20);
		}
		
		JniRequester requester = new JniRequester(intervalMs, numMsgs, numResponders);
		requester.start();
		requester.join();
		
		for (JniResponder resp : responders) {
			resp.interrupt();
			Thread.sleep(20);
			resp.destroyZyre();
		}
	}
	
	private static void runJava(int numResponders, int numMsgs, int intervalMs) {
		for (int i=0; i < numResponders; i++) {
			ZyreResponder responder = new ZyreResponder();
			new Thread(responder).start();
		}
		
		ZyreRequester requester = new ZyreRequester(intervalMs, numMsgs, numResponders);
		requester.start();
	}
	
	private static Options createOptions() {
		Option responders = Option.builder("r")
				.required(true)
				.longOpt("numResponders")
				.hasArg()
				.desc("number of responder threads to start")
				.build();

		Option msgs = Option.builder("m")
				.required(true)
				.longOpt("numMsgs")
				.hasArg()
				.desc("number of messages to send")
				.build();

		Option interval = Option.builder("i")
				.required(true)
				.longOpt("interval")
				.hasArg()
				.desc("ms to wait between sends")
				.build();
		
		Option zyreImpl = Option.builder("z")
				.required(true)
				.longOpt("zyreImpl")
				.hasArg()
				.desc("implementation of zyre: java or jni")
				.build();

		Options options = new Options();
		options.addOption(responders).addOption(msgs).addOption(interval).addOption(zyreImpl);

		return options;

	}
}
