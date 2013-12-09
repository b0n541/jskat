/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the normal thread class with an wait option
 */
public class JSkatThread extends Thread {

	private static Logger log = LoggerFactory.getLogger(JSkatThread.class);

	private volatile boolean terminate = false;

	/**
	 * Terminates the thread
	 */
	public void terminate() {
		terminate = true;
		// A terminated thread must not wait
		pleaseWait = false;
		// Interrupts if the thread is waiting
		interrupt();
	}

	/**
	 * Checks whether the thread is terminated
	 * 
	 * @return TRUE, if the thread is terminated
	 */
	public boolean isTerminated() {
		return terminate;
	}

	/**
	 * Starts the waiting
	 */
	public void startWaiting() {
		// A terminated thread must not wait
		if (!terminate) {
			this.pleaseWait = true;
		}
	}

	/**
	 * Stops the waiting
	 */
	public void stopWaiting() {
		this.pleaseWait = false;
		notify();
	}

	/**
	 * Checks whether the waiting condition is set or not
	 */
	protected void checkWaitCondition() {
		// Check if should wait
		synchronized (this) {
			while (this.pleaseWait) {
				try {
					wait();
				} catch (Exception e) {
					// TODO handle exception accordingly
					log.error(e.toString());
				}
			}
		}
	}

	/**
	 * Checks whether a thread is waiting or not
	 * 
	 * @return TRUE if the thread is waiting
	 */
	public boolean isWaiting() {

		return this.pleaseWait;
	}

	private boolean pleaseWait = false;
}
