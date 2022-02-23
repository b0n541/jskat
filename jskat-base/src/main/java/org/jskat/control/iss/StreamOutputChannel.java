
package org.jskat.control.iss;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StreamOutputChannel implements OutputChannel {

	private static Logger log = LoggerFactory
			.getLogger(StreamOutputChannel.class);

	private final PrintWriter output;

	/**
	 * Constructor
	 * 
	 * @param newOutput
	 *            Input stream from ISS
	 */
	StreamOutputChannel(final PrintWriter newOutput) {

		this.output = newOutput;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendMessage(final String message) {
		log.debug("ISS <--|    " + message); //$NON-NLS-1$
		this.output.println(message);
	}
}
