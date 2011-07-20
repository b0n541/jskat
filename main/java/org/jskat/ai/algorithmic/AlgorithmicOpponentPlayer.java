/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	public Card playCard() {
		if(knowledge.getTrickCards()==null || knowledge.getTrickCards().isEmpty()) {
			if(knowledge.getNoOfTricks()<1) {
				return openGame();
			}
			return openTrick();
		}
		if(knowledge.getTrickCards().size()==1) {
			return playMiddlehandCard();
		}
		return playRearhandCard();
	}
	
	private Card openGame() {
		CardList cards = knowledge.getMyCards();
		if(knowledge.getDeclarer()==Player.MIDDLEHAND) {
			// "kurzer Weg, lange Farbe"
			Suit longSuit = cards.getMostFrequentSuit(knowledge.getGameType().asSuit());
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
				if(suit==knowledge.getGameType().asSuit()) continue;
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
			log.warn(".openGame(): wrong declarer position: "+knowledge.getDeclarer());
			return cards.get(cards.size()-1);
		}
	}

	private Card openTrick() {
		CardList cards = knowledge.getMyCards();
		for(Suit s: Suit.values()) {
			if(!knowledge.couldHaveSuit(knowledge.getDeclarer(), s) && cards.hasSuit(knowledge.getGameType(), s)) {
				return cards.get(cards.getLastIndexOfSuit(s));
			}
		}
		return openGame();
	}

	private Card playMiddlehandCard() {
		log.debug("Opponent player is in middlehand");
		// fallback: take the first valid card
		CardList cards = knowledge.getMyCards();
		Card initialCard = knowledge.getTrickCards().get(0);
		Card result = null;
		if(knowledge.getDeclarer()==Player.FOREHAND) {
			log.debug("Single player has already played a card");
			for(Card c: cards) {
				if(c.beats(knowledge.getGameType(), initialCard)) result = c;
			}
			if(result!=null) return result;
		}
		else {
			log.debug("Single player is in rearhand");
			if(knowledge.couldHaveSuit(knowledge.getDeclarer(), initialCard.getSuit())) {
				if(initialCard.getRank()!=Rank.JACK && initialCard.getSuit()!=knowledge.getTrumpSuit() && cards.contains(Card.getCard(initialCard.getSuit(), Rank.ACE))) return Card.getCard(initialCard.getSuit(), Rank.ACE);
			}
			else {
				// if the single player doesn't have that suit: take the lowest one
				result = cards.get(cards.getLastIndexOfSuit(initialCard.getSuit()));
				if(result!=null) return result;
				if(knowledge.couldHaveTrump(knowledge.getDeclarer())) {
				}
			}
		}
		
		
		for(Card c: cards) {
			if(c.isAllowed(knowledge.getGameType(), knowledge.getTrickCards().isEmpty()?null:knowledge.getTrickCards().get(0), cards)) result = c;
		}
		if(result!=null) return result;
		log.warn("no possible card found in card list ["+cards+"] with "+knowledge.getGameType()+" / "+knowledge.getTrickCards().get(0));
		return cards.get(0);
	}
	
	private Card playRearhandCard() {
		log.debug("Opponent player is in rearhand");
		// fallback: take the first valid card
		CardList cards = knowledge.getMyCards();
		if(knowledge.getTrickCards().get(0).beats(knowledge.getGameType(), knowledge.getTrickCards().get(1))) {
			// forehand win
			if(knowledge.getDeclarer()==Player.FOREHAND) {
				// it's a single player win so far
			}
			else {
				// it's ours already
			}
		}
		else {
			// middlehand win
			if(knowledge.getDeclarer()==Player.MIDDLEHAND) {
				// it's a single player win so far
			}
			else {
				// it's ours already
			}
		}
		
		
		Card result = null;
		for(Card c: cards) {
			if(c.isAllowed(knowledge.getGameType(), knowledge.getTrickCards().isEmpty()?null:knowledge.getTrickCards().get(0), cards)) result = c;
		}
		if(result!=null) return result;
		log.warn("no possible card found in card list ["+cards+"] with "+knowledge.getGameType()+" / "+knowledge.getTrickCards().get(0));
		return cards.get(0);
	}

	/* (non-Javadoc)
	 * @see org.jskat.ai.algorithmic.IAlgorithmicAIPlayer#discardSkat(org.jskat.ai.algorithmic.BidEvaluator)
	 */
	@Override
	public CardList discardSkat(BidEvaluator bidEvaluator) {
		throw new IllegalStateException("opponent player cannot discard a skat");
	}
}
