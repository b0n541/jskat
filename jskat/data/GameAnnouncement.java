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
 * An object of this class is returned by a AI player
 * for game announcement
 *
 * @author  Jan Schäfer <j@nschaefer.net>
 */

public class GameAnnouncement {

	public int getGameType() {
		
		return gameType;
	}

	public void setGameType(int gameType) {
		
		this.gameType = gameType;
		
		if (gameType != SkatConstants.SUIT) {
			
			setTrump(-1);
		}
	}
	
	public boolean isSchneider() {
		return schneider;
	}
	
	public void setSchneider(boolean schneider) {
		this.schneider = schneider;
	}
	
	public boolean isSchwarz() {
		return schwarz;
	}
	
	public void setSchwarz(boolean schwarz) {
		this.schwarz = schwarz;
	}
	
	public int getTrump() {
		return trump;
	}
	
	public void setTrump(int trump) {
		
		this.trump = trump;
		
		if (trump == SkatConstants.CLUBS ||
				trump == SkatConstants.SPADES ||
				trump == SkatConstants.HEARTS ||
				trump == SkatConstants.DIAMONDS) {
			
			setGameType(SkatConstants.SUIT);
		}
	}

	public boolean isOuvert() {
		return ouvert;
	}

	public void setOuvert(boolean ouvert) {
		this.ouvert = ouvert;
	}

	private int gameType;
	private int trump;
	private boolean ouvert = false;
	private boolean schneider = false;
	private boolean schwarz = false;
}
