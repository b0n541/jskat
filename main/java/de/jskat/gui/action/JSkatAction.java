/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action;

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
	 * Looks into skat
	 */
	LOOK_INTO_SKAT,
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
