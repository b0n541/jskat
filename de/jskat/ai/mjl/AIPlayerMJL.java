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
import de.jskat.util.rule.BasicSkatRules;

/** A JSkat AI Player
 * @author Markus J. Luzius <markus@luzius.de>
 */
public class AIPlayerMJL extends AbstractJSkatPlayer {

	private Log log = LogFactory.getLog(AIPlayerMJL.class);
	private CardPlayer aiPlayer;
	int maxBidValue = -1;

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		// reset implementation of aiPlayer
		aiPlayer = new SinglePlayer(cards, rules);
		maxBidValue = -1;
		// nothing else to do right now...
	}
	
	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#bidMore(int)
	 */
	@Override
	public int bidMore(int nextBidValue) {
		if(maxBidValue<0) {
			maxBidValue = new Bidding(cards).getMaxBid();
		}
		if(maxBidValue<nextBidValue) {
			aiPlayer = new OpponentPlayer(cards);
			return -1;
		}
		return nextBidValue;
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#holdBid(int)
	 */
	@Override
	public boolean holdBid(int currBidValue) {
		if(maxBidValue<0) {
			maxBidValue = new Bidding(cards).getMaxBid();
		}
		boolean result = !(maxBidValue<18) && maxBidValue>=currBidValue;
		if(!result) {
			aiPlayer = new OpponentPlayer(cards);
		}
		return result; 
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#lookIntoSkat()
	 */
	@Override
	public boolean lookIntoSkat() {
		// TODO really look into skat?
		aiPlayer = new SinglePlayer(cards, rules);
		return true;
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {
		GameAnnouncement game = new GameAnnouncement();
		game.setGameType(new Bidding(cards).getSuggestedGameType());
		return game;
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
	 * @see de.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	protected void startGame() {
		if(singlePlayer!=knowledge.getPlayerPosition()) {
			log.debug("ok? AIPlayerMJL should be OpponentPlayer - actually is: "+(aiPlayer==null?"null":aiPlayer.getClass().getName()));
		}
		else {
			if(aiPlayer==null) {
				log.warn("todo: AIPlayerMJL should already have been set to SinglePlayer! "+(aiPlayer==null?"null":aiPlayer.getClass().getName()));
				aiPlayer = new SinglePlayer(cards, rules);
			}
			else {
				log.debug("ok! AIPlayerMJL already set to SinglePlayer: "+(aiPlayer==null?"null":aiPlayer.getClass().getName()));
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.jskat.ai.JSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
        TrickInfo thisTrick = new TrickInfo();
        GameInfo game = new GameInfo(gameType, gameType.getTrumpSuit(), singlePlayer.getOrder());
        thisTrick.setGameInfo(game);
        thisTrick.setTrick(knowledge.getTrickCards());
		thisTrick.setSinglePlayerPos(singlePlayer.getOrder());
		Card toPlay = aiPlayer.playNextCard(thisTrick);
		// make sure, that there is a card 
		if(toPlay!=null) return toPlay;
		// if there is none, just play the first valid card
		CardList result = getPlayableCards(this.knowledge.getTrickCards());
		if(result.size()<1) {
			log.warn("no playable cards - shouldn't be possible!");
			log.debug("my cards: "+cards+", trick: "+this.knowledge.getTrickCards());
			return null;
		}
		return result.get(0);
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
	 * @see de.jskat.ai.JSkatPlayer#isAIPlayer()
	 */
	@Override
	public boolean isAIPlayer() {
		return true;
	}

}