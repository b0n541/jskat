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

public class AlgorithmSuit extends AbstractAlgorithmAI {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmSuit.class);

	AlgorithmSuit(final AlgorithmAI p, final GameType pGameType) {
		super(p, pGameType);

		log.debug(String.format("/s is %s", myPlayer.getPlayerName(), this
				.getClass().getName()));
	}

	@Override
	protected Card startGame() {
		log.debug("Suit-Declarer starts Game: "
				+ knowledge.getCurrentTrick().getForeHand());

		return playStartGameCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	protected Card playForehandCard() {
		log.debug("Suit-Declarer plays Forehand-Card: "
				+ knowledge.getCurrentTrick().getForeHand());

		return playForehandCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	protected Card playMiddlehandCard() {
		log.debug("Suit-Declarer plays Middlehand-Card: "
				+ knowledge.getCurrentTrick().getMiddleHand());

		return playMiddlehandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	protected Card playRearhandCard() {
		log.debug("Suit-Declarer plays Rearhand-Card: "
				+ knowledge.getCurrentTrick().getRearHand());

		return playRearhandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation);
	}

	@Override
	public CardList discardSkat(final BidEvaluator pBid) {
		log.debug("discardSkat");

		final CardList tDiscardCards = discardSkatCards(pBid, knowledge.getOwnCards());
		// knowledge.removeOwnCards(tDiscardCards);

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
		// pull trump cards
		if (pCards.contains(Card.CJ)) {
			return playLowestWinningJack(pCards);
		}

		final int tRandom = (int) (Math.random() * 4);
		if (tRandom < 3 && Helper.countJacks(pCards) > 0) {
			return playRandomJack(pCards);
		}

		// Count Aces
		int tAcesCount = 0;
		if (pSituation.getTrumpSuit() != Suit.CLUBS && pCards.contains(Card.CA)) {
			tAcesCount++; // Clubs
		}
		if (pSituation.getTrumpSuit() != Suit.SPADES
				&& pCards.contains(Card.SA)) {
			tAcesCount++; // Spades
		}
		if (pSituation.getTrumpSuit() != Suit.HEARTS
				&& pCards.contains(Card.HA)) {
			tAcesCount++; // Hearts
		}
		if (pSituation.getTrumpSuit() != Suit.DIAMONDS
				&& pCards.contains(Card.DA)) {
			tAcesCount++; // Diamonds
		}

		if (tAcesCount > 1) {
			return getLowValueTrumpCard(pCards, pSituation.getTrumpSuit());
		}

		return playForehandCard(pCards, pTrickCards, pPlayedCards,
				pNotOpponentCards, pSituation);
	}

	public static Card playForehandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		final CardList possibleCards = new CardList();

		// Spiele die hoechsten Truempfe
		if (pCards.contains(Card.CJ)) {
			return playLowestWinningJack(pCards);
		}

		// Wenn beide Gegner noch Truempfe haben
		// und der Spieler noch die hoechsten Truempfe besitzt
		if (pNotOpponentCards.getSuitCount(pSituation.getTrumpSuit(), false)
				+ Helper.countJacks(pNotOpponentCards) < 10) {
			// Wenn ein Bube vorhanden dann spielen
			if (Helper.countJacks(pNotOpponentCards) == 4
					&& (Helper.countJacks(pCards) > 0 || Helper
							.isHighestTrumpCard(pCards.get(0),
									pSituation.getGameType(), pPlayedCards))) {
				return pCards.get(0);
			}
			// Wenn der Spieler die hoechste Trumpfkarte besitzt und der Gegner
			// noch Trumpfkarten hat
			// oder alles bis auf eine Karte sind noch Trumpfkarten in der Hand
			// des Spielers
			if (Helper.isHighestTrumpCard(pCards.get(0),
					pSituation.getGameType(), pPlayedCards)
					&& (pNotOpponentCards.getSuitCount(
							pSituation.getTrumpSuit(), false) != 7
							|| Helper
									.countJacks(pCards)
									+ pCards.getSuitCount(pSituation.getTrumpSuit(),
											false) >= pCards.size() - 1)) {
				return pCards.get(0);
			}
			// sonst niedrigste Trumpfkarte zu den moeglichen Karten hinzufuegen
			// MÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¶glichkeit der Karte erhoehen. 2x hinzufuegen
			possibleCards.add(pCards.get(pCards.getLastIndexOfSuit(pSituation
					.getTrumpSuit())));
			possibleCards.add(pCards.get(pCards.getLastIndexOfSuit(pSituation
					.getTrumpSuit())));
		}
		// Wenn ein Gegner noch Truempfe besitzt
		else if (pNotOpponentCards.getSuitCount(pSituation.getTrumpSuit(),
				false) + Helper.countJacks(pNotOpponentCards) == 10) {
			possibleCards.add(pCards.get(pCards.getLastIndexOfSuit(pSituation
					.getTrumpSuit())));
		}

		// Farbe (!= Trumpf) spielen
		for (final Suit s : Suit.values()) {
			if (s == pSituation.getTrumpSuit()
					|| pCards.getSuitCount(s, false) == 0) {
				continue;
			}

			final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(s,
					false)); // highest Card
			final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(s,
					false)); // lowest Card

			if (possibleHighCard.equals(possibleLowCard)) {
				possibleCards.add(possibleLowCard);
			}

			// Wenn der Spieler das Ass hat und weniger als 3 Karten dieser
			// Farbe gespielt wurden
			// oder der Spieler die hoechste Karte der Farbe hat und kein Trumpf
			// beim Gegner existiert
			if (possibleHighCard.getRank() == Rank.ACE
					&& pPlayedCards.getSuitCount(s, false) < 3
					|| Helper.isHighestSuitCard(possibleHighCard, pPlayedCards,
							pTrickCards)
							&& Helper.countJacks(pNotOpponentCards)
									+ pNotOpponentCards.getSuitCount(
											pSituation.getTrumpSuit(), false) == 11) {
				return possibleHighCard;
			}

			// Wenn eine hoehere Karte beim Gegner ist, als die eigene Hoechste
			if (!Helper.isHighestSuitCard(possibleHighCard,
					pSituation.getGameType(), pPlayedCards, pTrickCards)) {
				possibleCards.add(possibleLowCard);
			}
		}

		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}

		// Wenn der Spieler die restligen Truempfe hat
		if (pCards.hasTrump(pSituation.getGameType())
				&& pCards.size()
						+ pPlayedCards.getSuitCount(pSituation.getTrumpSuit(),
								false)
						+ Helper.countJacks(pPlayedCards) == 11) {
			return pCards.get(0);
		}

		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static Card playMiddlehandCard(final CardList pCards,
			final CardList pTrickCards, final CardList pPlayedCards,
			final CardList pNotOpponentCards, final Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		final Card tForehandCard = pTrickCards.get(0);
		final CardList possibleCards = new CardList();

		// Wenn Trumpfgespielt wurde
		if (tForehandCard.getRank() == Rank.JACK
				|| tForehandCard.getSuit() == pSituation.getTrumpSuit()) {
			if (tForehandCard.getPoints() >= 10) {
				return pCards.get(0);
			} else if (tForehandCard.getPoints() != 0
					&& Helper.isHighestTrumpCard(pCards.get(0),
							pSituation.getGameType(), pPlayedCards)) {
				return pCards.get(0);
			} else if ((!pNotOpponentCards.contains(Card.getCard(
					pSituation.getTrumpSuit(), Rank.ACE)) || !pNotOpponentCards
							.contains(Card.getCard(pSituation.getTrumpSuit(), Rank.TEN)))
					&& pCards.get(0).getRank() == Rank.JACK) {
				return pCards.get(0);
			}
			return pCards.get(pCards.getLastIndexOfSuit(
					pSituation.getTrumpSuit(), false));
		}

		// Wenn andere Farbe gelegt wurde
		final int tSuitCount = pCards.getSuitCount(tForehandCard.getSuit(), false);
		if (tSuitCount == 1) {
			return pCards.get(pCards.getFirstIndexOfSuit(
					tForehandCard.getSuit(), false));
		} else if (tSuitCount > 1) {
			final Card lCard = pCards.get(pCards.getFirstIndexOfSuit(
					tForehandCard.getSuit(), false));
			if (lCard.beats(pSituation.getGameType(), tForehandCard)
					&& Helper.isHighestSuitCard(lCard, pPlayedCards,
							pTrickCards)
					&& !pSituation.isLeftPlayerBlankOnColor(tForehandCard
							.getSuit())) {
				return lCard;
			}
			return pCards.get(pCards.getLastIndexOfSuit(
					tForehandCard.getSuit(), false));
		}

		// FOREHAND-Karte kann nicht bedient werden
		// Wenn die Karte >= 10 Punkte wert ist und der Spieler eine Trumpfkarte
		// hat
		if (tForehandCard.getPoints() >= 10
				&& pCards.hasTrump(pSituation.getGameType())) {
			if (Helper.countJacks(pCards) > 0) {
				return pCards.get(Helper.countJacks(pCards) - 1);
			}
			if (Helper.isHighestSuitCard(pCards.get(0),
					pSituation.getGameType(), pPlayedCards, pTrickCards)) {
				return pCards.get(0);
			}
			return pCards.get(pCards.getLastIndexOfSuit(pSituation
					.getTrumpSuit()));
		}

		for (final Suit s : Suit.values()) {
			if (s == pSituation.getTrumpSuit()
					|| pCards.getSuitCount(s, false) == 0) {
				continue;
			}

			final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(s,
					false)); // highest Card
			final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(s,
					false)); // lowest Card

			// Wenn nur eine Karte dieser Farbe, die zudem <= 4 Punkte Wert ist
			// und eine Karte mit Wert >= 10 noch im Spiel ist -> abwerfen
			if (pCards.getSuitCount(s, false) == 1
					&& possibleHighCard.getPoints() <= 4
					&& (!pNotOpponentCards.contains(Card.getCard(s, Rank.ACE)) || !pNotOpponentCards
							.contains(Card.getCard(s, Rank.TEN)))) {
				return possibleHighCard;
			} else if (pCards.getSuitCount(s, false) > 2) {
				possibleCards.add(possibleLowCard);
			} else if (pCards.getSuitCount(s, false) == 2) {
				// Wenn A, 10
				if (possibleLowCard.getRank() == Rank.TEN) {
					continue;
				} else if (possibleHighCard.getRank() == Rank.ACE
						&& possibleLowCard.getPoints() <= 3) {
					possibleCards.add(possibleLowCard);
				} else if (Helper.isHighestSuitCard(possibleHighCard,
						pSituation.getGameType(), pPlayedCards, pTrickCards)) {
					possibleCards.add(possibleLowCard);
				}

			}
		}

		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}

		// Wenn Hinterhand auf der Farbe nicht blank ist
		// erst ein Stich in der Farbe gespielt wurde
		if (pCards.hasTrump(pSituation.getGameType())) {
			if (!pSituation.isLeftPlayerBlankOnColor(tForehandCard.getSuit())
					|| pPlayedCards
							.getSuitCount(tForehandCard.getSuit(), false) < 4) {
				return Helper.getHighestValueCard(pCards,
						pSituation.getTrumpSuit(), true);
			}
			return pCards.get(pCards.getLastIndexOfSuit(
					pSituation.getTrumpSuit(), false));
		}

		return getRandomAllowedCard(pCards, tForehandCard,
				pSituation.getGameType());
	}

	public static Card playRearhandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation) {
		pCards.sort(pSituation.getGameType());
		final Card tForehandCard = pTrickCards.get(0);
		final Card tMiddlehandCard = pTrickCards.get(1);
		final Suit tSuit = tForehandCard.getSuit();
		final CardList possibleCards = new CardList();

		Card tCardToBeat = tForehandCard;
		if (tMiddlehandCard.beats(pSituation.getGameType(), tCardToBeat)) {
			tCardToBeat = tMiddlehandCard;
		}

		// Wenn mit einem Trumpf eroeffnet wurde
		if (tForehandCard.getRank() == Rank.JACK
				|| tForehandCard.getSuit() == pSituation.getTrumpSuit()) {

			// Wenn >= 7 Punkte
			if (tMiddlehandCard.getPoints() + tForehandCard.getPoints() >= 7) {
				// Wenn der Spieler eine hoehere Karte hat
				if (Helper.getTrumpCardsToBinary(pCards,
						pSituation.getTrumpSuit()) > tCardToBeat.getRank()
								.toBinaryFlag()) {
					// Spiele den niedrigsten hoeheren Trumpf
					for (int i = pCards.getLastIndexOfSuit(pSituation
							.getTrumpSuit()); i >= 0; i--) {
						if (pCards.get(i).beats(pSituation.getGameType(),
								tCardToBeat)) {
							return pCards.get(i);
						}
					}
				}
			}
			// Wenn der Spieler das Trumpf-Ass hat und mit dem Ass gewinnt
			if (pCards.contains(Card.getCard(pSituation.getTrumpSuit(),
					Rank.ACE))
					&& Card.getCard(pSituation.getTrumpSuit(), Rank.ACE).beats(
							pSituation.getGameType(), tCardToBeat)) {
				return Card.getCard(pSituation.getTrumpSuit(), Rank.ACE);
			}
			// Wenn der Spieler die Trumpf-10 hat und mit dem Ass gewinnt
			if (pCards.contains(Card.getCard(pSituation.getTrumpSuit(),
					Rank.TEN))
					&& Card.getCard(pSituation.getTrumpSuit(), Rank.TEN).beats(
							pSituation.getGameType(), tCardToBeat)) {
				return Card.getCard(pSituation.getTrumpSuit(), Rank.TEN);
			}

			return getLowValueTrumpCard(pCards, pSituation.getTrumpSuit());
		}

		// Wenn kein Trumpf gespielt wurde
		// Wenn der Spieler nur noch eine Karte der Farbe hat
		if (pCards.getSuitCount(tSuit, false) == 1) {
			return pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));
		}
		// Wenn mehrere Karten
		if (pCards.getSuitCount(tSuit, false) > 1) {

			// Wenn die 2. Karte eine Trumpfkarte ist
			if (tCardToBeat.getSuit() == pSituation.getTrumpSuit()) {
				return pCards.get(pCards.getLastIndexOfSuit(tSuit, false));
			}
			// Wenn der Spieler den Stich gewinnen kann
			if (pCards.get(pCards.getFirstIndexOfSuit(tSuit, false)).beats(
					pSituation.getGameType(), tCardToBeat)) {
				return pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));
			}
			// Ansonsten niedrigste Karte legen
			return pCards.get(pCards.getLastIndexOfSuit(tSuit, false));
		}

		// Wenn der Spieler nicht bedienen kann
		// Wenn der Stich mindestens 8 Punkte wert ist
		if (tMiddlehandCard.getPoints() + tForehandCard.getPoints() >= 8) {
			// Wenn der andere Gegner schon mit Trumpf eingegriffen haben sollte
			if (tCardToBeat.getSuit() == pSituation.getTrumpSuit()
					&& Helper.getTrumpCardsToBinary(pCards,
							pSituation.getTrumpSuit()) > tCardToBeat.getRank()
									.toBinaryFlag()) {
				// Spiele den niedrigsten hoeheren Trumpf
				for (int i = pCards.getLastIndexOfSuit(pSituation
						.getTrumpSuit()); i >= 0; i--) {
					if (pCards.get(i).beats(pSituation.getGameType(),
							tCardToBeat)) {
						return pCards.get(i);
					}
				}
			}
			// Kein Trumpf im Spiel
			// Wenn der Spieler das Trumpf-Ass hat und mit dem Ass gewinnt
			if (pCards.contains(Card.getCard(pSituation.getTrumpSuit(),
					Rank.ACE))
					&& Card.getCard(pSituation.getTrumpSuit(), Rank.ACE).beats(
							pSituation.getGameType(), tCardToBeat)) {
				return Card.getCard(pSituation.getTrumpSuit(), Rank.ACE);
			}
			// Wenn der Spieler die Trumpf-10 hat und mit dem Ass gewinnt
			if (pCards.contains(Card.getCard(pSituation.getTrumpSuit(),
					Rank.TEN))
					&& Card.getCard(pSituation.getTrumpSuit(), Rank.TEN).beats(
							pSituation.getGameType(), tCardToBeat)) {
				return Card.getCard(pSituation.getTrumpSuit(), Rank.TEN);
			}
			// Wenn der Spieler die Trumpf-K hat und mit dem Ass gewinnt
			if (pCards.contains(Card.getCard(pSituation.getTrumpSuit(),
					Rank.KING))
					&& Card.getCard(pSituation.getTrumpSuit(), Rank.KING)
							.beats(pSituation.getGameType(), tCardToBeat)) {
				return Card.getCard(pSituation.getTrumpSuit(), Rank.KING);
			}

			return pCards.get(pCards.getLastIndexOfSuit(pSituation
					.getTrumpSuit()));
		}

		for (final Suit s : Suit.values()) {
			if (s == pSituation.getTrumpSuit()
					|| pCards.getSuitCount(s, false) == 0) {
				continue;
			}
			// Wenn nur eine Karte dieser Farbe, die zudem <= 4 Punkte Wert ist
			// und eine Karte mit Wert >= 10 noch im Spiel ist -> abwerfen
			if (pCards.getSuitCount(s, false) == 1
					&& pCards.get(pCards.getFirstIndexOfSuit(s)).getPoints() <= 4
					&& (!pNotOpponentCards.contains(Card.getCard(
							pSituation.getTrumpSuit(), Rank.ACE)) || !pNotOpponentCards
									.contains(Card.getCard(pSituation.getTrumpSuit(),
											Rank.TEN)))) {
				return pCards.get(pCards.getFirstIndexOfSuit(s));
			}
			// Wenn 3 Karten einer Farbe, dann kann die niedrigste Karte
			// geworfen werden
			if (pCards.getSuitCount(s, false) >= 3) {
				possibleCards.add(pCards.get(pCards.getLastIndexOfSuit(s)));
			}
		}
		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}

		if (pCards.getTrumpCount(pSituation.getTrumpSuit()) > 0) {
			if (pCards.getSuitCount(pSituation.getTrumpSuit(), false) > 0) {
				return pCards.get(pCards.getFirstIndexOfSuit(pSituation
						.getTrumpSuit()));
			}
			return pCards.get(Helper.countJacks(pCards) - 1);
		}

		for (final Suit s : Suit.values()) {
			if (s == pSituation.getTrumpSuit()
					|| pCards.getSuitCount(s, false) == 0) {
				continue;
			}
			possibleCards.add(pCards.get(pCards.getLastIndexOfSuit(s)));
		}
		return getRandomAllowedCard(possibleCards, tForehandCard,
				pSituation.getGameType());
	}

	public static CardList discardSkatCards(final BidEvaluator pBid,
			final CardList pOwnCards) {
		final CardList tCards = new CardList(pOwnCards);
		tCards.sort(pBid.getSuggestedGameType());

		CardList tDiscardCards = new CardList();
		final CardList t1ToDiscard = new CardList();
		final ArrayList<CardList> t2ToDiscard = new ArrayList<CardList>();
		final CardList t1PossibleDiscard = new CardList();
		final ArrayList<CardList> t2PossibleDiscard = new ArrayList<CardList>();
		final ArrayList<CardList> tTenZeroToDiscard = new ArrayList<CardList>();

		for (final Suit lSuit : Suit.values()) {
			if (lSuit == pBid.getSuggestedTrumpSuit()
					|| tCards.getSuitCount(lSuit, false) == 0) {
				continue;
			}

			final int lFirstIndex = tCards.getFirstIndexOfSuit(lSuit, false);
			final int lLastIndex = tCards.getLastIndexOfSuit(lSuit, false);
			final Card lFirstCard = tCards.get(lFirstIndex);
			final Card lLastCard = tCards
					.get(tCards.getLastIndexOfSuit(lSuit, false));

			// Wenn nur eine Karte der Farbe und diese ist nicht das Ass
			if (tCards.getSuitCount(lSuit, false) == 1) {
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
			else if (tCards.getSuitCount(lSuit, false) == 2) {
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
					final CardList t = new CardList();
					t.add(lFirstCard);
					t.add(tCards.get(lFirstIndex + 1));
					tTenZeroToDiscard.add(t);
				} else {
					final CardList t = new CardList();
					t.add(lFirstCard);
					t.add(tCards.get(lFirstIndex + 1));
					t2ToDiscard.add(t);
					t1PossibleDiscard.add(lFirstCard);
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
						t2ToDiscard.add(t);
						t1PossibleDiscard.add(lFirstCard);
					}
				}
				if (lFirstCard.getRank() == Rank.TEN) {
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
			if (!t1PossibleDiscard.isEmpty()) {
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

		if (tDiscardCards.size() != 2 && !tTenZeroToDiscard.isEmpty()) {
			CardList returnCardList = new CardList();
			int points = 0;
			for (final CardList cards : tTenZeroToDiscard) {
				if (cards.get(0).getPoints() + cards.get(1).getPoints() > points) {
					points = cards.get(0).getPoints()
							+ cards.get(1).getPoints();
					returnCardList = cards;
				}
			}
			tDiscardCards = returnCardList;
		}

		if (tDiscardCards.size() == 0
				&& tCards.getTrumpCount(pBid.getSuggestedTrumpSuit()) == 11) {
			tDiscardCards.add(tCards.get(tCards.size() - 1));
		}
		if (tDiscardCards.size() == 1
				&& tCards.getTrumpCount(pBid.getSuggestedTrumpSuit()) == 11) {
			tDiscardCards.add(tCards.get(tCards.size() - 2));
		}

		if (tDiscardCards.size() != 2) {
			log.debug("x");
		}

		return tDiscardCards;
	}
}