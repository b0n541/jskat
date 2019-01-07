/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.newalgorithm;

import java.util.Random;

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

/**
 * @author Daniel Loreck <br>
 *
 */
public abstract class AbstractAlgorithmAI {
	protected final AlgorithmAI myPlayer;
	protected final ImmutablePlayerKnowledge knowledge;

	protected final static Random random = new Random();

	protected Situation oSituation;
	protected CardList oPlayedCards;
	protected CardList oNotOpponentCards;

	public AbstractAlgorithmAI(final AlgorithmAI p, GameType pGameType) {
		myPlayer = p;
		knowledge = p.getKnowledge();

		oSituation = new Situation(knowledge, pGameType);

		oPlayedCards = new CardList();
		oNotOpponentCards = new CardList();
	}

	public Card playCard() {
		// remove last tricks cards from the list of all opponent cards
		int currentTrick;
		oNotOpponentCards.clear();

		if ((currentTrick = knowledge.getNoOfTricks()) != 0) {
			addTrickToPlayedCards(knowledge.getCompletedTricks().get(
					currentTrick - 1));
		}

		oNotOpponentCards.addAll(oPlayedCards);
		oNotOpponentCards.addAll(knowledge.getOwnCards());
		oNotOpponentCards.addAll(knowledge.getSkat());

		oPlayedCards.sort(oSituation.getGameType());
		oNotOpponentCards.sort(oSituation.getGameType());

		// Wenn nur eine Karte vorhanden ist
		if (knowledge.getOwnCards().size() == 1) {
			return knowledge.getOwnCards().get(0);
		}
		if (knowledge.getTrickCards() == null
				|| knowledge.getTrickCards().isEmpty()) {
			if (knowledge.getNoOfTricks() < 1) {
				return startGame();
			}
			return playForehandCard();
		}
		if (knowledge.getTrickCards().size() == 1) {
			return playMiddlehandCard();
		}
		return playRearhandCard();
	}

	protected abstract Card startGame();

	protected abstract Card playForehandCard();

	protected abstract Card playMiddlehandCard();

	protected abstract Card playRearhandCard();

	public abstract CardList discardSkat(BidEvaluator bidEvaluator);

	protected void addTrickToPlayedCards(Trick pTrick) {
		Suit tTrickSuit = pTrick.getFirstCard().getSuit();

		oPlayedCards.add(pTrick.getFirstCard());

		oPlayedCards.add(pTrick.getSecondCard());
		// Wenn mit der 2. Karte nicht bedient wurde
		// oder 1. Karte nicht Trumpf und 2. Karte ein Bube ist
		if (tTrickSuit != pTrick.getSecondCard().getSuit()
				|| (tTrickSuit != knowledge.getTrumpSuit() && pTrick
						.getSecondCard().getRank() == Rank.JACK)) {
			if (pTrick.getMiddleHand() == knowledge.getPlayerPosition()
					.getLeftNeighbor())
				oSituation.setLeftPlayerBlankColor(tTrickSuit);
			else if (pTrick.getMiddleHand() == knowledge.getPlayerPosition()
					.getRightNeighbor())
				oSituation.setRightPlayerBlankColor(tTrickSuit);
		} else if (tTrickSuit == knowledge.getTrumpSuit()
				&& !(pTrick.getSecondCard().getSuit() == tTrickSuit || pTrick
						.getSecondCard().getRank() == Rank.JACK)) {
			if (pTrick.getMiddleHand() == knowledge.getPlayerPosition()
					.getLeftNeighbor())
				oSituation.setLeftPlayerBlankOnTrump();
			else if (pTrick.getMiddleHand() == knowledge.getPlayerPosition()
					.getRightNeighbor())
				oSituation.setRightPlayerBlankOnTrump();
		}

		oPlayedCards.add(pTrick.getThirdCard());
		// Wenn mit der 3. Karte nicht bedient wurde
		// oder 1. Karte nicht Trumpf und 3. Karte ein Bube ist
		if (tTrickSuit != pTrick.getThirdCard().getSuit()
				|| (tTrickSuit != knowledge.getTrumpSuit() && pTrick
						.getThirdCard().getRank() == Rank.JACK)) {
			if (pTrick.getRearHand() == knowledge.getPlayerPosition()
					.getLeftNeighbor())
				oSituation.setLeftPlayerBlankColor(tTrickSuit);
			else if (pTrick.getRearHand() == knowledge.getPlayerPosition()
					.getRightNeighbor())
				oSituation.setRightPlayerBlankColor(tTrickSuit);
		} else if (tTrickSuit == knowledge.getTrumpSuit()
				&& !(pTrick.getThirdCard().getSuit() == tTrickSuit || pTrick
						.getThirdCard().getRank() == Rank.JACK)) {
			if (pTrick.getRearHand() == knowledge.getPlayerPosition()
					.getLeftNeighbor())
				oSituation.setLeftPlayerBlankOnTrump();
			else if (pTrick.getRearHand() == knowledge.getPlayerPosition()
					.getRightNeighbor())
				oSituation.setRightPlayerBlankOnTrump();
		}

		// Wenn keine Karten mehr beim Gegner sind dann beide blank setzen

		// Wenn Farbspiel
		if ((knowledge.getGameType() == GameType.CLUBS
				|| knowledge.getGameType() == GameType.SPADES
				|| knowledge.getGameType() == GameType.HEARTS || knowledge
					.getGameType() == GameType.DIAMONDS)) {
			// Wenn erste Karte kein Trump war
			if (tTrickSuit != knowledge.getTrumpSuit()
					&& oNotOpponentCards.getSuitCount(tTrickSuit, false) == 7) {
				oSituation.setLeftPlayerBlankColor(tTrickSuit);
				oSituation.setRightPlayerBlankColor(tTrickSuit);
			} else if (tTrickSuit == knowledge.getTrumpSuit()
					&& oNotOpponentCards.getSuitCount(tTrickSuit, false)
							+ Helper.countJacks(oNotOpponentCards) == 11) {
				oSituation.setLeftPlayerBlankOnTrump();
				oSituation.setRightPlayerBlankOnTrump();
			}
		}
		// Wenn Grand-Spiel
		// UND keine Karten dieser Farbe mehr beim Gegner (exkl. Buben)
		else if (knowledge.getGameType() == GameType.GRAND
				&& oNotOpponentCards.getSuitCount(tTrickSuit, false) == 7) {
			oSituation.setLeftPlayerBlankColor(tTrickSuit);
			oSituation.setRightPlayerBlankColor(tTrickSuit);
		}
		// Wenn Null-Spiel
		// UND keine Karten dieser Farbe mehr beim Gegner (inkl. Buben)
		else if (knowledge.getGameType() == GameType.NULL
				&& oNotOpponentCards.getSuitCount(tTrickSuit, true) == 8) {
			oSituation.setLeftPlayerBlankColor(tTrickSuit);
			oSituation.setRightPlayerBlankColor(tTrickSuit);
		}
	}

	// protected boolean isPlayerBlankOnColor(Player pPlayer, Suit pSuit) {
	// if(pPlayer == knowledge.getPlayerPosition().getLeftNeighbor())
	// return oFreeSuitOpponentLeft.contains(pSuit);
	// if(pPlayer == knowledge.getPlayerPosition().getRightNeighbor())
	// return oFreeSuitOpponentRight.contains(pSuit);
	// return false;
	// }

	protected static Card getRandomAllowedCard(final CardList cards,
			final Card initialCard, final GameType gameType) {
		Card result = null;
		for (Card c : cards) {
			if (c.isAllowed(gameType, initialCard, cards)) {
				result = c;
			}
		}
		if (result != null) {
			return result;
		}
		return cards.get(0);
	}

	protected static Card playRandomCard(CardList pCards) {
		return pCards.get((int) (Math.random() * pCards.size()));
	}

	protected static Card getLowValueTrumpCard(CardList pCards, Suit pTrumpSuit) {
		for (int i = pCards.getLastIndexOfSuit(pTrumpSuit); i >= 0; i--) {
			if (pCards.get(i).getPoints() < 10) {
				return pCards.get(i);
			}
		}
		return pCards.get(pCards.getLastIndexOfSuit(pTrumpSuit));
	}

	protected static Card playLowestWinningJack(CardList pCards) {
		if (pCards.contains(Card.SJ)) {
			if (pCards.contains(Card.HJ)) {
				if (pCards.contains(Card.DJ)) {
					return pCards.get(pCards.indexOf(Card.DJ));
				}
				return pCards.get(pCards.indexOf(Card.HJ));
			}
			return pCards.get(pCards.indexOf(Card.SJ));
		}
		return pCards.get(pCards.indexOf(Card.CJ));
	}

	protected static Card playRandomJack(CardList pCards) {
		int tRandom = (int) (Math.random() * Helper.countJacks(pCards));
		return pCards.get(tRandom);
	}

	protected static Card getLowestBeatingCard(CardList pCards,
			GameType pGameType, Card tCardToBeat) {
		int counter = -1;
		if (pGameType != GameType.GRAND)
			counter = pCards.getLastIndexOfSuit(tCardToBeat.getSuit(), false);
		if (counter == -1)
			counter = Helper.countJacks(pCards) - 1;
		// Solange die niedrige Karte die Karte vom Solo-Spieler nicht besiegt,
		// weiter hoch gehen im Ranking der Karten
		while (!pCards.get(counter).beats(pGameType, tCardToBeat))
			counter--;
		return pCards.get(counter);
	}

	protected static CardList getPossibleMaxValueCards(CardList pCards,
			int pMaxCardValue) {
		CardList possibleCards = new CardList();

		for (Suit lSuit : Suit.values()) {
			int suitCount = pCards.getSuitCount(lSuit, false);
			Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
					lSuit, false));
			Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(lSuit,
					false));

			if (suitCount > 0 && possibleHighCard == possibleLowCard
					&& possibleLowCard.getPoints() <= pMaxCardValue) {
				possibleCards.add(possibleLowCard);
			} else if (suitCount > 2
					&& possibleLowCard.getPoints() <= pMaxCardValue) {
				possibleCards.add(possibleLowCard);
			}
		}

		return possibleCards;
	}
}
