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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reads data from ISS until an interrupt signal occures
 * 
 * Idea is taken from the book Java Threads by Scott Oaks and Henry Wong
 */
class InputChannel extends Thread {

	static Log log = LogFactory.getLog(InputChannel.class);

	MessageHandler messageHandler;

	Object lock = new Object();
	InputStream stream;
	BufferedReader reader;
	boolean done = false;

	/**
	 * Constructor
	 * 
	 * @param controller
	 * @param conn
	 * @param is
	 *            Input stream
	 */
	InputChannel(ISSController controller, Connector conn, InputStream is) {

		this.stream = is;
		this.reader = new BufferedReader(new InputStreamReader(this.stream));
		this.messageHandler = new MessageHandler(conn, controller);
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
					InputChannel.this.messageHandler.handleMessage(line);
				} catch (IOException ioe) {
					log.debug("IO exception", ioe); //$NON-NLS-1$
					InputChannel.this.done = true;
				}
			}

			synchronized (InputChannel.this.lock) {
				InputChannel.this.lock.notify();
			}
		}
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		ReaderClass rc = new ReaderClass();

		synchronized (this.lock) {

			rc.start();

			while (!this.done) {
				try {
					this.lock.wait();
				} catch (InterruptedException ie) {
					this.done = true;
					rc.interrupt();
					try {
						this.stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}