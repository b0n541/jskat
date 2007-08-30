/*

@ShortLicense@

Authos: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import jskat.player.AIPlayerMJL.GameInfo;
import jskat.player.AIPlayerMJL.OpponentPlayer;
import jskat.player.AIPlayerMJL.TrickInfo;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

/**
 * OpponentPlayerTestCase.java
 * Created 24.05.2007 15:22:32
 * @author Markus J. Luzius
 *
 */
public class OpponentPlayerTestCase {
	private OpponentPlayer op = new OpponentPlayer(1);
	private TrickInfo ti;
	private CardVector myCards;
	
	public OpponentPlayerTestCase(int singlePlayerPos, CardVector cards, CardVector trick, int gameType, int trump) {
		myCards = cards;

		GameInfo gi = new GameInfo(gameType, trump, 0);

		ti = new TrickInfo();
		ti.setGameInfo(gi);
		ti.setTrick(trick);		
		ti.setSinglePlayerPos(singlePlayerPos);

	}
	
	public OpponentPlayer getOpponentPlayer() {
		return op;
	}
	
	public CardVector getCards() {
		return myCards;
	}

	public TrickInfo getTrickInfo() {
		return ti;
	}
	
}
