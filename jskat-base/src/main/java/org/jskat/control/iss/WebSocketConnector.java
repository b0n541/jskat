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

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.jskat.data.JSkatOptions.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StreamConnector to International Skat Server ISS
 */
class WebSocketConnector extends AbstractIssConnector {

	private static Logger log = LoggerFactory
			.getLogger(WebSocketConnector.class);

	private WebSocketConnection webSocket;

	/**
	 * Establishes a connection with ISS
	 * 
	 * @return TRUE if the connection was successful
	 */
	@Override
	public boolean establishConnection(final IssController issControl) {

		log.debug("WebSocketConnector.establishConnection()"); //$NON-NLS-1$

		try {
			WebSocketClientFactory factory = new WebSocketClientFactory();
			factory.start();

			WebSocketClient client = factory.newWebSocketClient();

			client.setMaxIdleTime(30 * 60 * 1000); // 30 minutes
			client.setMaxTextMessageSize(4096);
			client.setProtocol("chat");

			webSocket = new WebSocketConnection(issControl);

			Connection connection = client
					.open(new URI(
							"ws://"	+ options.getString(Option.ISS_ADDRESS) + ":" //$NON-NLS-1$ //$NON-NLS-2$
									+ options.getInteger(Option.ISS_PORT)),
							webSocket, 10, TimeUnit.SECONDS);

			if (connection.isOpen()) {
				return true;
			}

		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open connection to ISS"); //$NON-NLS-1$
			issControl.showErrorMessage(strings
					.getString("cant_connect_to_iss")); //$NON-NLS-1$
			return false;
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString()); //$NON-NLS-1$
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public OutputChannel getOutputChannel() {
		return webSocket;
	}

	/**
	 * Closes the connection to ISS
	 */
	@Override
	public void closeConnection() {
		webSocket.messageHandler.interrupt();
		webSocket.connection.close();
		log.debug("connection closed"); //$NON-NLS-1$
	}

	/**
	 * Checks whether there is an open connection
	 * 
	 * @return TRUE if there is an open connection
	 */
	@Override
	public boolean isConnected() {
		return webSocket != null && webSocket.connection != null
				&& webSocket.connection.isOpen();
	}
}
