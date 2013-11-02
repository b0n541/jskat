/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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
