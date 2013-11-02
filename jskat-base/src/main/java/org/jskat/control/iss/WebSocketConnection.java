/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
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
		// handle incoming message
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
