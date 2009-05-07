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

	/**
	 * Logs into ISS
	 * 
	 * @param login Login name
	 * @param password Password
	 * @param port (e.g. 80, 7000, 8000)
	 * @return TRUE if the connection was successful
	 */
	public boolean login(String login, String password, int port) {

		log.debug("Login");
		
		try {
			this.socket = new Socket("bodo1.cs.ualberta.ca", port);
			this.output = new PrintWriter(this.socket.getOutputStream(), true);
			this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (java.net.UnknownHostException e) {
			log.error("Cannot open localhost");
		} catch (java.io.IOException e) {
			log.error("IOException: " + e.toString());
		}
		
		log.debug("Connection established...");
		
		this.issIn = new ISSInputThread(this.input);
		this.issIn.start();
		this.issOut = new ISSOutputChannel(this.output);
		
		log.debug("Sending credentials...");
		
		this.issOut.send(login);
		this.issOut.send(password);
		
		return true;
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
			this.input.close();
			this.output.close();
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private ISSInputThread issIn;
	private ISSOutputChannel issOut;
}
