/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

import jskat.share.SkatConstants;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class GameInfo {

	/**
	 * default constructor
	 */
	public GameInfo() {
	}
	
	/** Constructor for setting up the game info with initial values
	 * @param type game type
	 * @param suit trump color
	 * @param player id of the single player
	 */
	public GameInfo(SkatConstants.GameTypes type, SkatConstants.Suits suit, int player) {
		gameType = type;
		trump = suit;
		singlePlayer = player;
	}

	/**
	 * get the current game type
	 * @return current game type
	 */
	public SkatConstants.GameTypes getGameType() {
		return gameType;
	}

	/**
	 * get the current single player
	 * @return ID of the current single player
	 */
	public int getSinglePlayer() {
		return singlePlayer;
	}

	/**
	 * get the current trump suit
	 * @return current trump suit
	 */
	public SkatConstants.Suits getTrump() {
		return trump;
	}

	/**
	 * set the current game type
	 * @param gameType game type
	 */
	public void setGameType(SkatConstants.GameTypes gameType) {
		this.gameType= gameType;
	}

	/**
	 * set the current single player
	 * @param id ID of the single player
	 */
	public void setSinglePlayer(int id) {
		singlePlayer= id;
	}

	/**
	 * set the trump suit
	 * @param suit trump suit
	 */
	public void setTrump(SkatConstants.Suits suit) {
		trump= suit;
	}

	/**	 type of the game
	 * @see jskat.share.SkatConstants */
	private SkatConstants.GameTypes gameType;
	/**	 trump suit in the current game
	 * @see jskat.share.SkatConstants */
	private SkatConstants.Suits trump;
	/** id of the single player */
	private int singlePlayer;
}
