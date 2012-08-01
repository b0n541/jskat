package org.jskat.control.iss;

/**
 * Handles all outgoing messages to ISS
 */
interface OutputChannel {
	/**
	 * Sends a message to ISS
	 * 
	 * @param message
	 *            Message text
	 */
	void sendMessage(final String message);
}
