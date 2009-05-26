/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.AbstractJSkatPlayer;
import de.jskat.data.GameAnnouncement;
import de.jskat.util.Card;
import de.jskat.util.CardList;

/** A JSkat AI Player
 * @author Markus J. Luzius <markus@luzius.de>
 */
public class AIPlayerMJL extends AbstractJSkatPlayer {

	private int maxBidValue = -1;
	private Log log = LogFactory.getLog(AIPlayerMJL.class);
	
	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {
		// TODO game announcement
		log.debug("game announcement probably causes an exception...");
		return null;
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#bidMore(int)
	 */
	@Override
	public int bidMore(int nextBidValue) {
		if(maxBidValue<0) {
			maxBidValue = new Bidding(cards).getMaxBid();
		}
		if(maxBidValue<18) return -1;
		return (maxBidValue>=nextBidValue?nextBidValue:-1);
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#discardSkat()
	 */
	@Override
	public CardList discardSkat() {
		// TODO check which cards should best be discarded
		log.debug("no algorithm yet, discarding original skat of ["+skat+"]");
		return skat;
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		// don't know what to do here (yet)...
		log.debug("finalizing game...");
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#holdBid(int)
	 */
	@Override
	public boolean holdBid(int currBidValue) {
		if(maxBidValue<0) {
			maxBidValue = new Bidding(cards).getMaxBid();
		}
		if(maxBidValue<18) return false;
		return (maxBidValue>=currBidValue?true:false);
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#isAIPlayer()
	 */
	@Override
	public boolean isAIPlayer() {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#lookIntoSkat()
	 */
	@Override
	public boolean lookIntoSkat() {
		// TODO really look into skat?
		return true;
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
		// TODO implementation!!!
		// but make sure, that card is valid!!!
		CardList result = getPlayableCards(this.knowledge.getTrickCards());
		if(result.size()<1) {
			log.warn("no playable cards - shouldn't be possible!");
			log.debug("my cards: "+cards+", trick: "+this.knowledge.getTrickCards());
			return null;
		}
		return result.get(0);
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		// reset maxBidValue, so it is recalculated next time a game starts... 
		maxBidValue = -1;
		// nothing else to do right now...
	}
}