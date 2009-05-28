/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.iss.ISSChatMessage;

/**
 * Connector to Internation Skat Server ISS
 */
class Connector {

	private static Log log = LogFactory.getLog(Connector.class);

	private Socket socket;
	private PrintWriter output;
	private InputChannel issIn;
	private OutputChannel issOut;
	
	private String loginName;
	private String password;
	private int port;

	private ISSController issControl;

	/**
	 * Constructor
	 * 
	 * @param controller Controller for ISS connection
	 */
	Connector(ISSController controller) {
		
		this.issControl = controller;
	}
	
	/**
	 * Sets login credentials
	 * 
	 * @param newLoginName Login name
	 * @param newPassword Password
	 * @param newPort Port number (80, 7000, 8000 are allowed)
	 */
	void setConnectionData(String newLoginName, String newPassword, int newPort) {
		
		this.loginName = newLoginName;
		this.password = newPassword;
		
		if (newPort == 80 || newPort == 7000 || newPort == 8000) {
			
			this.port = newPort;
		}
		else {
			
			throw new IllegalArgumentException("Unsupported port number: " + newPort); //$NON-NLS-1$
		}
	}
	
	/**
	 * Establishes a connection with ISS
	 * 
	 * @return TRUE if the connection was successful
	 */
	boolean establishConnection() {

		log.debug("ISSConnector.establishConnection()"); //$NON-NLS-1$
		
		try {
			this.socket = new Socket("bodo1.cs.ualberta.ca", this.port); //$NON-NLS-1$
			this.output = new PrintWriter(this.socket.getOutputStream(), true);
			this.issOut = new OutputChannel(this.output);
			this.issIn = new InputChannel(this.issControl, this, this.socket.getInputStream());
			this.issIn.start();
			
		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open connection to ISS"); //$NON-NLS-1$
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString()); //$NON-NLS-1$
		}
		
		log.debug("Connection established..."); //$NON-NLS-1$
		
		this.issOut.send(this.loginName);
		
		return true;
	}

	void sendPassword() {
		
		this.issOut.send(this.password);
	}
	
	/**
	 * Closes the connection to ISS
	 */
	void closeConnection() {
		
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
	boolean isConnected() {
		
		return this.socket != null && this.socket.isBound();
	}

	void send(ISSChatMessage message) {
	
		this.issOut.send("yell " + message.getMessage()); //$NON-NLS-1$
	}
}
