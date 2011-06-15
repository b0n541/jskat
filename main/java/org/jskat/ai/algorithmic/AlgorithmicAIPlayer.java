/**
 * 
 */
package org.jskat.ai.algorithmic;

import org.apache.log4j.Logger;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.ai.IJSkatPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * @author Markus J. Luzius <br>
 * created: 13.05.2011 17:33:05
 *
 */
public class AlgorithmicAIPlayer extends AbstractJSkatPlayer {
	private static final Logger log = Logger.getLogger(AlgorithmicAIPlayer.class);

	private IJSkatPlayer aiPlayer = null;
	BidEvaluator bidEvaluator = null;
	
	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		log.debug("New game preparation for player <"+playerName+">");
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		// TODO Auto-generated method stub
		bidEvaluator = null; // not necessry any more
		aiPlayer = null;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#bidMore(int)
	 */
	@Override
	public int bidMore(int nextBidValue) {
		if(bidEvaluator==null) bidEvaluator = new BidEvaluator(cards);
		if(bidEvaluator.getMaxBid()>=nextBidValue) return nextBidValue;
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#holdBid(int)
	 */
	@Override
	public boolean holdBid(int currBidValue) {
		if(bidEvaluator==null) bidEvaluator = new BidEvaluator(cards);
		return (bidEvaluator.getMaxBid()>=currBidValue);
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#pickUpSkat()
	 */
	@Override
	public boolean pickUpSkat() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {
		if(bidEvaluator==null) bidEvaluator = new BidEvaluator(cards);
		GameAnnouncement myGame = new GameAnnouncement();
		myGame.setGameType(bidEvaluator.getSuggestedGameType());
		aiPlayer = new AlgorithmicSinglePlayer(this);
		return myGame;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#isAIPlayer()
	 */
	@Override
	public final boolean isAIPlayer() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#discardSkat()
	 */
	@Override
	public CardList discardSkat() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		log.debug(aiPlayer);
	}

}
