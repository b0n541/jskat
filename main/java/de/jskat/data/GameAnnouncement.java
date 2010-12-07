/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.util.GameType;

/**
 * Game announcement
 * 
 * An object of this class is returned by an AI player for game announcement
 */
public class GameAnnouncement {

	private static Log log = LogFactory.getLog(GameAnnouncement.class);

	/**
	 * Game type
	 */
	private GameType gameType;
	/**
	 * Ouvert announcement
	 */
	private boolean ouvert;
	/**
	 * Hand announcement
	 */
	private boolean hand;
	/**
	 * Schneider announcement
	 */
	private boolean schneider;
	/**
	 * Schwarz announcement
	 */
	private boolean schwarz;
	/**
	 * Contra announcement
	 */
	private boolean contra;
	/**
	 * Re announcement
	 */
	private boolean re;
	/**
	 * Bock announcement
	 */
	private boolean bock;

	/**
	 * Constructor
	 */
	public GameAnnouncement() {

		initializeVariables();
	}

	/**
	 * Initializes all variables
	 */
	public void initializeVariables() {

		gameType = null;
		ouvert = false;
		schneider = false;
		schwarz = false;
		contra = false;
		re = false;
		bock = false;
	}

	/**
	 * Gets the game type
	 * 
	 * @return Game type
	 */
	public GameType getGameType() {

		return gameType;
	}

	/**
	 * Sets the game type
	 * 
	 * @param newGameType
	 */
	public void setGameType(GameType newGameType) {

		gameType = newGameType;
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
	 * @param isSchneider
	 *            TRUE if schneider was announced
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
	 * @param isSchwarz
	 *            TRUE if schwarz was announced
	 */
	public void setSchwarz(boolean isSchwarz) {

		schwarz = isSchwarz;
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
	 * @param isOuvert
	 *            TRUE if ouvert was announced
	 */
	public void setOuvert(boolean isOuvert) {

		ouvert = isOuvert;
	}

	/**
	 * Checks whether a hand game was announced or not
	 * 
	 * @return TRUE if a hand game was announced
	 */
	public boolean isHand() {

		return hand;
	}

	/**
	 * Sets flag for a hand announcement
	 * 
	 * @param isHand
	 *            TRUE, if hand was announced
	 */
	public void setHand(boolean isHand) {

		hand = isHand;
	}

	/**
	 * Checks whether contra was announced for the game
	 * 
	 * @return TRUE if contra was announced
	 */
	public boolean isContra() {

		return contra;
	}

	/**
	 * Sets the flag for contra announcement
	 * 
	 * @param isContra
	 *            TRUE if contra was announced
	 */
	public void setContra(boolean isContra) {

		contra = isContra;
	}

	/**
	 * Checks whether re was announced for the game
	 * 
	 * @return TRUE if re was announced
	 */
	public boolean isRe() {

		return re;
	}

	/**
	 * Sets the flag for re announcement
	 * 
	 * @param isRe
	 *            TRUE if re was announced
	 */
	public void setRe(boolean isRe) {

		re = isRe;
	}

	/**
	 * Checks whether bock was announced for the game
	 * 
	 * @return TRUE if bock was announced
	 */
	public boolean isBock() {

		return bock;
	}

	/**
	 * Sets the flag for bock announcement
	 * 
	 * @param isBock
	 *            TRUE if bock was announced
	 */
	public void setBock(boolean isBock) {

		contra = isBock;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append("Game announcement: ").append(gameType);

		if (hand) {
			result.append(" hand");
		}
		if (ouvert) {

			result.append(" ouvert");
		}

		if (schneider) {

			result.append(" schneider");
		}

		if (schwarz) {

			result.append(" schwarz");
		}

		return result.toString();
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		log.debug("Cloning GameAnnouncement..."); //$NON-NLS-1$

		GameAnnouncement clone = new GameAnnouncement();

		clone.setGameType(gameType);
		clone.setHand(hand);
		clone.setOuvert(ouvert);
		clone.setSchneider(schneider);
		clone.setSchwarz(schwarz);
		clone.setContra(contra);
		clone.setRe(re);
		clone.setBock(bock);

		log.debug(this + " " + clone); //$NON-NLS-1$

		return clone;
	}
}
