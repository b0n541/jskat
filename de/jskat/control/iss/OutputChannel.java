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
 * Handles all outgoing messages to ISS
 */
class OutputChannel {

	private static Log log = LogFactory.getLog(OutputChannel.class);

	/**
	 * Constructor
	 * 
	 * @param newOutput
	 *            Input stream from ISS
	 */
	OutputChannel(PrintWriter newOutput) {

		this.output = newOutput;
	}

	/**
	 * Sends a message to ISS
	 * 
	 * @param message
	 *            Message text
	 */
	void send(String message) {

		log.debug("ISS <--|    " + message); //$NON-NLS-1$
		this.output.println(message);
	}

	private PrintWriter output;
}
