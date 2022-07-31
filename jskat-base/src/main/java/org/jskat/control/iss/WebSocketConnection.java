//package org.jskat.control.iss;
//
//import java.io.IOException;
//
//import org.eclipse.jetty.websocket.WebSocket;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//class WebSocketConnection implements WebSocket.OnTextMessage, OutputChannel {
//
//	private static Logger log = LoggerFactory
//			.getLogger(WebSocketConnection.class);
//
//	Connection connection;
//	MessageHandler messageHandler;
//
//	public WebSocketConnection(final IssController controller) {
//		messageHandler = new MessageHandler(controller);
//		messageHandler.start();
//	}
//
//	@Override
//	public void onOpen(final Connection connection) {
//		// open notification
//		log.debug("[ connection established ]");
//		this.connection = connection;
//	}
//
//	@Override
//	public void onClose(final int closeCode, final String message) {
//		// close notification
//		log.debug("[ connection closed ]");
//	}
//
//	@Override
//	public void onMessage(final String message) {
//		log.debug("ISS    |---> " + message); 
//		messageHandler.addMessage(message);
//	}
//
//	@Override
//	public void sendMessage(final String message) {
//		try {
//			log.debug("ISS <--|    " + message); 
//			connection.sendMessage(message);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
