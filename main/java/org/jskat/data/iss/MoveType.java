package org.jskat.data.iss;

/**
 * Move types on ISS
 */
public enum MoveType {
	/**
	 * Card dealing
	 */
	DEAL,
	/**
	 * Bid
	 */
	BID,
	/**
	 * Holding a bid
	 */
	HOLD_BID,
	/**
	 * Passing
	 */
	PASS,
	/**
	 * Requesting skat cards
	 */
	SKAT_REQUEST,
	/**
	 * Picking up skat
	 */
	PICK_UP_SKAT,
	/**
	 * Announcing game
	 */
	GAME_ANNOUNCEMENT,
	/**
	 * Card playing
	 */
	CARD_PLAY,
	/**
	 * Showing cards
	 */
	SHOW_CARDS,
	/**
	 * Resigning game
	 */
	RESIGN,
	/**
	 * Time out
	 */
	TIME_OUT
}
