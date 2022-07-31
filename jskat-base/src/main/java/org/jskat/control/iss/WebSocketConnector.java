//package org.jskat.control.iss;
//
//import java.net.URI;
//import java.util.concurrent.TimeUnit;
//
//import org.eclipse.jetty.websocket.WebSocket.Connection;
//import org.eclipse.jetty.websocket.WebSocketClient;
//import org.eclipse.jetty.websocket.WebSocketClientFactory;
//import org.jskat.data.JSkatOptions.Option;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * StreamConnector to International Skat Server ISS
// */
//class WebSocketConnector extends AbstractIssConnector {
//
//	private static Logger log = LoggerFactory
//			.getLogger(WebSocketConnector.class);
//
//	private WebSocketConnection webSocket;
//
//	/**
//	 * Establishes a connection with ISS
//	 *
//	 * @return TRUE if the connection was successful
//	 */
//	@Override
//	public boolean establishConnection(final IssController issControl) {
//
//		log.debug("WebSocketConnector.establishConnection()"); 
//
//		try {
//			WebSocketClientFactory factory = new WebSocketClientFactory();
//			factory.start();
//
//			WebSocketClient client = factory.newWebSocketClient();
//
//			client.setMaxIdleTime(30 * 60 * 1000); // 30 minutes
//			client.setMaxTextMessageSize(4096);
//			client.setProtocol("chat");
//
//			webSocket = new WebSocketConnection(issControl);
//
//			Connection connection = client
//					.open(new URI(
//							"ws://"	+ options.getString(Option.ISS_ADDRESS) + ":"  
//									+ options.getInteger(Option.ISS_PORT)),
//							webSocket, 10, TimeUnit.SECONDS);
//
//			if (connection.isOpen()) {
//				return true;
//			}
//
//		} catch (java.net.UnknownHostException e) {
//			log.error("Cannot open connection to ISS"); 
//			issControl.showErrorMessage(strings
//					.getString("cant_connect_to_iss")); 
//			return false;
//		} catch (java.io.IOException e) {
//			log.error("IOException: " + e.toString()); 
//			return false;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	@Override
//	public OutputChannel getOutputChannel() {
//		return webSocket;
//	}
//
//	/**
//	 * Closes the connection to ISS
//	 */
//	@Override
//	public void closeConnection() {
//		webSocket.messageHandler.interrupt();
//		webSocket.connection.close();
//		log.debug("connection closed"); 
//	}
//
//	/**
//	 * Checks whether there is an open connection
//	 *
//	 * @return TRUE if there is an open connection
//	 */
//	@Override
//	public boolean isConnected() {
//		return webSocket != null && webSocket.connection != null
//				&& webSocket.connection.isOpen();
//	}
//}
