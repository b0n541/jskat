/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

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
	public GameInfo(int type, int suit, int player) {
		gameType = type;
		trump = suit;
		singlePlayer = player;
	}

	/**
	 * get the current game type
	 * @return current game type
	 */
	public int getGameType() {
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
	public int getTrump() {
		return trump;
	}

	/**
	 * set the current game type
	 * @param gameType game type
	 */
	public void setGameType(int gameType) {
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
	public void setTrump(int suit) {
		trump= suit;
	}

	/**	 type of the game
	 * @see jskat.share.SkatConstants */
	private int gameType;
	/**	 trump suit in the current game
	 * @see jskat.share.SkatConstants */
	private int trump;
	/** id of the single player */
	private int singlePlayer;
}
