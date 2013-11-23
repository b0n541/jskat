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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.Option;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StreamConnector to International Skat Server ISS
 */
class StreamConnector extends AbstractIssConnector {

	private static Logger log = LoggerFactory.getLogger(StreamConnector.class);

	private static JSkatResourceBundle strings = JSkatResourceBundle.instance();
	private static JSkatOptions options = JSkatOptions.instance();

	private Socket socket;
	private PrintWriter output;
	private InputChannel issIn;
	private StreamOutputChannel issOut;

	/**
	 * Establishes a connection with ISS
	 * 
	 * @return TRUE if the connection was successful
	 */
	@Override
	public boolean establishConnection(final IssController issControl) {

		log.debug("StreamConnector.establishConnection()"); //$NON-NLS-1$

		try {
			socket = new Socket(options.getString(Option.ISS_ADDRESS),
					options.getInteger(Option.ISS_PORT));

			output = new PrintWriter(socket.getOutputStream(), true);
			issOut = new StreamOutputChannel(output);
			issIn = new InputChannel(issControl, this, socket.getInputStream());
			issIn.start();
			log.debug("Connection established..."); //$NON-NLS-1$

		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open connection to ISS"); //$NON-NLS-1$
			issControl.showErrorMessage(strings
					.getString("cant_connect_to_iss")); //$NON-NLS-1$
			return false;
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString()); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	@Override
	public OutputChannel getOutputChannel() {
		return issOut;
	}

	/**
	 * Closes the connection to ISS
	 */
	@Override
	public void closeConnection() {

		try {
			log.debug("closing connection"); //$NON-NLS-1$
			issIn.interrupt();
			log.debug("input channel closed"); //$NON-NLS-1$
			output.close();
			log.debug("output channel closed"); //$NON-NLS-1$
			socket.close();
			log.debug("socket closed"); //$NON-NLS-1$
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.debug("ISS connector IOException"); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether there is an open connection
	 * 
	 * @return TRUE if there is an open connection
	 */
	@Override
	public boolean isConnected() {

		return socket != null && !socket.isClosed();
	}
}
