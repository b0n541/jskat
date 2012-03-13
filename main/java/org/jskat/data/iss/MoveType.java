/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0
 * Copyright (C) 2012-03-13
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
