/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles all incoming messages from ISS
 */
class ISSInputThread extends Thread {

	private Log log = LogFactory.getLog(ISSInputThread.class);
	
	private ISSConnector connect;
	private BufferedReader input;
	private final int protocolVersion = 14;

	/**
	 * Constructor
	 * 
	 * @param newInput Input stream from ISS
	 */
	ISSInputThread(ISSConnector newConnect, BufferedReader newInput) {
		
		this.connect = newConnect;
		this.input = newInput;
	}
	
	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		
		String issInput;
		
		try {
			while ((issInput = this.input.readLine()) != null) {
				// handle messages
				handleMessage(issInput);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleMessage(String message) {
		
		log.debug("ISS: " + message);
		
		StringTokenizer token = new StringTokenizer(message);
		String first = token.nextToken();
		
		if (first.equals("password:")) {
			
			handlePasswordMassage(token);
		}
		else if (first.equals("Welcome")) {
			
			handleWelcomeMessage(token);
		}
		else if (first.equals("clients")) {
			
			handleClientListMessage(token);
		}
		else if (first.equals("tables")) {
			
			handleTableListMessage(token);
		}
		else if (first.equals("create")) {
			
			handleTableCreationMessage(token);
		}
		else if (first.equals("table")) {
			
			handleTableMessage(token);
		}
		else if (first.equals("error")) {
			
			handleErrorMessage(token);
		}
		else if (first.equals("text")) {
			
			handleTextMessage(token);
		}
		else {
			
			System.err.println("UNHANDLED MESSAGE: " + message);
		}
	}
	
	private void handlePasswordMassage(StringTokenizer token) {
		
		this.connect.sendPassword();
	}

	private void handleTextMessage(StringTokenizer token) {
		
		StringBuffer textMessage = new StringBuffer();
		
		while(token.hasMoreTokens()) {
			
			textMessage.append(token.nextToken()).append(' ');
		}
		
		System.err.println(textMessage);
	}

	private void handleErrorMessage(StringTokenizer token) {
		
		StringBuffer errorMessage = new StringBuffer();
		
		while(token.hasMoreTokens()) {
			
			errorMessage.append(token.nextToken()).append(' ');
		}
		
		System.err.println(errorMessage);
	}

	private void handleTableMessage(StringTokenizer token) {
		// TODO Auto-generated method stub
		
	}

	private void handleTableCreationMessage(StringTokenizer token) {
		// TODO Auto-generated method stub
		
	}

	private void handleTableListMessage(StringTokenizer token) {
		// TODO Auto-generated method stub
		
	}

	private void handleClientListMessage(StringTokenizer token) {
		
		String plusMinus = token.nextToken();
		
		if (plusMinus.equals("+")) {
			
			log.debug("addClientToList();");
		}
		else if (plusMinus.equals("-")) {
			
			log.debug("removeClientFromList();");
		}
	}
	
	private void handleWelcomeMessage(StringTokenizer token) {
		
		while(!token.nextToken().equals("version")) {
			// search for token "version"
		}
		
		double issProtocolVersion = Double.parseDouble(token.nextToken());
		
		log.debug("iss version: " + issProtocolVersion);
		log.debug("local version: " + this.protocolVersion);
		
		if ((int)issProtocolVersion != this.protocolVersion) {
			// TODO handle this in JSkatMaster
			System.err.println("Wrong protocol version!!!");
		}
	}
}
