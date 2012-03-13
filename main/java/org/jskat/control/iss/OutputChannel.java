/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.control.iss;

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
