/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Extends the normal thread class with an wait option
 */
public class JSkatThread extends Thread {

	private static Log log = LogFactory.getLog(JSkatThread.class);

	/**
	 * Starts the waiting
	 */
	public void startWaiting() {

		this.pleaseWait = true;
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
					log.error(e);
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
