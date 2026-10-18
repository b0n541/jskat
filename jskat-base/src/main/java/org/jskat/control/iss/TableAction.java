
package org.jskat.control.iss;

/**
 * Actions on an ISS table
 */
public enum TableAction {

	/**
	 * E n d value.
	 */
	/**
	 * Request current table state.
	 */
	STATE,
	/**
	 * Mark the player ready.
	 */
	READY,
	/**
	 * Start the table game.
	 */
	START,
	/**
	 * Allow the player to act.
	 */
	GO,
	/**
	 * Submit a play to the table.
	 */
	PLAY,
	/**
	 * End the current table game.
	 */
	END
}
