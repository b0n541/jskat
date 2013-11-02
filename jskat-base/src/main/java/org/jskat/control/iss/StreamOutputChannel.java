/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
 * Copyright (C) 2013-11-02
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
