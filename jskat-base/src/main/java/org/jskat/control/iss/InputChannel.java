/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control.iss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads data from ISS until an interrupt signal occures
 * 
 * Idea is taken from the book Java Threads by Scott Oaks and Henry Wong
 */
class InputChannel extends Thread {

	private static Logger log = LoggerFactory.getLogger(InputChannel.class);

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
	InputChannel(final IssController controller,
			final StreamConnector conn, final InputStream is) {

		this.stream = is;
		this.reader = new BufferedReader(new InputStreamReader(this.stream));
		this.messageHandler = new MessageHandler(conn, controller);
		messageHandler.start();
	}

	/**
	 * Helper class for reading incoming information
	 */
	class ReaderClass extends Thread {

		@Override
		public void run() {

			log.debug("... listening to ISS");
			String line;
			while (!InputChannel.this.done) {
				try {

					line = InputChannel.this.reader.readLine();
					
					log.debug("ISS    |--> " + line); //$NON-NLS-1$
					
					InputChannel.this.messageHandler.addMessage(line);

					if (line == null) {
						InputChannel.this.done = true;
					}

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