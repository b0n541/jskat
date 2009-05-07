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
import de.jskat.util.SkatConstants;

/**
 * Game announcement
 * 
 * An object of this class is returned by an AI player
 * for game announcement
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
		
		this.gameType = null;
		this.ouvert = false;
		this.schneider = false;
		this.schwarz = false;
		this.contra = false;
		this.re = false;
		this.bock = false;
	}
	
	/**
	 * Gets the game type
	 * 
	 * @return Game type
	 */
	public GameType getGameType() {
		
		return this.gameType;
	}

	/**
	 * Sets the game type
	 * 
	 * @param newGameType
	 */
	public void setGameType(GameType newGameType) {
		
		this.gameType = newGameType;
	}
	
	/**
	 * Checks whether schneider was announced or not
	 * 
	 * @return TRUE if schneider was announced
	 */
	public boolean isSchneider() {
		
		return this.schneider;
	}
	
	/**
	 * Sets flag for schneider announcement
	 * 
	 * @param isSchneider TRUE if schneider was announced
	 */
	public void setSchneider(boolean isSchneider) {
		
		this.schneider = isSchneider;
	}
	
	/**
	 * Checks whether schwarz was announced or not
	 * 
	 * @return TRUE if schwarz was announced
	 */
	public boolean isSchwarz() {
		
		return this.schwarz;
	}
	
	/**
	 * Sets flag for schwarz announcement
	 * 
	 * @param isSchwarz TRUE if schwarz was announced
	 */
	public void setSchwarz(boolean isSchwarz) {
		
		this.schwarz = isSchwarz;
	}
	
	/**
	 * Checks whether an ouvert game was announced or not
	 * 
	 * @return TRUE if an ouvert game was announced
	 */
	public boolean isOuvert() {
		
		return this.ouvert;
	}

	/**
	 * Sets flag for an ouvert announcement
	 * 
	 * @param isOuvert TRUE if ouvert was announced
	 */
	public void setOuvert(boolean isOuvert) {
		
		this.ouvert = isOuvert;
	}

	/**
	 * Checks whether contra was announced for the game
	 * 
	 * @return TRUE if contra was announced
	 */
	public boolean isContra() {
		
		return this.contra;
	}
	
	/**
	 * Sets the flag for contra announcement
	 * 
	 * @param isContra TRUE if contra was announced
	 */
	public void setContra(boolean isContra) {
		
		this.contra = isContra;
	}
	
	/**
	 * Checks whether re was announced for the game
	 * 
	 * @return TRUE if re was announced
	 */
	public boolean isRe() {
		
		return this.re;
	}
	
	/**
	 * Sets the flag for re announcement
	 * 
	 * @param isRe TRUE if re was announced
	 */
	public void setRe(boolean isRe) {
		
		this.re = isRe;
	}
	
	/**
	 * Checks whether bock was announced for the game
	 * 
	 * @return TRUE if bock was announced
	 */
	public boolean isBock() {
		
		return this.bock;
	}
	
	/**
	 * Sets the flag for bock announcement
	 * 
	 * @param isBock TRUE if bock was announced
	 */
	public void setBock(boolean isBock) {
		
		this.contra = isBock;
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer();
		
		result.append("Game announcement: ").append(this.gameType);
		
		if (this.ouvert) {
			
			result.append(" ouvert");
		}
		
		if (this.schneider) {
			
			result.append(" schneider");
		}
		
		if (this.schwarz) {
			
			result.append(" schwarz");
		}
		
		return result.toString();
	}
	
	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		log.debug("Cloning GameAnnouncement...");
		
		GameAnnouncement clone = new GameAnnouncement();
		
		clone.setGameType(this.gameType);
		clone.setOuvert(this.ouvert);
		clone.setSchneider(this.schneider);
		clone.setSchwarz(this.schwarz);
		clone.setContra(this.contra);
		clone.setRe(this.re);
		clone.setBock(this.bock);
		
		log.debug(this + " " + clone);
		
		return clone;
	}
}
