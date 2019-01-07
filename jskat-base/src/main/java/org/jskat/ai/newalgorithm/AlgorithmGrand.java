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

import java.util.ArrayList;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmGrand extends AbstractAlgorithmAI {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmGrand.class);

	AlgorithmGrand(final AlgorithmAI p, final GameType pGameType) {
		super(p, pGameType);

		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as "
				+ this.getClass().getName());
	}

	@Override
	protected Card startGame() {
		log.debug("Grand starts Game: " + knowledge.getPlayerPosition());

		return playStartGameCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	protected Card playForehandCard() {
		log.debug("Grand plays Forehand-Card: " + knowledge.getPlayerPosition());

		return playForehandCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	protected Card playMiddlehandCard() {
		log.debug("Grand plays Middlehand-Card: "
				+ knowledge.getPlayerPosition());

		return playMiddlehandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	protected Card playRearhandCard() {
		log.debug("Grand plays Rearhand-Card: " + knowledge.getPlayerPosition());

		return playRearhandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	public CardList discardSkat(final BidEvaluator pBid) {
		log.debug("discardSkat");

		final CardList tDiscardCards = discardSkatCards(pBid, knowledge.getOwnCards());
		// knowledge.removeOwnCards(tDiscardCards.getImmutableCopy());

		// handle wrong discarding
		while (tDiscardCards.get(0).equals(tDiscardCards.get(1))) {
			tDiscardCards.remove(1);
			tDiscardCards.add(knowledge.getOwnCards().get(
					random.nextInt(knowledge.getOwnCards().size())));
		}

		final CardList cardsAfterDiscarding = new CardList(knowledge.getOwnCards());
		cardsAfterDiscarding.removeAll(tDiscardCards);
		oSituation.setCardsAfterDiscarding(cardsAfterDiscarding);

		return tDiscardCards;
	}

	// static methods for creating JUnit-tests and test cardplaybehavior
	public static Card playStartGameCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation) {
		pCards.sort(pSituation.getGameType());

		if (Helper.countJacks(pCards) == 4) {
			return pCards.get((int) (Math.random() * 3 + 1));
		} else if (pCards.contains(Card.CJ)
				&& (pCards.contains(Card.HJ) || pCards.contains(Card.DJ))) {
			return pCards.get(0);
		} else if (pCards.get(1) == Card.SJ) {
			return pCards.get(1);
		} else if (pCards.get(0) == Card.SJ) {
			return pCards
					.get((int) (Math.random() * Helper.countJacks(pCards)));
		}

		return playForehandCard(pCards, pTrickCards, pPlayedCards,
				pNotOpponentCards, pSituation);
	}

	public static Card playForehandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation) {
		pCards.sort(pSituation.getGameType());

		final CardList possibleCards = new CardList();

		// Count Aces
		int tAcesCount = 0;
		if (pCards.contains(Card.CA)) {
			tAcesCount++; // Clubs
		}
		if (pCards.contains(Card.SA)) {
			tAcesCount++; // Spades
		}
		if (pCards.contains(Card.HA)) {
			tAcesCount++; // Hearts
		}
		if (pCards.contains(Card.DA)) {
			tAcesCount++; // Diamonds
		}

		// Count 10Duos (10 mit niedriger Karte)
		int t10DuoCount = 0;
		if (!pCards.contains(Card.CA) && pCards.contains(Card.CT)
				&& pCards.getSuitCount(Suit.CLUBS, false) > 1) {
			t10DuoCount++; // Clubs
		}
		if (!pCards.contains(Card.SA) && pCards.contains(Card.ST)
				&& pCards.getSuitCount(Suit.SPADES, false) > 1) {
			t10DuoCount++; // Spades
		}
		if (!pCards.contains(Card.HA) && pCards.contains(Card.HT)
				&& pCards.getSuitCount(Suit.HEARTS, false) > 1) {
			t10DuoCount++; // Hearts
		}
		if (!pCards.contains(Card.DA) && pCards.contains(Card.DT)
				&& pCards.getSuitCount(Suit.DIAMONDS, false) > 1) {
			t10DuoCount++; // Diamonds
		}

		// Wenn nur noch Buben und eine weitere Karte -> weitere Karte wird
		// zuletzt gespielt
		if (pCards.size() == Helper.countJacks(pCards) + 1) {
			return pCards.get(0);
		} else if (Helper.countJacks(pNotOpponentCards) == 3
				&& pCards.get(0).getRank() == Rank.JACK) {
			// Wenn schlagbar
			if (pCards.get(0).getSuit() == Suit.CLUBS
					|| pCards.get(0).getSuit() == Suit.SPADES
							&& pPlayedCards.contains(Card.CJ)
					|| pCards.get(0).getSuit() == Suit.HEARTS
							&& pPlayedCards.contains(Card.CJ)
							&& pPlayedCards.contains(Card.SJ)) {
				return pCards.get(0);
			}
			// Wenn nicht schlagbar aber 4 Asse oder 10Duo
			if (tAcesCount + t10DuoCount + pSituation.getBlankSuits().size() == 4) {

				// lange Suit spielen, bis gestochen wird (in 2 von 3 Faellen
				// wird lange Farbe gespielt)
				if (pSituation.getRandomInt() != 0
						&& pCards.getSuitCount(pSituation.getLongestSuit(),
								false) > 0) {
					// Wenn hoechste Karte der Farbe vorhanden ist
					// UND bei den Gegnern noch mindestens 2 Karten der Farbe
					// UND beide Gegner sind noch nicht Blank auf der Farbe
					// (Wenn sie keine Truempfe mehr besitzen wird auch mit
					// niedrigen Karten die Farbe leer gespielt)
					if (Helper.isHighestSuitCard(
							pCards.get(pCards.getFirstIndexOfSuit(
									pSituation.getLongestSuit(), false)),
							pPlayedCards, pTrickCards)
							&& 7 - pNotOpponentCards.getSuitCount(
									pSituation.getLongestSuit(), false) >= 2
							&& !pSituation.isLeftPlayerBlankOnColor(pSituation
									.getLongestSuit())
							&& !pSituation.isRightPlayerBlankOnColor(pSituation
									.getLongestSuit())) {
						return pCards.get(pCards.getFirstIndexOfSuit(
								pSituation.getLongestSuit(), false));
					}
					return pCards.get(pCards.getLastIndexOfSuit(
							pSituation.getLongestSuit(), false));
				}
				// erst alle Karten spielen, wo die zweite Karte anschlieÃƒÅ¸end
				// die beste ist
				for (final Suit lSuit : Suit.values()) {
					if (pCards.getSuitCount(lSuit, false) > 1) {
						final Card possibleHighCard = pCards.get(pCards
								.getFirstIndexOfSuit(lSuit, false));
						final Card possibleSecondCard = pCards.get(pCards
								.getFirstIndexOfSuit(lSuit, false) + 1);

						final CardList tAfterHighCard = new CardList(pPlayedCards);
						tAfterHighCard.add(possibleHighCard);

						if (Helper.isHighestSuitCard(possibleHighCard, null,
								pPlayedCards, pTrickCards)
								&& Helper.isHighestSuitCard(possibleSecondCard,
										null, tAfterHighCard, pTrickCards)) {
							return possibleHighCard;
						}
					}
				}
			}
		}
		// Wenn nicht schlagbar aber selbst noch 2 Buben
		else if (Helper.countJacks(pNotOpponentCards) == 3
				&& pCards.get(1).getRank() == Rank.JACK) {
			return pCards.get(0);
		}
		// Wenn keine Buben mehr beim Gegner
		else if (Helper.countJacks(pNotOpponentCards) == 4) {
			for (final Suit lSuit : Suit.values()) {
				final int suitCount = pCards.getSuitCount(lSuit, false);
				if (suitCount == 0) {
					continue;
				}
				final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
						lSuit, false));
				final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(
						lSuit, false));

				if (Helper.isHighestSuitCard(possibleHighCard,
						pSituation.getGameType(), pPlayedCards, pTrickCards)
						&& !(suitCount == 2 && possibleLowCard.getPoints() == 0)) {
					possibleCards.add(possibleHighCard);
				}
			}
		}

		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}

		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static Card playMiddlehandCard(final CardList pCards,
			final CardList pTrickCards, final CardList pPlayedCards,
			final CardList pNotOpponentCards, final Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		final Card tCardToBeat = pTrickCards.get(0);
		final Suit tSuit = tCardToBeat.getSuit();

		CardList possibleCards = new CardList();

		// Wenn ein Bube aufgespielt wurde
		if (tCardToBeat.getRank() == Rank.JACK
				&& pCards.get(0).getRank() == Rank.JACK) {
			if (pCards.get(0).beats(pSituation.getGameType(), tCardToBeat)) {
				return getLowestBeatingCard(pCards, pSituation.getGameType(),
						tCardToBeat);
			}
			return pCards.get(Helper.countJacks(pCards) - 1);
		}
		// Wenn Karte bedient werden kann/muss
		if (pCards.getSuitCount(tSuit, false) > 0) {
			// Wenn schlagbar
			if (Helper.isHighestSuitCard(
					pCards.get(pCards.getFirstIndexOfSuit(tSuit, false)),
					pPlayedCards, pTrickCards)) {
				return pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));
			}
			// Wenn nicht schlagbar
			return pCards.get(pCards.getLastIndexOfSuit(tSuit, false));
		}
		// Wenn Farbe blank ist
		// Wenn >= 10 Punkte im Stich && (kein Bube beim Gegner || CJ und noch
		// mindestens ein weiterer Bube)
		if (tCardToBeat.getPoints() >= 10
				&& (Helper.countJacks(pNotOpponentCards) == 4 || Helper
						.countJacks(pNotOpponentCards) >= 2
						&& pCards.contains(Card.CJ)
						&& pCards.get(1).getRank() == Rank.JACK)) {
			return pCards.get(Helper.countJacks(pCards) - 1);
		}

		possibleCards = getPossibleMaxValueCards(pCards, 0);
		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}
		possibleCards = getPossibleMaxValueCards(pCards, 3);
		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}
		possibleCards = getPossibleMaxValueCards(pCards, 4);
		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}

		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static Card playRearhandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		final Card tForehandCard = pTrickCards.get(0);
		final Suit tSuit = tForehandCard.getSuit();
		final Card tMiddlehandCard = pTrickCards.get(1);
		CardList possibleCards = new CardList();

		Card tCardToBeat = tForehandCard;
		if (tMiddlehandCard.beats(pSituation.getGameType(), tCardToBeat)) {
			tCardToBeat = tMiddlehandCard;
		}

		// Wenn Vorhand-Karte bedient werden kann/muss
		if (pCards.getSuitCount(tSuit, false) > 0) {
			final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
					tSuit, false)); // highest Card
			int possibleBeatingCardIndex = pCards.getLastIndexOfSuit(tSuit,
					false); // lowest Card

			if (pCards.getSuitCount(tSuit, false) == 1) {
				return possibleHighCard;
			}

			// Wenn schlagbar
			if (possibleHighCard.beats(pSituation.getGameType(), tCardToBeat)) {
				while (!pCards.get(possibleBeatingCardIndex).beats(
						pSituation.getGameType(), tCardToBeat)) {
					possibleBeatingCardIndex--;
				}
				return pCards.get(possibleBeatingCardIndex);
			}
			// Wenn nicht schlagbar
			return pCards.get(pCards.getLastIndexOfSuit(tSuit, false));
		}
		// Wenn Farbe blank ist
		else {
			// Wenn >= 10 Punkte im Stich && (kein Bube beim Gegner || CJ und
			// noch mindestens ein weiterer Bube)
			if (tForehandCard.getPoints() + tMiddlehandCard.getPoints() >= 10
					&& (Helper.countJacks(pNotOpponentCards) == 4 || Helper
							.countJacks(pNotOpponentCards) >= 2
							&& pCards.contains(Card.CJ)
							&& pCards.get(1).getRank() == Rank.JACK)) {
				return pCards.get(Helper.countJacks(pCards) - 1);
			}
			// Wenn < 10 -> Farbe abwerfen
			else {
				possibleCards = getPossibleMaxValueCards(pCards, 0);
				if (!possibleCards.isEmpty()) {
					return playRandomCard(possibleCards);
				}
				possibleCards = getPossibleMaxValueCards(pCards, 3);
				if (!possibleCards.isEmpty()) {
					return playRandomCard(possibleCards);
				}
				possibleCards = getPossibleMaxValueCards(pCards, 4);
				if (!possibleCards.isEmpty()) {
					return playRandomCard(possibleCards);
				}
			}
		}

		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static CardList discardSkatCards(final BidEvaluator pBid,
			final CardList pOwnCards) {
		final CardList tCards = new CardList(pOwnCards);
		tCards.sort(GameType.GRAND);

		CardList tDiscardCards = new CardList();
		final CardList t1ToDiscard = new CardList();
		final ArrayList<CardList> t2ToDiscard = new ArrayList<CardList>();
		final CardList t1PossibleDiscard = new CardList();
		final ArrayList<CardList> t2PossibleDiscard = new ArrayList<CardList>();

		for (final Suit lSuit : Suit.values()) {
			if (tCards.getSuitCount(lSuit, false) == 0) {
				continue;
			}

			final int lFirstIndex = tCards.getFirstIndexOfSuit(lSuit, false);
			final int lLastIndex = tCards.getLastIndexOfSuit(lSuit, false);
			final Card lFirstCard = tCards.get(lFirstIndex);
			final Card lLastCard = tCards.get(lLastIndex);

			// Wenn nur eine Karte der Farbe und diese ist nicht das Ass
			if (lFirstIndex == lLastIndex) {
				if (lFirstCard.getRank() != Rank.ACE) {
					// Wenn sie Punkte bringt dann druecken
					if (lFirstCard.getPoints() != 0) {
						t1ToDiscard.add(lFirstCard);
					} else {
						tDiscardCards.add(lFirstCard);
					}
				}
			}
			// Wenn der Spieler 2 Karten der Farbe auf der Hand hat
			else if (lLastIndex - lFirstIndex == 1) {
				// Wenn die hohe Karte das Ass ist
				if (lFirstCard.getRank() == Rank.ACE) {
					// Wenn die niedrige Karte die 10 oder der K ist
					if (tCards.get(lFirstIndex + 1).getRank() == Rank.TEN
							|| tCards.get(lFirstIndex + 1).getRank() == Rank.KING) {
						continue;
					} else {
						t1ToDiscard.add(lLastCard);
					}
				}
				// Wenn die hohe Karte die 10 ist
				else if (lFirstCard.getRank() == Rank.TEN) {
					continue;
				}
			}
			// Wenn der Spieler >=3 Karten der Farbe auf der Hand hat
			else {
				if (lFirstCard.getRank() == Rank.ACE) {
					// Wenn A, 10, K -> A und 10 druecken
					if (lLastCard.getRank() == Rank.KING) {
						final CardList t = new CardList();
						t.add(lFirstCard);
						t.add(tCards.get(lFirstIndex + 1));
						t2ToDiscard.add(t);
						t1PossibleDiscard.add(lFirstCard);
					}
					// Wenn zweite Karte eine 10 ->
					else if (tCards.get(lFirstIndex + 1).getRank() == Rank.TEN) {
						t1ToDiscard.add(lFirstCard);
					} else {
						final CardList t = new CardList();
						t.add(lFirstCard);
						t.add(tCards.get(lFirstIndex + 1));
						t2PossibleDiscard.add(t);
						t1PossibleDiscard.add(lFirstCard);
					}
				} else if (lFirstCard.getRank() == Rank.TEN) {
					// Wenn 10, K -> 10 druecken
					if (lLastCard.getRank() == Rank.KING) {
						t1ToDiscard.add(lFirstCard);
					} else if (tCards.get(lFirstIndex + 1).getRank() == Rank.QUEEN) {
						t1ToDiscard.add(tCards.get(lFirstIndex + 1));
					} else {
						t1PossibleDiscard.add(lLastCard);
					}
				}
				final CardList t = new CardList();
				t.add(lFirstCard);
				t.add(tCards.get(lFirstIndex + 1));
				t2PossibleDiscard.add(t);
			}
		}

		// Wenn 2x blank moeglich
		while (tDiscardCards.size() > 2) {
			tDiscardCards.remove((int) (Math.random() * tDiscardCards.size()));
		}

		if (tDiscardCards.size() == 0) {
			for (final Card lCardList : t1ToDiscard) {
				tDiscardCards.add(lCardList);
			}
			for (final Card lCardList : t1PossibleDiscard) {
				tDiscardCards.add(lCardList);
			}
			while (tDiscardCards.size() > 2) {
				tDiscardCards.remove(tDiscardCards.size() - 1);
			}
		}

		if (tDiscardCards.size() == 1) {
			if (!t1ToDiscard.isEmpty()) {
				tDiscardCards
						.add(t1ToDiscard.get((int) (Math.random() * t1ToDiscard
								.size())));
			}
			if (tDiscardCards.size() != 2 && !t1PossibleDiscard.isEmpty()) {
				tDiscardCards
						.add(t1PossibleDiscard.get((int) (Math.random() * t1PossibleDiscard
								.size())));
			}
		}

		if (tDiscardCards.size() != 2 && !t2ToDiscard.isEmpty()) {
			tDiscardCards = t2ToDiscard.get((int) (Math.random() * t2ToDiscard
					.size()));
		}

		if (tDiscardCards.size() != 2 && !t2PossibleDiscard.isEmpty()) {
			tDiscardCards = t2PossibleDiscard
					.get((int) (Math.random() * t2PossibleDiscard.size()));
		}

		return tDiscardCards;
	}
}