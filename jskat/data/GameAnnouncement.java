/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import jskat.share.SkatConstants;

/**
 * Game announcement
 * 
 * An object of this class is returned by an AI player
 * for game announcement
 */
public class GameAnnouncement {

	/**
	 * Constructor
	 */
	public GameAnnouncement() {
		
	}

	private SkatConstants.GameTypes gameType;
	private SkatConstants.Suits trump;
	private boolean hand = false;
	private boolean ouvert = false;
	private boolean schneider = false;
	private boolean schwarz = false;
	
	/**
	 * Gets the game type
	 * 
	 * @return Game type
	 */
	public SkatConstants.GameTypes getGameType() {
		
		return gameType;
	}

	/**
	 * Sets the game type
	 * 
	 * @param newGameType
	 */
	public void setGameType(SkatConstants.GameTypes newGameType) {
		
		gameType = newGameType;
		
		if (gameType != SkatConstants.GameTypes.SUIT) {
			
			setTrump(null);
		}
	}
	
	/**
	 * Checks whether schneider was announced or not
	 * 
	 * @return TRUE if schneider was announced
	 */
	public boolean isSchneider() {
		return schneider;
	}
	
	/**
	 * Sets flag for schneider announcement
	 * 
	 * @param isSchneider TRUE if schneider was announced
	 */
	public void setSchneider(boolean isSchneider) {
		schneider = isSchneider;
	}
	
	/**
	 * Checks whether schwarz was announced or not
	 * 
	 * @return TRUE if schwarz was announced
	 */
	public boolean isSchwarz() {
		return schwarz;
	}
	
	/**
	 * Sets flag for schwarz announcement
	 * 
	 * @param isSchwarz TRUE if schwarz was announced
	 */
	public void setSchwarz(boolean isSchwarz) {
		schwarz = isSchwarz;
	}
	
	/**
	 * Gets the trump color for suit games
	 * 
	 * @return Trump color
	 */
	public SkatConstants.Suits getTrump() {
		return trump;
	}
	
	/**
	 * Sets the trump color for suit games
	 * 
	 * @param trump Trump color
	 */
	public void setTrump(SkatConstants.Suits newTrump) {
		
		trump = newTrump;
		
		if (trump == SkatConstants.Suits.CLUBS ||
				trump == SkatConstants.Suits.SPADES ||
				trump == SkatConstants.Suits.HEARTS ||
				trump == SkatConstants.Suits.DIAMONDS) {
			
			setGameType(SkatConstants.GameTypes.SUIT);
		}
	}

	/**
	 * Checks whether an ouvert game was announced or not
	 * 
	 * @return TRUE if an ouvert game was announced
	 */
	public boolean isOuvert() {
		return ouvert;
	}

	/**
	 * Sets flag for an ouvert announcement
	 * 
	 * @param isOuvert TRUE if ouvert was announced
	 */
	public void setOuvert(boolean isOuvert) {
		ouvert = isOuvert;
	}

	/**
	 * Checks whether a hand game was announced or not
	 * 
	 * @return TRUE fi a hand game was announced
	 */
	public boolean isHand() {
		return hand;
	}

	/**
	 * Sets the flag for a hand announcement
	 * 
	 * @param isHand TRUE if hand was announced
	 */
	public void setHand(boolean isHand) {
		hand = isHand;
	}
}
