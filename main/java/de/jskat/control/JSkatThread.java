/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control;

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
