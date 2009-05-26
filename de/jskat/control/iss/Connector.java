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

import de.jskat.control.JSkatMaster;

/**
 * Connector to Internation Skat Server ISS
 */
public class Connector {

	private static Log log = LogFactory.getLog(Connector.class);

	private Socket socket;
	private PrintWriter output;
	private InputChannel issIn;
	private OutputChannel issOut;
	
	private String loginName;
	private String password;
	private int port;

	private ISSController issControl;

	public Connector(ISSController controller) {
		
		this.issControl = controller;
	}
	
	public void setConnectionData(String newLoginName, String newPassword, int newPort) {
		
		this.loginName = newLoginName;
		this.password = newPassword;
		
		if (newPort == 80 || newPort == 7000 || newPort == 8000) {
			
			this.port = newPort;
		}
		else {
			
			throw new IllegalArgumentException("Unsupported port number: " + newPort);
		}
	}
	
	/**
	 * Establishes a connection with ISS
	 * 
	 * @param port (e.g. 80, 7000, 8000)
	 * @return TRUE if the connection was successful
	 */
	public boolean establishConnection() {

		log.debug("ISSConnector.establishConnection()");
		
		try {
			this.socket = new Socket("bodo1.cs.ualberta.ca", port);
			this.output = new PrintWriter(this.socket.getOutputStream(), true);
			this.issOut = new OutputChannel(this.output);
			this.issIn = new InputChannel(this.issControl, this, this.socket.getInputStream());
			this.issIn.start();
			
		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open connection to ISS");
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString());
		}
		
		log.debug("Connection established...");
		
		this.issOut.send(this.loginName);
		
		return true;
	}

	void sendPassword() {
		
		this.issOut.send(this.password);
	}
	
	/**
	 * Closes the connection to ISS
	 */
	public void closeConnection() {
		
		try {
			log.debug("closing connection");
			this.issIn.interrupt();
			log.debug("input channel closed");
			this.output.close();
			log.debug("output channel closed");
			this.socket.close();
			log.debug("socket closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.debug("ISS connector IOException");
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether there is an open connection
	 * 
	 * @return TRUE if there is an open connection
	 */
	public boolean isConnected() {
		
		return this.socket != null && this.socket.isBound();
	}
}
