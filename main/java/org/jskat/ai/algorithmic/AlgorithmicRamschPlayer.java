/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-12
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
package org.jskat.ai.algorithmic;

import org.apache.log4j.Logger;
import org.jskat.ai.PlayerKnowledge;
import org.jskat.data.JSkatOptions;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

/**
 * @author Markus J. Luzius <br>
 *         created: 15.06.2011 19:13:50
 * 
 */
public class AlgorithmicRamschPlayer implements IAlgorithmicAIPlayer {
	private static final Logger log = Logger
			.getLogger(AlgorithmicRamschPlayer.class);

	private final AlgorithmicAIPlayer myPlayer;
	private final PlayerKnowledge knowledge;

	/**
	 * 
	 */
	AlgorithmicRamschPlayer(AlgorithmicAIPlayer p) {
		myPlayer = p;
		knowledge = p.getKnowledge();
		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as "
				+ this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	public Card playCard() {
		if (knowledge.getMyCards().size() == 1)
			return knowledge.getMyCards().get(0);
		if (knowledge.getTrickCards() == null
				|| knowledge.getTrickCards().isEmpty()) {
			if (knowledge.getNoOfTricks() < 1) {
				return openGame();
			}
			return openTrick();
		}
		if (knowledge.getTrickCards().size() == 1) {
			return playMiddlehandCard();
		}
		return playRearhandCard();
	}

	private Card openGame() {
		CardList cards = knowledge.getMyCards();
		if(cards.get(0).getRank()==Rank.JACK && cards.get(0).getSuit().ordinal()>2 && cards.get(1).getRank()!=Rank.JACK) return cards.get(0);
		int resultIndex = -1;
		for(Suit s: Suit.values()) {
			if(cards.getSuitCount(s, false)==1) {
				if(resultIndex<0 || cards.get(resultIndex).getRamschOrder()>cards.get(cards.getFirstIndexOfSuit(s)).getRamschOrder()) {
					resultIndex = cards.getFirstIndexOfSuit(s);
				}
			}
			else if(cards.getSuitCount(s, false)==2 && cards.get(cards.getLastIndexOfSuit(s)).getRank()==Rank.SEVEN) {
					resultIndex = cards.getFirstIndexOfSuit(s);
			}
		}
		if(resultIndex>=0) return cards.get(resultIndex);
		return cards.get(cards.size() - 1);
	}

	private Card openTrick() {
		CardList cards = knowledge.getMyCards();
		int[] playedCards = knowledge.getPlayedCardsBinary();
		int resultIndex = -1;
		// see if I want to get rid of any single cards
		for(Suit s: Suit.values()) {
			if((knowledge.getPlayedCardsBinary()[s.ordinal()]&127)>0) continue;
			Card c = cards.get(cards.getFirstIndexOfSuit(s, false));
			if(cards.getSuitCount(s, false)==1 && c.getRank().getRamschOrder()<6 && c.getRank().getRamschOrder()>2) {
				if(resultIndex<0) {
					resultIndex = cards.getIndexOf(c);
				}
				else {
					if(c.getRamschOrder()>cards.get(resultIndex).getRamschOrder()) {
						resultIndex = cards.getIndexOf(c);						
					}
				}
			}
			Card lowCard = cards.get(cards.getLastIndexOfSuit(s, false));
			if(cards.getSuitCount(s, false)==2 && c.getRank().getRamschOrder()<6 && lowCard.getRank().getRamschOrder()<2) {
				resultIndex = cards.getIndexOf(c);
			}
		}
		if(resultIndex>=0) {
			Card result = cards.get(resultIndex);
			log.debug("Playing single (or high double) suit card: "+result+" of "+cards);
			return result;
		}
		
		int jack = Rank.JACK.toBinaryFlag();
		if((playedCards[0]&jack)+(playedCards[1]&jack)+(playedCards[2]&jack)+(playedCards[3]&jack)==0) {
			log.debug("no jack played yet - trying it myself");
			if(cards.get(0).getRank()==Rank.JACK && cards.get(0).getSuit().ordinal()>1 && cards.get(1).getRank()!=Rank.JACK) return cards.get(0);
		}

		// check best card, if there are no "easy" suits
		for(Card c: cards) {
			if(c.getRamschOrder()==1) resultIndex = cards.getIndexOf(c);
			else if(c.getRamschOrder()==0 && resultIndex<0) resultIndex = cards.getIndexOf(c);
		}
		if(resultIndex>=0) return cards.get(resultIndex);
		return cards.get(cards.size() - 1);
	}

	private Card playMiddlehandCard() {
		log.debug("I (" + myPlayer.getPlayerName() + ") am in middlehand (OpponentPlayer)");
		CardList cards = knowledge.getMyCards();
		Card initialCard = knowledge.getTrickCards().get(0);
		GameType gameType = knowledge.getGameType();
//		Card result = null;

		// fallback: get last valid card
		return getDefaultCard(cards, initialCard, gameType);
	}

	private Card playRearhandCard() {
		log.debug("I (" + myPlayer.getPlayerName() + ") am in rearhand (OpponentPlayer)");
		CardList cards = knowledge.getMyCards();
		Card initialCard = knowledge.getTrickCards().get(0);
		GameType gameType = knowledge.getGameType();
//		Card result = null;
		// fallback: take the first valid card
		return getDefaultCard(cards, initialCard, gameType);
	}

	/**
	 * Gets a fallback card, if no other algorithm returned a card
	 * 
	 * @param cards
	 * @param initialCard
	 * @param gameType
	 * @return a default card
	 */
	private Card getDefaultCard(CardList cards, Card initialCard,
			GameType gameType) {
		Card result = null;
		for (Card c : cards) {
			if (c.isAllowed(gameType, initialCard, cards)) {
				result = c;
			}
		}
		if (result != null) {
			log.debug("playCard (8)");
			return result;
		}
		log.warn("no possible card found in card list [" + cards + "] with " + gameType + " / " + initialCard);
		log.debug("playCard (9)");
		return cards.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jskat.ai.algorithmic.IAlgorithmicAIPlayer#discardSkat(org.jskat.ai
	 * .algorithmic.BidEvaluator)
	 */
	@Override
	public CardList discardSkat(BidEvaluator bidEvaluator) {
		log.debug(myPlayer.getPlayerName()+" ("+this.getClass()+") is discarding cards");
		knowledge.getMyCards().sort(GameType.RAMSCH);
		if(JSkatOptions.instance().isSchieberRamschJacksInSkat()) {
			return discardWithJacks();
		}
		return discardNoJacks();
	}
	
	private CardList discardWithJacks() {
		log.debug("cards left before discarding(withJacks): "+knowledge.getMyCards().size()+" - "+knowledge.getMyCards());
		CardList result = new CardList();
		CardList cards = knowledge.getMyCards();
		if(cards.get(0).getRank()==Rank.JACK && cards.get(0).getSuit()==Suit.CLUBS || cards.get(0).getSuit()==Suit.SPADES) {
			result.add(knowledge.getMyCards().remove(0));
		}
		if(cards.get(0).getRank()==Rank.JACK && cards.get(0).getSuit()==Suit.SPADES) {
			result.add(knowledge.getMyCards().remove(0));
		}
		if(result.size()==2) return result;
		result.addAll(discardNoJacks());
		while(result.size()>2) {
			cards.add(result.remove(result.size()-1));
		}
		log.debug("cards left after discarding(withJacks): "+knowledge.getMyCards().size()+" - "+knowledge.getMyCards());
		return result;
	}
	
	private CardList discardNoJacks() {
		log.debug("cards left before discarding(noJacks): "+knowledge.getMyCards().size()+" - "+knowledge.getMyCards());
		CardList result = new CardList();
		for(Card c: knowledge.getMyCards()) {
			if(result.size()<2 && c.getRank()==Rank.ACE) {
				result.add(c);
			}
			else if(result.size()==2 && c.getRank()==Rank.ACE) {
				int len = knowledge.getMyCards().getSuitCount(c.getSuit(), false);
				int len0 = knowledge.getMyCards().getSuitCount(result.get(0).getSuit(), false);
				int len1 = knowledge.getMyCards().getSuitCount(result.get(1).getSuit(), false);
				if(len<len0) {
					result.remove(0);
					result.add(c);
				}
				else if(len<len1){
					result.remove(1);
					result.add(c);
				}
			}
		}
		for(Card c: knowledge.getMyCards()) {
			if(result.size()<2 && c.getRank()==Rank.TEN) {
				result.add(c);
			}
			else if(result.size()==2 && c.getRank()==Rank.TEN) {
				int len = knowledge.getMyCards().getSuitCount(c.getSuit(), false);
				int len0 = knowledge.getMyCards().getSuitCount(result.get(0).getSuit(), false);
				int len1 = knowledge.getMyCards().getSuitCount(result.get(1).getSuit(), false);
				if(len<len0) {
					result.remove(0);
					result.add(c);
				}
				else if(len<len1){
					result.remove(1);
					result.add(c);
				}
			}
		}
		for(Card c: knowledge.getMyCards()) {
			if(result.size()<2 && c.getRank()==Rank.KING) {
				result.add(c);
			}
		}
		for(Card c: knowledge.getMyCards()) {
			if(result.size()<2 && c.getRank()!=Rank.JACK) {
				result.add(c);
			}
		}
		knowledge.getMyCards().removeAll(result);
		log.debug("cards left after discarding(noJacks): "+knowledge.getMyCards().size()+" - "+knowledge.getMyCards());
		return result;
		
	}
	
}
