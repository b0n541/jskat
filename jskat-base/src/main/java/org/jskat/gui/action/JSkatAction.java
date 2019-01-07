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
	 * Recognizes a change of the active skat table
	 */
	CHANGE_ACTIVE_TABLE,
	/**
	 * Starts a local skat series
	 */
	START_LOCAL_SERIES,
	/**
	 * Continues a local skat series
	 */
	CONTINUE_LOCAL_SERIES,
	/**
	 * Replays a game
	 */
	REPLAY_GAME,
	/**
	 * Plays the next replay step
	 */
	NEXT_REPLAY_STEP,
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
	 * Play grand hand during ramsch games
	 */
	PLAY_GRAND_HAND,
	/**
	 * Call Contra
	 */
	CALL_CONTRA,
	/**
	 * Call Re
	 */
	CALL_RE,
	/**
	 * Play schieberamsch
	 */
	PLAY_SCHIEBERAMSCH,
	/**
	 * skips the skat pickup in a ramsch game (called "Schieben")
	 */
	SCHIEBEN,
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
	/**
	 * Resign a game on ISS
	 */
	RESIGN,
	/**
	 * Shows own cards on ISS
	 */
	SHOW_CARDS,
	/**
	 * Inspects saved ISS games
	 */
	ANALYSE_ISS_GAMES,
	// ----------------------------------------------------------------------------//
	// -- Neural Network Player related actions
	// ----------------------------------------------------------------------------//
	/**
	 * Trains neural networks for Neural Network Player
	 */
	TRAIN_NEURAL_NETWORKS,
	/**
	 * Stops training of neural networks for Neural Network Player
	 */
	STOP_TRAIN_NEURAL_NETWORKS,
	/**
	 * Loads neural network information for Neural Network Player
	 */
	LOAD_NEURAL_NETWORKS,
	/**
	 * Saves neural network information for Neural Network Player
	 */
	SAVE_NEURAL_NETWORKS,
	/**
	 * Resets neural networks
	 */
	RESET_NEURAL_NETWORKS;
}
