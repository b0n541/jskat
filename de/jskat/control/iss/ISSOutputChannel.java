/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles all incoming messages from ISS
 */
class ISSOutputChannel {

	private static Log log = LogFactory.getLog(ISSOutputChannel.class);
	
	/**
	 * Constructor
	 * 
	 * @param newOutput Input stream from ISS
	 */
	ISSOutputChannel(PrintWriter newOutput) {
		
		this.output = newOutput;
	}
	
	/**
	 * Sends a message to ISS
	 * 
	 * @param message Message text
	 */
	void send(String message) {
		
		log.debug(message + " --> ISS ");
		this.output.println(message);
	}
	
	private PrintWriter output;
}
