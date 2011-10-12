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
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;

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
		return cards.get(cards.size() - 1);
	}

	private Card openTrick() {
		return openGame();
	}

	private Card playMiddlehandCard() {
		log.debug("I (" + myPlayer.getPlayerName()
				+ ") am in middlehand (OpponentPlayer)");
		CardList cards = knowledge.getMyCards();
		Card initialCard = knowledge.getTrickCards().get(0);
		GameType gameType = knowledge.getGameType();
		Card result = null;

		// fallback: get last valid card
		return getDefaultCard(cards, initialCard, gameType);
	}

	private Card playRearhandCard() {
		log.debug("I (" + myPlayer.getPlayerName()
				+ ") am in rearhand (OpponentPlayer)");
		// fallback: take the first valid card
		CardList cards = knowledge.getMyCards();
		Card initialCard = knowledge.getTrickCards().get(0);
		GameType gameType = knowledge.getGameType();
		Card result = null;
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
		log.warn("no possible card found in card list [" + cards + "] with "
				+ gameType + " / " + initialCard);
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
		throw new IllegalStateException("ramsch player cannot discard a skat");
	}
}
