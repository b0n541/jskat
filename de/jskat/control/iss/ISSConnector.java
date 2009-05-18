/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Connector to Internation Skat Server ISS
 */
public class ISSConnector {

	private static Log log = LogFactory.getLog(ISSConnector.class);

	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private ISSInputThread issIn;
	private ISSOutputChannel issOut;
	
	private String loginName;
	private String password;
	private int port;

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
			this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open connection to ISS");
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString());
		}
		
		log.debug("Connection established...");
		
		this.issIn = new ISSInputThread(this, this.input);
		this.issIn.start();
		this.issOut = new ISSOutputChannel(this.output);
		
		this.issOut.send(this.loginName);
		
		return true;
	}

	void sendPassword() {
		
		this.issOut.send(this.password);
	}
	
	private void startTable() {
		
//		this.issOut.send("create / 3");
//		this.issOut.send("join .1 ");
//		this.issOut.send("table .3 foo invite xskat bernie");
//		this.issOut.send("table .3 foo ready");
	}

	/**
	 * Closes the connection to ISS
	 */
	public void closeConnection() {
		
		try {
			log.debug("closing connection");
//			this.issIn.interrupt();
//			log.debug("iss thread interrupted");
			this.input.close();
//			log.debug("input reader closed");
			this.output.close();
			log.debug("output writer closed");
			this.socket.close();
			log.debug("socket closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
