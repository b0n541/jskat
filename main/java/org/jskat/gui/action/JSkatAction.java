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


package org.jskat.gui.action;

/**
 * All actions that can be invoked by a view
 */
public enum JSkatAction {

	// ----------------------------------------------------------------------------//
	// -- General actions
	// ----------------------------------------------------------------------------//
	/**
	 * Loads a saved series of games
	 */
	LOAD_SERIES,
	/**
	 * Saves a series of games
	 */
	SAVE_SERIES,
	/**
	 * Saves a series of games under a new name
	 */
	SAVE_SERIES_AS,
	/**
	 * Exits JSkat
	 */
	EXIT_JSKAT,
	/**
	 * Shows the preferences dialog
	 */
	PREFERENCES,
	/**
	 * Shows the about information
	 */
	ABOUT_JSKAT,
	/**
	 * Shows the help information
	 */
	HELP,
	/**
	 * Shows the license information
	 */
	LICENSE,
	/**
	 * Creates a local skat table
	 */
	CREATE_LOCAL_TABLE,
	/**
	 * Recognizes a change of the acitve skat table
	 */
	CHANGE_ACTIVE_TABLE,
	/**
	 * Starts a local skat series
	 */
	START_LOCAL_SERIES,
	/**
	 * Pauses a local skat series
	 */
	PAUSE_LOCAL_SERIES,
	/**
	 * Continues a local skat series
	 */
	CONTINUE_LOCAL_SERIES,
	// ----------------------------------------------------------------------------//
	// -- Human player actions
	// ----------------------------------------------------------------------------//
	/**
	 * Makes a bid
	 */
	MAKE_BID,
	/**
	 * Holds a bid
	 */
	HOLD_BID,
	/**
	 * Passes a bid
	 */
	PASS_BID,
	/**
	 * Picks up skat
	 */
	PICK_UP_SKAT,
	/**
	 * Plays a hand game
	 */
	PLAY_HAND_GAME,
	/**
	 * Puts a card into the skat
	 */
	PUT_CARD_INTO_SKAT,
	/**
	 * Takes a card from the skat
	 */
	TAKE_CARD_FROM_SKAT,
	/**
	 * Discards skat cards
	 */
	DISCARD_CARDS,
	/**
	 * Announces a game
	 */
	ANNOUNCE_GAME,
	/**
	 * Plays a card
	 */
	PLAY_CARD,
	// ----------------------------------------------------------------------------//
	// -- ISS related actions
	// ----------------------------------------------------------------------------//
	/**
	 * Opens ISS homepage in default browser
	 */
	OPEN_ISS_HOMEPAGE,
	/**
	 * Opens ISS registration form in default browser
	 */
	REGISTER_ON_ISS,
	/**
	 * Shows the login screen
	 */
	SHOW_ISS_LOGIN,
	/**
	 * Connects to the International Skat Server
	 */
	CONNECT_TO_ISS,
	/**
	 * Disconnects from the International Skat Server
	 */
	DISCONNECT_FROM_ISS,
	/**
	 * Sends a chat message
	 */
	SEND_CHAT_MESSAGE,
	/**
	 * Creates a table
	 */
	CREATE_ISS_TABLE,
	/**
	 * Sit on table
	 */
	JOIN_ISS_TABLE,
	/**
	 * Leave a table
	 */
	LEAVE_ISS_TABLE,
	/**
	 * Observe a table
	 */
	OBSERVE_ISS_TABLE,
	/**
	 * Invite a player on ISS
	 */
	INVITE_ISS_PLAYER,
	/**
	 * Ready to play
	 */
	READY_TO_PLAY,
	/**
	 * Enable talking
	 */
	TALK_ENABLED,
	/**
	 * Change table seats
	 */
	CHANGE_TABLE_SEATS,
	// ----------------------------------------------------------------------------//
	// -- Neural Network Player related actions
	// ----------------------------------------------------------------------------//
	/**
	 * Trains neural networks for Neural Network Player
	 */
	TRAIN_NEURAL_NETWORKS,
	/**
	 * Loads neural network information for Neural Network Player
	 */
	LOAD_NEURAL_NETWORKS,
	/**
	 * Saves neural network information for Neural Network Player
	 */
	SAVE_NEURAL_NETWORKS;
}
