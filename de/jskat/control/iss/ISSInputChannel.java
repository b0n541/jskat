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
public class ISSInputChannel extends Thread {

	private Log log = LogFactory.getLog(ISSInputChannel.class);
	
	private ISSConnector connect;
	private final static int protocolVersion = 14;

    Object lock = new Object();
    InputStream stream;
    BufferedReader reader;
    boolean done = false;
    
    /**
     * Constructor
     * 
     * @param is Input stream
     */
    public ISSInputChannel(ISSConnector conn, InputStream is) {

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

	private void handleMessage(String message) {
		
		log.debug("ISS: " + message);
		
		StringTokenizer token = new StringTokenizer(message);
		String first = token.nextToken();
		
		if (first.equals("password:")) {
			
			handlePasswordMessage(token);
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
			
			handleTableUpdateMessage(token);
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
			
			log.error("UNHANDLED MESSAGE: " + message);
		}
	}
	
	private void handlePasswordMessage(StringTokenizer token) {
		
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

	private void handleTableUpdateMessage(StringTokenizer token) {
		// TODO Auto-generated method stub
		
	}

	private void handleTableListMessage(StringTokenizer token) {

		String plusMinus = token.nextToken();
		
		if (plusMinus.equals("+")) {
			
			addOrUpdateTableList(token);
		}
		else if (plusMinus.equals("-")) {
			
			removeTableFromList(token);
		}
	}

	private void addOrUpdateTableList(StringTokenizer token) {
		
		log.debug("addOrUpdateTableList()");
	}
	
	private void removeTableFromList(StringTokenizer token) {
		
		log.debug("removeTableFromList();");
	}
	
	private void handleClientListMessage(StringTokenizer token) {
		
		String plusMinus = token.nextToken();
		
		if (plusMinus.equals("+")) {
			
			addOrUpdateClientList(token);
		}
		else if (plusMinus.equals("-")) {
			
			removeClientFromList(token);
		}
	}

	private void addOrUpdateClientList(StringTokenizer token) {
		
		log.debug("addOrUpdateClientList()");
	}
	
	private void removeClientFromList(StringTokenizer token) {
		
		log.debug("removeClientFromList()");
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
			log.error("Wrong protocol version!!!");
		}
	}

    class ReaderClass extends Thread {

        @Override
		public void run() {

        	String line;
            while (!ISSInputChannel.this.done) {
                try {
                	line = ISSInputChannel.this.reader.readLine();
                	handleMessage(line);
                } catch (IOException ioe) {
                    ISSInputChannel.this.done = true;
                }
            }

            synchronized(ISSInputChannel.this.lock) {
                ISSInputChannel.this.lock.notify( );
            }
        }
    }
}