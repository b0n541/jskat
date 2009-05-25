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
public enum JSkatActions {

//----------------------------------------------------------------------------//
//-- General actions ---------------------------------------------------------//
//----------------------------------------------------------------------------//	
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
	 * Shows the about information
	 */
	ABOUT_JSKAT,
	/**
	 * Shows the help information
	 */
	HELP,
	/**
	 * Creates a local skat table
	 */
	CREATE_LOCAL_TABLE,
	/**
	 * Starts a local skat series
	 */
	START_LOCAL_SERIES,
//----------------------------------------------------------------------------//
//-- Human player actions ----------------------------------------------------//
//----------------------------------------------------------------------------//
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
	/**
	 * Continues a series
	 */
	CONTINUE_LOCAL_SERIES,
//----------------------------------------------------------------------------//
//-- ISS related actions -----------------------------------------------------//
//----------------------------------------------------------------------------//
	/**
	 * Shows the login screen
	 */
	SHOW_ISS_LOGIN,
	/**
	 * Connects to the International Skat Server
	 */
	CONNECT_TO_ISS,
//----------------------------------------------------------------------------//
//-- Neural Network Player related actions -----------------------------------//
//----------------------------------------------------------------------------//
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
	SAVE_NEURAL_NETWORKS
}
