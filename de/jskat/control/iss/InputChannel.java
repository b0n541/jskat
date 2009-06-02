/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reads data from ISS until an interrupt signal occures
 * 
 * Idea was taken from the book Java Threads by Scott Oaks and Henry Wong
 */
class InputChannel extends Thread {

	private static Log log = LogFactory.getLog(InputChannel.class);
	
	private ISSController issControl;
	
	private Connector connect;
	private final static int protocolVersion = 14;

    Object lock = new Object();
    InputStream stream;
    BufferedReader reader;
    boolean done = false;
    
    /**
     * Constructor
     * 
     * @param controller 
     * @param conn 
     * @param is Input stream
     */
    InputChannel(ISSController controller, Connector conn, InputStream is) {

    	this.issControl = controller;
    	this.connect = conn;
    	this.stream = is;
        this.reader = new BufferedReader(new InputStreamReader(this.stream));
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {

        ReaderClass rc = new ReaderClass( );

        synchronized(this.lock) {

            rc.start( );

            while (!this.done) {
                try {
                    this.lock.wait( );
                } catch (InterruptedException ie) {
                    this.done = true;
                    rc.interrupt( );
                    try {
                        this.stream.close( );
                    } catch (IOException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }
    }

	void handleMessage(String message) {
		
		log.debug("ISS: " + message); //$NON-NLS-1$
		
		StringTokenizer token = new StringTokenizer(message);
		String first = token.nextToken();
		
		if (first.equals("password:")) { //$NON-NLS-1$
			
			handlePasswordMessage();
		}
		else if (first.equals("Welcome")) { //$NON-NLS-1$
			
			handleWelcomeMessage(token);
		}
		else if (first.equals("clients")) { //$NON-NLS-1$
			
			handleClientListMessage(token);
		}
		else if (first.equals("tables")) { //$NON-NLS-1$
			
			handleTableListMessage(token);
		}
		else if (first.equals("create")) { //$NON-NLS-1$
			
			handleTableCreateMessage(token);
		}
		else if (first.equals("table")) { //$NON-NLS-1$
			
			handleTableUpdateMessage(token);
		}
		else if (first.equals("error")) { //$NON-NLS-1$
			
			handleErrorMessage(token);
		}
		else if (first.equals("text")) { //$NON-NLS-1$
			
			handleTextMessage(token);
		}
		else if (first.equals("yell")) { //$NON-NLS-1$
			
			handleLobbyChatMessage(token);
		}
		else {
			
			log.error("UNHANDLED MESSAGE: " + message); //$NON-NLS-1$
		}
	}
	
	private void handleLobbyChatMessage(StringTokenizer token) {
		
		this.issControl.addChatMessage(ChatMessageType.LOBBY, token);
	}

	private void handlePasswordMessage() {
		
		this.connect.sendPassword();
	}

	private void handleTextMessage(StringTokenizer token) {
		
		StringBuffer textMessage = new StringBuffer();
		
		while(token.hasMoreTokens()) {
			
			textMessage.append(token.nextToken()).append(' ');
		}
		
		log.error(textMessage);
	}

	private void handleErrorMessage(StringTokenizer token) {
		
		StringBuffer errorMessage = new StringBuffer();
		
		while(token.hasMoreTokens()) {
			
			errorMessage.append(token.nextToken()).append(' ');
		}
		
		log.error(errorMessage);
	}

	private void handleTableMessage(StringTokenizer token) {
		// TODO Auto-generated method stub
		
	}

	private void handleTableCreateMessage(StringTokenizer token) {

		log.debug("table creation message");
		
		this.issControl.createTable(token.nextToken(), token.nextToken(), Integer.parseInt(token.nextToken()));
	}
	
	private void handleTableUpdateMessage(StringTokenizer token) {
		
		log.debug("table update message");
		
		// table .1 bar state 3 bar xskat xskat:2 . bar . 0 0 0 0 0 0 1 0 xskat $ 0 0 0 0 0 0 1 1 xskat:2 $ 0 0 0 0 0 0 1 1 . . 0 0 0 0 0 0 0 0 false
		
		String tableName = token.nextToken();
		String creator = token.nextToken();
		String actionCommand = token.nextToken();
		
		if (actionCommand.equals("state")) {
		
			this.issControl.updateISSTableState(tableName, token);
		}
		else {
			
			log.debug("unhandled action command: " + actionCommand + " for table " + tableName);
		}
	}

	/**
	 * Handles a table list message
	 * 
	 * @param Table information
	 */
	private void handleTableListMessage(StringTokenizer token) {

		String plusMinus = token.nextToken();
		
		if (plusMinus.equals("+")) { //$NON-NLS-1$
			
			updateTableList(token);
		}
		else if (plusMinus.equals("-")) { //$NON-NLS-1$
			
			removeTableFromList(token);
		}
	}

	/**
	 * Adds or updates a table on the table list
	 * 
	 * @param token Table information
	 */
	private void updateTableList(StringTokenizer token) {
		
		String tableName = token.nextToken();
		int maxPlayers = Integer.parseInt(token.nextToken());
		long gamesPlayed = Long.parseLong(token.nextToken());
		String player1 = token.nextToken();
		String player2 = token.nextToken();
		String player3 = token.nextToken();
		
		this.issControl.updateISSTableList(tableName, maxPlayers, gamesPlayed, player1, player2, player3);
	}
	
	/**
	 * Removes a table from the table list
	 * 
	 * @param token Table information
	 */
	private void removeTableFromList(StringTokenizer token) {
		
		this.issControl.removeISSTableFromList(token.nextToken());
	}
	
	/**
	 * Handles a client list message
	 * 
	 * @param token Client information
	 */
	private void handleClientListMessage(StringTokenizer token) {
		
		String plusMinus = token.nextToken();
		
		if (plusMinus.equals("+")) { //$NON-NLS-1$
			
			updatePlayerList(token);
		}
		else if (plusMinus.equals("-")) { //$NON-NLS-1$
			
			removeClientFromList(token);
		}
	}

	/**
	 * Adds or updates a player on the client list
	 * 
	 * @param token Player information
	 */
	private void updatePlayerList(StringTokenizer token) {
		
		String playerName = token.nextToken();
		// ignore next token
		token.nextToken();
		String language = token.nextToken();
		long gamesPlayed = Long.parseLong(token.nextToken());
		double strength = Double.parseDouble(token.nextToken());
		// ignore rest of the tokens
		
		this.issControl.updateISSPlayerList(playerName, language, gamesPlayed, strength);
	}
	
	/**
	 * Removes a player from the client list
	 * 
	 * @param token Player information
	 */
	private void removeClientFromList(StringTokenizer token) {
		
		this.issControl.removeISSPlayerFromList(token.nextToken());
	}
	
	/**
	 * Handles the welcome message and checks the protocol version
	 * 
	 * @param token Welcome information
	 */
	private void handleWelcomeMessage(StringTokenizer token) {
		
		while(!token.nextToken().equals("version")) {
			// search for token "version"
		}
		
		double issProtocolVersion = Double.parseDouble(token.nextToken());
		
		log.debug("iss version: " + issProtocolVersion); //$NON-NLS-1$
		log.debug("local version: " + InputChannel.protocolVersion); //$NON-NLS-1$
		
		if ((int)issProtocolVersion != InputChannel.protocolVersion) {
			// TODO handle this in JSkatMaster
			log.error("Wrong protocol version!!!"); //$NON-NLS-1$
		}
	}

	/**
	 * Helper class for reading incoming information
	 */
    class ReaderClass extends Thread {

        @Override
		public void run() {

        	String line;
            while (!InputChannel.this.done) {
                try {
                	line = InputChannel.this.reader.readLine();
                	handleMessage(line);
                } catch (IOException ioe) {
                    InputChannel.this.done = true;
                }
            }

            synchronized(InputChannel.this.lock) {
                InputChannel.this.lock.notify( );
            }
        }
    }
}