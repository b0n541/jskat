package org.jskat.control.event;

import org.jskat.data.SkatGameData;

/**
 * Interface for events during a Skat game
 */
public interface Event {
	/**
	 * Processes the event forward.
	 */
	public void processForward(SkatGameData data);

	/**
	 * Processes the event backward.
	 */
	public void processBackward(SkatGameData data);
}
