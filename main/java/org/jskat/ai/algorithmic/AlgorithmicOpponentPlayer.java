/**
 * 
 */
package org.jskat.ai.algorithmic;

import org.apache.log4j.Logger;
import org.jskat.ai.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

/**
 * @author Markus J. Luzius <br>
 * created: 15.06.2011 19:13:50
 *
 */
public class AlgorithmicOpponentPlayer implements IAlgorithmicAIPlayer {
	private static final Logger log = Logger.getLogger(AlgorithmicOpponentPlayer.class);

	private final AlgorithmicAIPlayer myPlayer;
	private final PlayerKnowledge knowledge;
	
	/**
	 * 
	 */
	AlgorithmicOpponentPlayer(AlgorithmicAIPlayer p) {
		myPlayer = p;
		knowledge = p.getKnowledge();
		log.debug("Defining player <"+myPlayer.getPlayerName()+"> as "+this.getClass().getName());
	}
	
	
	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#discardSkat()
	 */
	public CardList discardSkat() {
		throw new IllegalStateException("opponent player cannot discard a skat");
	}
	
	/* (non-Javadoc)
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	public Card playCard() {
		if(knowledge.getNoOfTricks()<1) {
			return openGame();
		}
		if(knowledge.getTrickCards()==null || knowledge.getTrickCards().isEmpty()) {
			return openTrick();
		}
			
		// fallback: play the first valid card
		for(Card c: knowledge.getMyCards()) {
			if(c.isAllowed(knowledge.getGame().getGameType(), knowledge.getTrickCards().isEmpty()?null:knowledge.getTrickCards().get(0), knowledge.getMyCards())) return c;
		}
		log.warn("no possible card found in card list ["+knowledge.getMyCards()+"] with "+knowledge.getGame().getGameType()+" / "+knowledge.getTrickCards().get(0));
		return knowledge.getMyCards().get(0);
	}
	
	private Card openGame() {
		CardList cards = knowledge.getMyCards();
		if(knowledge.getDeclarer()==Player.MIDDLEHAND) {
			// "kurzer Weg, lange Farbe"
			Suit longSuit = cards.getMostFrequentSuit(knowledge.getGame().getGameType().asSuit());
			if(cards.get(cards.getFirstIndexOfSuit(longSuit)).getRank()==Rank.ACE) {
				return cards.get(knowledge.getMyCards().getFirstIndexOfSuit(longSuit)); 
			}
			return cards.get(knowledge.getMyCards().getLastIndexOfSuit(cards.getMostFrequentSuit()));
		}
		else if(knowledge.getDeclarer()==Player.REARHAND) {
			// "langer Weg, kurze Farbe"
			int minCount = 0;
			Suit shortSuit = null;
			for(Suit suit: Suit.values()) {
				if(suit==knowledge.getGame().getGameType().asSuit()) continue;
				int cardCount = cards.getSuitCount(suit, false);
				if(cardCount>0 && cardCount<minCount) {
					shortSuit = suit;
					minCount = cardCount;
				}
			}
			if(shortSuit==null) {
				log.warn("no short suit found: "+cards);
				return cards.get(cards.size()-1);
			}
			if(cards.get(cards.getFirstIndexOfSuit(shortSuit)).getRank()==Rank.ACE) {
				return cards.get(cards.getFirstIndexOfSuit(shortSuit)); 
			}
			return cards.get(cards.getLastIndexOfSuit(shortSuit));
		}
		else {
			log.warn("wrong declarer position: "+knowledge.getDeclarer());
			return cards.get(cards.size()-1);
		}
	}

	private Card openTrick() {
		CardList cards = knowledge.getMyCards();
		// "kurzer Weg, lange Farbe"
		Suit longSuit = cards.getMostFrequentSuit(knowledge.getGame().getGameType().asSuit());
		if(cards.get(cards.getFirstIndexOfSuit(longSuit)).getRank()==Rank.ACE) {
			return cards.get(cards.getFirstIndexOfSuit(longSuit)); 
		}
		return cards.get(cards.getLastIndexOfSuit(longSuit));
	}
}
