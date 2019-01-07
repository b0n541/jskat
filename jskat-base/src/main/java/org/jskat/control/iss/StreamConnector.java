/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

	private static JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;
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
			this.socket = new Socket(options.getString(Option.ISS_ADDRESS),
					options.getInteger(Option.ISS_PORT));

			this.output = new PrintWriter(this.socket.getOutputStream(), true);
			this.issOut = new StreamOutputChannel(this.output);
			this.issIn = new InputChannel(issControl, this,
					this.socket.getInputStream());
			this.issIn.start();
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
		return this.issOut;
	}

	/**
	 * Closes the connection to ISS
	 */
	@Override
	public void closeConnection() {

		try {
			log.debug("closing connection"); //$NON-NLS-1$
			this.issIn.interrupt();
			log.debug("input channel closed"); //$NON-NLS-1$
			this.output.close();
			log.debug("output channel closed"); //$NON-NLS-1$
			this.socket.close();
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

		return this.socket != null && !this.socket.isClosed();
	}
}
