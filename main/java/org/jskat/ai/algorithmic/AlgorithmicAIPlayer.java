/**
 * 
 */
package org.jskat.ai.algorithmic;

import org.apache.log4j.Logger;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.ai.PlayerKnowledge;
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

	private IAlgorithmicAIPlayer aiPlayer = null;
	BidEvaluator bidEvaluator = null;
	
	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		log.debug("New game preparation for player <"+playerName+">");
		aiPlayer = null;
	}
	
	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		bidEvaluator = null; // not necessry any more
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#bidMore(int)
	 */
	@Override
	public int bidMore(int nextBidValue) {
		if(bidEvaluator==null) bidEvaluator = new BidEvaluator(knowledge.getMyCards());
		if(bidEvaluator.getMaxBid()>=nextBidValue) return nextBidValue;
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#holdBid(int)
	 */
	@Override
	public boolean holdBid(int currBidValue) {
		if(bidEvaluator==null) bidEvaluator = new BidEvaluator(knowledge.getMyCards());
		return (bidEvaluator.getMaxBid()>=currBidValue);
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#pickUpSkat()
	 */
	@Override
	public boolean pickUpSkat() {
		if(bidEvaluator==null) bidEvaluator = new BidEvaluator(knowledge.getMyCards());
		return bidEvaluator.pickUpSkat();
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {
		if(bidEvaluator==null) bidEvaluator = new BidEvaluator(knowledge.getMyCards());
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
		log.debug(playerName+" is playing a card ("+aiPlayer.getClass()+")");
		return aiPlayer.playCard();
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
		if(aiPlayer instanceof AlgorithmicSinglePlayer) {
			log.warn("aiPlayer is not a single player instance: "+aiPlayer);
			aiPlayer = new AlgorithmicSinglePlayer(this);
		}
		return aiPlayer.discardSkat(bidEvaluator);
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		if(aiPlayer==null) aiPlayer = new AlgorithmicOpponentPlayer(this);
		log.debug("aiPlayer set to "+aiPlayer);
	}

	protected PlayerKnowledge getKnowledge() {
		return knowledge;
	}

}
