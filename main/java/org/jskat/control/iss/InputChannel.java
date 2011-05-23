/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0
 * Build date: 2011-05-23 18:13:47
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.jskat.control.iss;

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
	InputChannel(IssController controller, Connector conn, InputStream is) {

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

					log.debug("IO exception --> lost connection to ISS"); //$NON-NLS-1$
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

					log.debug("InputChannel interrupted"); //$NON-NLS-1$

					rc.interrupt();
					this.done = true;

					try {

						log.debug("Closing stream"); //$NON-NLS-1$

						stream.close();

					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		}
	}
}