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
