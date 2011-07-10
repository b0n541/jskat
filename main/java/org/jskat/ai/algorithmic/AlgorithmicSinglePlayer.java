/**
 * 
 */
package org.jskat.ai.algorithmic;

import org.apache.log4j.Logger;
import org.jskat.ai.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * @author Markus J. Luzius <br>
 * created: 15.06.2011 19:13:50
 *
 */
public class AlgorithmicSinglePlayer implements IAlgorithmicAIPlayer  {
	private static final Logger log = Logger.getLogger(AlgorithmicSinglePlayer.class);

	private final AlgorithmicAIPlayer myPlayer;
	private final PlayerKnowledge knowledge;
	
	/**
	 * 
	 */
	AlgorithmicSinglePlayer(AlgorithmicAIPlayer p) {
		myPlayer = p;
		knowledge = p.getKnowledge();
		log.debug("Defining player <"+myPlayer.getPlayerName()+"> as "+this.getClass().getName());
	}
	
	CardList discardSkat(BidEvaluator bid) {
		CardList cards = knowledge.getMyCards();
		cards.sort(bid.getSuggestedGameType());
		CardList toDiscard = new CardList();
		toDiscard.add(cards.remove(10));
		toDiscard.add(cards.remove(10));
		return toDiscard;
	}
	
	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	public Card playCard() {
		CardList cards = knowledge.getMyCards();
		for(Card c: cards) {
			if(c.isAllowed(knowledge.getGame().getGameType(), knowledge.getTrickCards().isEmpty()?null:knowledge.getTrickCards().get(0), cards)) return c;
		}
		log.warn("no possible card found in card list ["+cards+"] with "+knowledge.getGame().getGameType()+" / "+knowledge.getTrickCards().get(0));
		return cards.get(0);
	}

}
