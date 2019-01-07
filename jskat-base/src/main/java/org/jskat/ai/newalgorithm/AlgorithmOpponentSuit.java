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
import java.util.List;

import org.jskat.ai.newalgorithm.exception.IllegalMethodException;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmOpponentSuit extends AbstractAlgorithmAI {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmOpponentSuit.class);

	AlgorithmOpponentSuit(final AlgorithmAI p, final GameType pGameType) {
		super(p, pGameType);

		log.debug(String.format("/s is %s", myPlayer.getPlayerName(), this
				.getClass().getName()));
	}

	@Override
	protected Card startGame() {
		log.debug("Suit-Opponent starts Game: "
				+ knowledge.getCurrentTrick().getForeHand());

		return playStartGameCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getPlayerPosition(),
				knowledge.getDeclarer());
	}

	@Override
	protected Card playForehandCard() {
		log.debug("Suit-Opponent plays Forehand-Card: "
				+ knowledge.getCurrentTrick().getForeHand());

		return playForehandCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getPlayerPosition(),
				knowledge.getDeclarer());
	}

	@Override
	protected Card playMiddlehandCard() {
		log.debug("Suit-Opponent plays Middlehand-Card: "
				+ knowledge.getCurrentTrick().getMiddleHand());

		return playMiddlehandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getPlayerPosition(),
				knowledge.getDeclarer());
	}

	@Override
	protected Card playRearhandCard() {
		log.debug("Suit-Opponent plays Rearhand-Card: "
				+ knowledge.getCurrentTrick().getRearHand());

		return playRearhandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getPlayerPosition(),
				knowledge.getDeclarer());
	}

	@Override
	public CardList discardSkat(final BidEvaluator bidEvaluator) {
		throw new IllegalMethodException(
				"AlgorithmOpponentSuit has nothing to discard!");
	}

	// static methods for creating JUnit-tests and test cardplaybehavior
	public static Card playStartGameCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation, final Player pPlayerPosition, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final boolean tDeclarerInMiddle = pPlayerPosition.getLeftNeighbor() == pDeclarer;

		final CardList tPossibleHighCard = new CardList();
		final CardList tPossibleLowCard = new CardList();

		for (final Suit s : Suit.values()) {
			if (s == pSituation.getTrumpSuit()
					|| pCards.getSuitCount(s, false) == 0) {
				continue;
			}

			if (pCards.get(pCards.getFirstIndexOfSuit(s, false)).getRank() == Rank.ACE) {
				tPossibleHighCard.add(pCards.get(pCards.getFirstIndexOfSuit(s,
						false)));
			}
			if ((tDeclarerInMiddle || pCards.getSuitCount(s, false) >= 3)
					&& pCards.get(pCards.getLastIndexOfSuit(s, false))
							.getPoints() <= 0) {
				tPossibleLowCard.add(pCards.get(pCards.getLastIndexOfSuit(s,
						false)));
			}
		}

		if (!tPossibleHighCard.isEmpty()) {
			return playRandomCard(tPossibleHighCard);
		}
		if (!tPossibleLowCard.isEmpty()) {
			return playRandomCard(tPossibleLowCard);
		}

		return playForehandCard(pCards, pTrickCards, pPlayedCards,
				pNotOpponentCards, pSituation, pPlayerPosition, pDeclarer);
	}

	public static Card playForehandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation, final Player pPlayerPosition, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final boolean tDeclarerInMiddle = pPlayerPosition.getLeftNeighbor() == pDeclarer;

		final CardList possibleCards = new CardList();

		for (final Suit lSuit : Suit.values()) {
			if (lSuit == pSituation.getTrumpSuit()
					|| pCards.getSuitCount(lSuit, false) == 0) {
				continue;
			}

			final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
					lSuit, false)); // highest Card
			final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(lSuit,
					false)); // lowest Card

			// Wenn nur eine Karte und diese weniger als 10 Puntke wert ist
			if (possibleHighCard.equals(possibleLowCard)
					&& possibleHighCard.getPoints() < 10) {
				possibleCards.add(possibleHighCard);
			}

			// Wenn der Solo-Spieler in Mittelhand
			if (tDeclarerInMiddle) {
				// Wenn nur noch der Spieler selbst Karten der Farbe hat ->
				// versuchen Truempfe vom Solo-Spieler ziehen
				if ((Helper.getSuitCardsToBinary(pPlayedCards, lSuit) & Helper
						.getSuitCardsToBinary(pCards, lSuit)) == 127) {
					return possibleHighCard;
				}
				possibleCards.add(possibleLowCard);
			} else {
				if ((Helper.getSuitCardsToBinary(pPlayedCards, lSuit) & Helper
						.getSuitCardsToBinary(pCards, lSuit)) == 127
						&& pCards.getSuitCount(lSuit, false) <= 3) {
					return possibleHighCard;
				}
				if (possibleLowCard.getPoints() <= 4) {
					possibleCards.add(possibleLowCard);
				}
			}

			// Wenn der Spieler die hoechste Karte der Farbe hat
			// und der Solo-Spieler nicht blank ist
			if (pPlayedCards.getSuitCount(lSuit, false) <= 4
					&& (pPlayerPosition.getLeftNeighbor() == pDeclarer
							&& !pSituation.isLeftPlayerBlankOnColor(lSuit)
							|| pPlayerPosition
									.getRightNeighbor() == pDeclarer
									&& !pSituation.isRightPlayerBlankOnColor(lSuit))
					&& Helper.isHighestSuitCard(possibleHighCard,
							pSituation.getGameType(), pPlayedCards, null)) {
				return possibleHighCard;
			}
		}

		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}

		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static Card playMiddlehandCard(final CardList pCards,
			final CardList pTrickCards, final CardList pPlayedCards,
			final CardList pNotOpponentCards, final Situation pSituation,
			final Player pPlayerPosition, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final boolean tDeclarerInForhand = pPlayerPosition.getRightNeighbor() == pDeclarer;
		final Card tForehandCard = pTrickCards.get(0);
		final Suit tSuit = tForehandCard.getSuit();
		ArrayList<Suit> tDeclarerBlankSuits = pSituation
				.getLeftPlayerBlankSuits();
		if (pPlayerPosition.getRightNeighbor() == pDeclarer) {
			tDeclarerBlankSuits = pSituation.getRightPlayerBlankSuits();
		}

		final CardList possibleCards = new CardList();

		// Trumpfkarte wurde gespielt
		if (tForehandCard.getSuit() == pSituation.getTrumpSuit()
				|| tForehandCard.getRank() == Rank.JACK) {
			// Wenn Solo-Spieler in Forehand sitzt und die erste Karte des
			// Tricks seine ist
			if (tDeclarerInForhand) {
				// Wenn der Spieler Trumpfkarten hat
				if (pCards.hasTrump(pSituation.getGameType())) {
					final int tTrumpCount = Helper.countJacks(pCards)
							+ pCards.getSuitCount(pSituation.getTrumpSuit(),
									false);
					if (tTrumpCount == 1) {
						return pCards.get(0);
					}
					// Wenn schlagbar
					if (pCards.get(0).beats(pSituation.getGameType(),
							tForehandCard)) {
						if (pCards.contains(Card.getCard(
								pSituation.getTrumpSuit(), Rank.ACE))
								&& Rank.ACE.toBinaryFlag() > tForehandCard
										.getRank().toBinaryFlag()) {
							return Card.getCard(pSituation.getTrumpSuit(),
									Rank.ACE);
						} else if (pCards.contains(Card.getCard(
								pSituation.getTrumpSuit(), Rank.TEN))
								&& Rank.TEN.toBinaryFlag() > tForehandCard
										.getRank().toBinaryFlag()) {
							return Card.getCard(pSituation.getTrumpSuit(),
									Rank.TEN);
						}
						return getLowestBeatingCard(pCards,
								pSituation.getGameType(), tForehandCard);
					}
					return Helper.getLowestTrumpValueCard(pCards,
							pSituation.getTrumpSuit(), true);
				}
				// Wenn kein Trumpf mehr auf der Hand
				for (final Suit lSuit : Suit.values()) {
					if (lSuit == pSituation.getTrumpSuit()
							|| pCards.getSuitCount(lSuit, false) == 0) {
						continue;
					}

					final Card possibleHighCard = pCards.get(pCards
							.getFirstIndexOfSuit(lSuit, false)); // highest Card
					final Card possibleLowCard = pCards.get(pCards
							.getLastIndexOfSuit(lSuit, false)); // lowest Card

					// Wenn nur eine Karte der Farbe und nicht das Ass
					if (possibleHighCard == possibleLowCard
							&& possibleHighCard.getRank() != Rank.ACE) {
						// Wenns kein Ass oder 10 ist TODO
						if (possibleHighCard.getRank() == Rank.TEN) {
							if (Helper
									.getSuitCardsToBinary(pPlayedCards, lSuit) >= Rank.ACE
											.toBinaryFlag()) {
								continue;
							}
							possibleCards.add(possibleHighCard);
						} else {
							return possibleHighCard;
						}
					} else if (Helper.getSuitCardsToBinary(pPlayedCards, lSuit) > 2) {
						return possibleLowCard;
						// Wenn genau 2 Karten
					} else {
						possibleCards.add(possibleLowCard);
					}
				}
			}
			// Solo-Spieler sitzt in Hinterhand
			else {
				// Wenn nur noch ein Trumpf
				if (Helper.getTrumpCardsToBinary(pCards,
						pSituation.getTrumpSuit()) == 1) {
					return pCards.get(0);
				}

				// Wenn der Spieler Trumpfkarten hat
				if (pCards.hasTrump(pSituation.getGameType())) {
					// Wenn der Trumpf vom Mitspieler nicht geschlagen werden
					// kann
					if (Helper.isHighestTrumpCard(tForehandCard,
							pSituation.getGameType(), pPlayedCards)) {
						final Card tPossibleCard = Helper.getHighestValueCard(pCards,
								pSituation.getTrumpSuit(), true);
						// Wenn die HighValueCard des Spielers danach die
						// hoechste Karte ist
						if (Helper.isHighestTrumpCard(tPossibleCard,
								pSituation.getGameType(), pPlayedCards)) {
							return pCards
									.get(pCards.getIndexOf(tPossibleCard) + 1);
						}
						return tPossibleCard;
					}

					// Wenn das gelegte Ass oder die gelegte 10 die hoechste
					// Karte ist
					if (tForehandCard.getRank() == Rank.ACE
							&& (Helper.getTrumpCardsToBinary(pPlayedCards,
									pSituation.getTrumpSuit())
									& Helper
											.getTrumpCardsToBinary(pCards,
													pSituation.getTrumpSuit())) >= 1920 // CJ,
							// SJ,
							// HJ,
							// DJ
							|| tForehandCard.getRank() == Rank.TEN
									&& (Helper.getTrumpCardsToBinary(pPlayedCards,
											pSituation.getTrumpSuit())
											& Helper
													.getTrumpCardsToBinary(pCards,
															pSituation.getTrumpSuit())) >= 1984) { // CJ,
						// SJ,
						// HJ,
						// DJ,
						// ACE
						return pCards.get(pCards.getFirstIndexOfSuit(
								pSituation.getTrumpSuit(), false));
					}

					// Wenn Trumpfkarte auf der Hand -> niedrigste spielen
					if (pCards.getSuitCount(pSituation.getTrumpSuit(), false) > 0) {
						return pCards.get(pCards.getLastIndexOfSuit(
								pSituation.getTrumpSuit(), false));
					}
					// Sonst niedrigsten Buben spielen
					return pCards.get(Helper.countJacks(pCards) - 1);
				}

				// Wenn keine Trumpfkarte in der Hand
				// Wenn der Trumpf vom Mitspieler nicht geschlagen werden kann
				if (Helper.isHighestTrumpCard(tForehandCard,
						pSituation.getGameType(), pPlayedCards)) {
					for (int i = 0; i < tDeclarerBlankSuits.size(); i++) {
						final Card lCard = Helper.getHighestValueCard(pCards,
								tDeclarerBlankSuits.get(i), false);
						if (lCard != null && lCard.getPoints() >= 10) {
							return lCard;
						}
						possibleCards.add(lCard);
					}
					if (possibleCards.size() > 0) {
						return playRandomCard(possibleCards);
					}
				}

				// Spiele zuerst niedrige Karten der blanken Farbe des
				// Solo-Spielers
				for (int i = 0; i < tDeclarerBlankSuits.size(); i++) {
					final Card lCard = Helper.getLowestValueCard(pCards,
							tDeclarerBlankSuits.get(i));
					if (lCard != null) {
						if (lCard.getPoints() <= 3) {
							return lCard;
						}
						possibleCards.add(lCard);
					}
				}
				for (final Suit s : Suit.values()) {
					final Card lCard = Helper.getLowestValueCard(pCards, s);
					if (lCard != null) {
						if (lCard.getPoints() <= 3) {
							return lCard;
						}
						possibleCards.add(lCard);
					}
				}
				if (possibleCards.size() > 0) {
					return playRandomCard(possibleCards);
				}
			}
		}

		// Wenn nur eine Karte der Farbe vorhanden
		if (pCards.getSuitCount(tSuit, false) == 1) {
			return pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));
		}

		// Keine Trumpkarte wurde gespielt
		if (tDeclarerInForhand) {
			// Wenn der Spieler bedienen kann/muss
			if (pCards.getSuitCount(tSuit, false) > 1) {
				final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
						tSuit, false)); // highest Card
				final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(
						tSuit, false)); // lowest Card
				// Wenn der Solo-Spieler schlagbar ist -> schlagen
				if (possibleHighCard.beats(pSituation.getGameType(),
						tForehandCard)) {
					// Wenn die zweithÃƒÂ¶chste Karte anschliessend unschlagbar
					// ist
					if (Helper.isHighestSuitCard(pCards.get(pCards
							.getFirstIndexOfSuit(tSuit, false) + 1), pSituation
									.getGameType(),
							pNotOpponentCards, pTrickCards)) {
						return possibleHighCard;
					}
					for (int i = pCards.getLastIndexOfSuit(tSuit, false); i >= pCards
							.getFirstIndexOfSuit(tSuit, false); i--) {
						if (pCards.get(i).beats(pSituation.getGameType(),
								tForehandCard)) {
							return pCards.get(i);
						}
					}
				}
				final int i = Helper.getSuitCardsToBinary(pNotOpponentCards, tSuit)
						+ Helper.getSuitCardsToBinary(pTrickCards, tSuit);
				if (Helper.getSuitCardsToBinary(pNotOpponentCards, tSuit)
						+ Helper.getSuitCardsToBinary(pTrickCards, tSuit) == 127) {
					return possibleHighCard;
				}
				return possibleLowCard;
			}

			// Wenn keine Karte dieser Farbe
			// Wenn Trumpfkarte vorhanden
			if (pCards.hasTrump(pSituation.getGameType())) {
				final Card tPossibleCard = Helper.getHighestValueCard(pCards,
						pSituation.getTrumpSuit(), true);
				final int jackCount = Helper.countJacks(pCards);
				if (tPossibleCard != null) {
					// Wenn die hochwertige Karte des Spielers die hoechste
					// Trumpkarte ist
					if (Helper.isHighestTrumpCard(tPossibleCard,
							pSituation.getGameType(), pPlayedCards)
							&& pCards.get(pCards.getIndexOf(tPossibleCard) + 1)
									.getSuit() == pSituation.getTrumpSuit()) {
						return pCards.get(pCards.getIndexOf(tPossibleCard) + 1);
					}
					return tPossibleCard;
				} else if (jackCount > 0) {
					return pCards.get(jackCount - 1);
				}
			}

			// TODO: Wenn vom Mitspieler schlagbar -> hochwertige Karte, sonst
			// niedrige Karte spielen
			// Wenn die Karte des Solo-Spielers nicht die hoechste ist
			if (!Helper.isHighestSuitCard(tForehandCard,
					pSituation.getGameType(), pPlayedCards, pTrickCards)
					&& pPlayedCards.getSuitCount(pSituation.getTrumpSuit(),
							false) < 3
					|| pPlayedCards.getSuitCount(pSituation.getTrumpSuit(),
							false) > 5) {
				for (int i = 0; i < tDeclarerBlankSuits.size(); i++) {
					final Card lCard = Helper.getHighestValueCard(pCards,
							tDeclarerBlankSuits.get(i), false);
					if (lCard != null && lCard.getPoints() >= 10) {
						possibleCards.add(lCard);
					}
				}
				if (possibleCards.size() > 0) {
					return playRandomCard(possibleCards);
				}
			}

			// Solo-Spielers blanke Farben abwerfen wenn <= 4 Punkte Karte
			// vorhanden
			for (int i = 0; i < tDeclarerBlankSuits.size(); i++) {
				final Card lCard = Helper.getLowestValueCard(pCards,
						tDeclarerBlankSuits.get(i));
				if (lCard != null && lCard.getPoints() <= 4) {
					possibleCards.add(lCard);
				}
			}
			if (possibleCards.size() > 0) {
				return playRandomCard(possibleCards);
			}
		}
		// Forehand-Karte ist keine Trumpfkarte und Solo-Spieler sitzt in
		// Hinterhand
		else {
			// Wenn mehr als eine Karte der Farbe habe und bedienen kann/muss
			if (pCards.getSuitCount(tSuit, false) > 1) {
				// Solo-Spieler ist blank auf der Farbe -> niedrigste Karte
				// legen
				if (!tDeclarerBlankSuits.contains(tSuit)) {
					return pCards.get(pCards.getLastIndexOfSuit(tSuit, false));
				}
				return pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));
			} else if (pCards.getSuitCount(tSuit, false) == 0) {
				// Wenn >= 10 Punkte im Stich und Spieler hat den hoechsten
				// Trumpf in der Hand
				if (tDeclarerBlankSuits.contains(pSituation.getTrumpSuit())
						&& tForehandCard.getPoints() >= 10
						&& pCards.get(0).getSuit() == pSituation.getTrumpSuit()
						&& Helper.isHighestTrumpCard(pCards.get(0),
								pSituation.getGameType(), pPlayedCards)) {
					return pCards.get(0);
				}
				// Solo-Spieler hat wahrscheinlich noch eine Karte und A oder 10
				// ist noch nicht gespielt
				if (!tDeclarerBlankSuits.contains(pSituation.getTrumpSuit())
						&& (!pPlayedCards.contains(Card.getCard(
								pSituation.getTrumpSuit(), Rank.ACE))
								|| !pPlayedCards.contains(Card.getCard(
										pSituation.getTrumpSuit(), Rank.TEN))
								|| tForehandCard
										.getPoints() >= 10)) {
					if (pCards.hasTrump(pSituation.getGameType())) {
						return Helper.getHighestValueCard(pCards,
								pSituation.getTrumpSuit(), true);
					}
					return pCards.get(Helper.countJacks(pCards) - 1);
				}
			}

			if (tDeclarerBlankSuits.contains(pSituation.getTrumpSuit())
					|| Helper.isHighestTrumpCard(pCards.get(0),
							pSituation.getGameType(), pPlayedCards)) {
				return pCards.get(0); // highest Card
			} else {
				return pCards.get(pCards.getLastIndexOfSuit(
						pSituation.getTrumpSuit(), false));
			}
		}

		return getRandomAllowedCard(pCards, tForehandCard,
				pSituation.getGameType());
	}

	public static Card playRearhandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation, final Player pPlayerPosition, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final Card tForehandCard = pTrickCards.get(0);
		final Suit tSuit = tForehandCard.getSuit();
		final Card tMiddlehandCard = pTrickCards.get(1);
		CardList possibleCards = new CardList();

		Card tCardToBeat = tForehandCard;
		if (tMiddlehandCard.beats(pSituation.getGameType(), tCardToBeat)) {
			tCardToBeat = tMiddlehandCard;
		}
		final Suit tCardToBeatSuit = tCardToBeat.getSuit();

		ArrayList<Suit> tDeclarerBlankSuits = pSituation
				.getLeftPlayerBlankSuits();
		if (pPlayerPosition.getRightNeighbor() == pDeclarer) {
			tDeclarerBlankSuits = pSituation.getRightPlayerBlankSuits();
		}

		// Wenn der Stich bislang dem Solo-Spieler gehoert
		// Wenn Middlehand die hoehere ist und rechts vom Spieler sitzt der
		// Solo-Spieler
		// oder Forehand ist der Solo-Spieler und liegt vorne
		if (tMiddlehandCard.beats(pSituation.getGameType(), tForehandCard) == (pPlayerPosition
				.getRightNeighbor() == pDeclarer)) {
			// Wenn Solo-Spieler in Forehand sitzt
			if (tCardToBeat == tForehandCard) {
				// Wenn kein Trumpf
				if (!tForehandCard.isTrump(pSituation.getGameType())) {
					// Wenn bedienen kann/muss
					if (pCards.getSuitCount(tCardToBeatSuit, false) > 0) {
						// Wenn schlagbar
						if (pCards.get(
								pCards.getFirstIndexOfSuit(tCardToBeatSuit,
										false))
								.beats(pSituation.getGameType(),
										tCardToBeat)) {
							return getLowestBeatingCard(pCards,
									pSituation.getGameType(), tCardToBeat);
						}
						return pCards.get(pCards.getLastIndexOfSuit(
								tCardToBeatSuit, false));
					}
					// Wenn genug Punkte im Stich und Trumpf vorhanden -> Punkte
					// mitnehmen
					if (pCards.hasTrump(pSituation.getGameType())
							&& tForehandCard.getPoints()
									+ tMiddlehandCard.getPoints() >= 10) {
						if (pCards.hasTrump(pSituation.getGameType())) {
							return Helper.getHighestValueCard(pCards,
									pSituation.getTrumpSuit(), true);
						}
						return pCards.get(Helper.countJacks(pCards) - 1);
					}
					// FremdSuit-Karte abwerfen
					for (final Suit lSuit : Suit.values()) {
						final int lSuitCount = pCards.getSuitCount(lSuit, false);
						if (lSuit == pSituation.getTrumpSuit()
								|| lSuitCount == 0) {
							continue;
						}

						final Card possibleHighCard = pCards.get(pCards
								.getFirstIndexOfSuit(lSuit, false)); // highest
						// Card
						final Card possibleLowCard = pCards.get(pCards
								.getLastIndexOfSuit(lSuit, false)); // lowest
						// Card

						// Wenn eine Karte der Farbe
						if (lSuitCount == 1) {
							// Solo-Spieler hat Farbe blank && Kartenwert < 10
							if (tDeclarerBlankSuits.contains(lSuit)
									&& possibleLowCard.getPoints() < 10) {
								possibleCards.add(possibleLowCard);
							}
							// Wenn die Karte 0 Wert hat -> Farbe blank spielen
							if (possibleLowCard.getPoints() == 0) {
								return possibleLowCard;
							}
						}
						// Wenn zwei Karte der Farbe
						if (lSuitCount == 2) {
							// Solo-Spieler hat Farbe blank && Kartenwert < 10
							if (tDeclarerBlankSuits.contains(lSuit)
									&& possibleLowCard.getPoints() < 10) {
								possibleCards.add(possibleLowCard);
							}
							// Wenn niedrige Karte 7,8,9 oder Q ist und die hohe
							// Karte unschlagbar ist
							if (possibleLowCard.getPoints() <= 3
									&& Helper.isHighestSuitCard(
											possibleHighCard,
											pSituation.getGameType(),
											pPlayedCards, pTrickCards)) {
								possibleCards.add(possibleLowCard);
							}
						}
						if (lSuitCount > 2) {
							// Wenn niedrige Karte 7,8,9 oder Q ist
							if (possibleLowCard.getPoints() < 4) {
								possibleCards.add(possibleLowCard);
							}
						}
					}
					if (!possibleCards.isEmpty()) {
						return playRandomCard(possibleCards);
					}

					// mit einem niedrigen Trumpf mitnehmen
					if (pCards.getSuitCount(pSituation.getTrumpSuit(), false) > 0) {
						return Helper.getLowestTrumpValueCard(pCards,
								pSituation.getTrumpSuit(), false);
					}
				}
				// Wenn Trumpf
				else {
					// Wenn bedienen kann/muss
					if (pCards.hasTrump(pSituation.getGameType())) {
						// Wenn schlagbar
						if (pCards.get(0).beats(pSituation.getGameType(),
								tCardToBeat)) {
							return getLowestBeatingCard(pCards,
									pSituation.getGameType(), tCardToBeat);
						}
						// nicht schlagbar
						return Helper.getLowestTrumpValueCard(pCards,
								pSituation.getTrumpSuit(), true);
					}
					// FremdSuit-Karte abwerfen
					for (final Suit lSuit : Suit.values()) {
						final int lSuitCount = pCards.getSuitCount(lSuit, false);
						if (lSuit == pSituation.getTrumpSuit()
								|| lSuitCount == 0) {
							continue;
						}

						final Card possibleHighCard = pCards.get(pCards
								.getFirstIndexOfSuit(lSuit, false)); // highest
						// Card
						final Card possibleLowCard = pCards.get(pCards
								.getLastIndexOfSuit(lSuit, false)); // lowest
						// Card

						// Wenn eine Karte der Farbe
						if (lSuitCount == 1) {
							// Solo-Spieler hat Farbe blank && Kartenwert < 10
							if (tDeclarerBlankSuits.contains(lSuit)
									&& possibleLowCard.getPoints() < 10) {
								possibleCards.add(possibleLowCard);
							}
							// Wenn die Karte 0 Wert hat -> Farbe blank spielen
							if (possibleLowCard.getPoints() == 0) {
								return possibleLowCard;
							}
						}
						// Wenn zwei Karte der Farbe
						if (lSuitCount == 2) {
							// Solo-Spieler hat Farbe blank && Kartenwert < 10
							if (tDeclarerBlankSuits.contains(lSuit)
									&& possibleLowCard.getPoints() < 10) {
								possibleCards.add(possibleLowCard);
							}
							// Wenn niedrige Karte 7,8,9 oder Q ist und die hohe
							// Karte unschlagbar ist
							if (possibleLowCard.getPoints() <= 3
									&& Helper.isHighestSuitCard(
											possibleHighCard,
											pSituation.getGameType(),
											pPlayedCards, pTrickCards)) {
								possibleCards.add(possibleLowCard);
							}
						}
						if (lSuitCount > 2) {
							// Wenn niedrige Karte 7,8,9 oder Q ist
							if (possibleLowCard.getPoints() < 4) {
								possibleCards.add(possibleLowCard);
							}
						}
					}
					if (!possibleCards.isEmpty()) {
						return playRandomCard(possibleCards);
					}
				}
			}
			// Solo-Spieler ist in Mittelhand und bislang gehÃƒÂ¶rt ihm der
			// Stich
			// Wenn Vorhand schon eine Trumpfkarte ist
			if (tForehandCard.isTrump(pSituation.getGameType())) {
				// Wenn der Spieler noch einen Trumpf auf der Hand hat
				if (pCards.hasTrump(pSituation.getGameType())) {
					// niedrigen Trumpf
					if (pCards.get(0).beats(pSituation.getGameType(),
							tCardToBeat)) {
						return getLowestBeatingCard(pCards,
								pSituation.getGameType(), tCardToBeat);
					}
					return getLowValueTrumpCard(pCards,
							pSituation.getTrumpSuit());
				}
				// Kein Trumpf mehr auf der Hand
				possibleCards = getPossibleMaxValueCards(pCards, 0,
						pSituation.getTrumpSuit());
				if (!possibleCards.isEmpty()) {
					return playRandomCard(possibleCards);
				}
				possibleCards = getPossibleMaxValueCards(pCards, 3,
						pSituation.getTrumpSuit());
				if (!possibleCards.isEmpty()) {
					return playRandomCard(possibleCards);
				}
				possibleCards = getPossibleMaxValueCards(pCards, 4,
						pSituation.getTrumpSuit());
				if (!possibleCards.isEmpty()) {
					return playRandomCard(possibleCards);
				}
			}
			// Wenn Vorhand eine andere Farbe ist
			// UND Solo-Spieler sticht mit Trumpf
			else if (!tForehandCard.isTrump(pSituation.getGameType())
					&& tMiddlehandCard.isTrump(pSituation.getGameType())) {
				if (pCards.hasSuit(pSituation.getGameType(), tSuit)) {
					return Helper.getLowestValueCard(pCards, tSuit);
				}
			}
			// Wenn Solo-Spieler mit selber Farbe den Stich haelt
			else {
				// Wenn der Spieler eine Karte der Farbe hat
				if (pCards.hasSuit(pSituation.getGameType(), tSuit)) {
					final Card firstCard = pCards.get(pCards.getFirstIndexOfSuit(
							tSuit, false));
					final Card secondCard = pCards.get(pCards.getFirstIndexOfSuit(
							tSuit, false));
					// Wenn mit zweiter Karte schlagbar
					if (secondCard.beats(pSituation.getGameType(), tCardToBeat)) {
						// Wenn es moeglich ist, dass eine Karte dazwischen beim
						// Solo-Spieler ist
						final List<Rank> ranks = Rank.getRankList();
						for (int i = ranks.indexOf(firstCard.getRank()) + 1; i < ranks
								.indexOf(secondCard.getRank()); i++) {
							if (!pNotOpponentCards.contains(Card.getCard(
									firstCard.getSuit(), ranks.get(i)))) {
								return secondCard;
							}
						}
						return firstCard;
					}
					if (firstCard.beats(pSituation.getGameType(), tCardToBeat)) {
						return firstCard;
					}
					return Helper.getLowestValueCard(pCards, tSuit);
				} else {
					// Wenn mehr als 10 Punkte im Stich und noch Trumpf auf der
					// Hand
					if (tForehandCard.getPoints() + tMiddlehandCard.getPoints() >= 8
							&& pCards.hasTrump(pSituation.getGameType())) {
						return Helper.getHighestValueCard(pCards,
								pSituation.getTrumpSuit(), true);
					}

					// Lusche abwerfen
					possibleCards = getPossibleMaxValueCards(pCards, 0,
							pSituation.getTrumpSuit());
					if (!possibleCards.isEmpty()) {
						return playRandomCard(possibleCards);
					}
					possibleCards = getPossibleMaxValueCards(pCards, 3,
							pSituation.getTrumpSuit());
					if (!possibleCards.isEmpty()) {
						return playRandomCard(possibleCards);
					}
					possibleCards = getPossibleMaxValueCards(pCards, 4,
							pSituation.getTrumpSuit());
					if (!possibleCards.isEmpty()) {
						return playRandomCard(possibleCards);
					}
				}
			}
		}

		// Wenn Trumpf bedient werden muss
		if (tSuit == pSituation.getTrumpSuit()
				|| tForehandCard.getRank() == Rank.JACK) {
			if (pCards.hasTrump(pSituation.getGameType())) {
				return Helper.getHighestValueCard(pCards,
						pSituation.getTrumpSuit(), true);
			}
			if (Helper.countJacks(pCards) > 0) {
				return pCards.get(Helper.countJacks(pCards) - 1);
			}
		}
		// Wenn bedienen kann/muss
		final int tSuitCount = pCards.getSuitCount(tSuit, false);
		if (tSuitCount == 1) {
			// Karte spielen
			return pCards.get(pCards.getFirstIndexOfSuit(tSuit, false));
		} else if (tSuitCount == 2) {
			final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
					tSuit, false)); // highest Card
			final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(tSuit,
					false)); // lowest Card

			// Spieler spielt die hoechste Karte, wenn:
			// - hoechste Karte hat und es sind nur noch 3 Karten im Spiel
			// - pDeclarer in Mittelhand und hat die Farbe schon blank
			// - pDeclarer in Forhand und hat die Farbe schon blank
			if (Helper.isHighestSuitCard(possibleHighCard,
					pSituation.getGameType(), pPlayedCards, pTrickCards)
					&& (pNotOpponentCards.getSuitCount(tSuit, false) > 4 || possibleHighCard
							.getRank().toBinaryFlag() >> 1 == possibleLowCard
									.getRank().toBinaryFlag())
					|| pDeclarer == Player.MIDDLEHAND
							&& pSituation.isRightPlayerBlankOnColor(tSuit)
					|| pDeclarer == Player.FOREHAND
							&& pSituation.isLeftPlayerBlankOnColor(tSuit)) {
				return possibleHighCard;
			}
			possibleCards.add(possibleLowCard);
		} else if (tSuitCount > 2) {
			final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
					tSuit, false)); // highest Card
			final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(tSuit,
					false)); // lowest Card

			if (Helper.isHighestSuitCard(possibleHighCard,
					pSituation.getGameType(), pPlayedCards, pTrickCards)
					&& possibleHighCard.getRank().toBinaryFlag() >> 1 == pCards
							.get(pCards.getFirstIndexOfSuit(tSuit, false) + 1)
							.getRank().toBinaryFlag()
					|| pNotOpponentCards.getSuitCount(tSuit, false) > 4) {
				return possibleHighCard;
			}
			possibleCards.add(pCards.get(pCards.getFirstIndexOfSuit(tSuit,
					false) + 1));
		} else if (tSuitCount == 0) {
			for (final Suit lSuit : Suit.values()) {
				final int lSuitCount = pCards.getSuitCount(lSuit, false);
				if (lSuit == pSituation.getTrumpSuit() || lSuitCount == 0) {
					continue;
				}

				final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
						lSuit, false)); // highest Card
				final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(
						lSuit, false)); // lowest Card

				// Wenn nur eine Karte der Farbe und diese Karte ist nicht die
				// hoechste Karte
				if (lSuitCount == 1) {
					if (!Helper
							.isHighestSuitCard(possibleHighCard,
									pSituation.getGameType(), pPlayedCards,
									pTrickCards)) {
						return possibleHighCard;
					}
				} else if (lSuitCount == 2) {
					// Wenn die hoehere Karte die hoechste ist
					if (Helper
							.isHighestSuitCard(possibleHighCard,
									pSituation.getGameType(), pPlayedCards,
									pTrickCards)) {
						final CardList after = new CardList(pPlayedCards);
						after.add(possibleHighCard);
						// Wenn die niedrigere Karte die Hoechste nach der
						// Hoechsten ist --> Hoechste spielen, sonst die
						// niedrige spielen
						if (possibleHighCard.getRank().toBinaryFlag() >> 1 == possibleLowCard
								.getRank().toBinaryFlag()) {
							return possibleHighCard;
						}
					}
					if (possibleLowCard.getPoints() > 3) {
						return possibleLowCard;
					}
					possibleCards.add(possibleLowCard);
				}
				// Mehr Karten als 2 von der Farbe
				else {
					// Wenn die hoehere Karte die hoechste ist
					if (Helper
							.isHighestSuitCard(possibleHighCard,
									pSituation.getGameType(), pPlayedCards,
									pTrickCards)) {
						final CardList after = new CardList(pPlayedCards);
						after.add(possibleHighCard);
						// Wenn die niedrigere Karte die Hoechste nach der
						// Hoechsten ist --> Hoechste spielen, sonst die
						// niedrige spielen
						if (possibleHighCard.getRank().toBinaryFlag() >> 1 == pCards
								.get(pCards.getFirstIndexOfSuit(lSuit, false) + 1)
								.getRank().toBinaryFlag()) {
							return possibleHighCard;
						}
					}
					possibleCards.add(possibleHighCard);
				}
			}
		}
		if (!possibleCards.isEmpty()) {
			return playRandomCard(possibleCards);
		}

		return getRandomAllowedCard(pCards, tForehandCard,
				pSituation.getGameType());
	}

	protected static CardList getPossibleMaxValueCards(final CardList pCards,
			final int pMaxCardValue, final Suit pTrumpSuit) {
		final CardList possibleCards = new CardList();

		for (final Suit lSuit : Suit.values()) {
			if (lSuit == pTrumpSuit) {
				continue;
			}
			final int suitCount = pCards.getSuitCount(lSuit, false);
			final Card possibleHighCard = pCards.get(pCards.getFirstIndexOfSuit(
					lSuit, false));
			final Card possibleLowCard = pCards.get(pCards.getLastIndexOfSuit(lSuit,
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