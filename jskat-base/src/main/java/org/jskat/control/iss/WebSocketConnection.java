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

import org.eclipse.jetty.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WebSocketConnection implements WebSocket.OnTextMessage, OutputChannel {

	private static Logger log = LoggerFactory
			.getLogger(WebSocketConnection.class);

	Connection connection;
	MessageHandler messageHandler;

	public WebSocketConnection(final IssController controller) {
		messageHandler = new MessageHandler(controller);
		messageHandler.start();
	}

	@Override
	public void onOpen(final Connection connection) {
		// open notification
		log.debug("[ connection established ]");
		this.connection = connection;
	}

	@Override
	public void onClose(final int closeCode, final String message) {
		// close notification
		log.debug("[ connection closed ]");
	}

	@Override
	public void onMessage(final String message) {
		log.debug("ISS    |---> " + message); //$NON-NLS-1$
		messageHandler.addMessage(message);
	}

	@Override
	public void sendMessage(final String message) {
		try {
			log.debug("ISS <--|    " + message); //$NON-NLS-1$
			connection.sendMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
