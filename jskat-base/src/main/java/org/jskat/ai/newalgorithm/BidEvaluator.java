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

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BidEvaluator is the class to generate the acceptable bid value and to
 * decide, which game to play after discarding the Skat.
 *
 * @author Daniel Loreck
 *
 */
class BidEvaluator {

	private static Logger oLog = LoggerFactory.getLogger(BidEvaluator.class);

	/**
	 * Maximum value that the player will bid
	 */
	private int oMaxBid = -1;
	private GameType oSuggestedGameType = GameType.RAMSCH;
	private Suit oSuggestedTrumpSuit = null;

	private final Player oPlayersPosition;

	private boolean oGrandHand = false;
	private boolean oGrandSchneider = false;
	private boolean oGrandSchwarz = false;
	private boolean oGrandOuvert = false;

	private boolean oNullHand = false;
	private boolean oNullOuvert = false;

	private boolean oSuitHand = false;
	private boolean oSuitSchneider = false;

	/**
	 * default constructor
	 *
	 * @param cards
	 *            hand of the player
	 */
	BidEvaluator(final CardList pCards, final Player pPlayersPosition) {
		oPlayersPosition = pPlayersPosition;

		oLog.debug("Checking out what to bid with [" + pCards + "]"
				+ pCards.dumpFlag());
		eval(pCards);
	}

	public void eval(final CardList pCards) {
		oLog.debug("CARDLISTSIZE: " + pCards.size());
		if (pCards.size() < 10) {
			oLog.warn("Not enough cards for bid evaluation!");
			return;
		}
		oMaxBid = 0;
		// if(check4Grand(pCards)) {
		// if(true) {
		// int tMultiplier = Helper.getGrandMultiplier(pCards);
		// if(oGrandHand) tMultiplier++;
		// if(oGrandSchneider) tMultiplier++;
		// if(oGrandSchwarz) tMultiplier++;
		// if(oGrandOuvert) tMultiplier++;
		//
		// oSuggestedGameType = GameType.GRAND;
		// oMaxBid = tMultiplier *
		// SkatConstants.getGameBaseValue(GameType.GRAND, false, false);
		// }
		// if (check4Null(pCards)) {
		// int tMaxBid = SkatConstants.getGameBaseValue(GameType.NULL,
		// oNullHand, oNullOuvert);
		//
		// if (tMaxBid > oMaxBid) {
		// oSuggestedGameType = GameType.NULL;
		// oMaxBid = tMaxBid;
		// }
		// }

		getHighestSuitMultiplier(pCards);
	}

	private boolean check4Null(final CardList pCards) {
		pCards.sort(GameType.NULL);
		// Count NullCount
		int tNullCount = 0;
		tNullCount += getSuitNullLength(pCards, Suit.CLUBS); // Clubs
		tNullCount += getSuitNullLength(pCards, Suit.SPADES); // Spades
		tNullCount += getSuitNullLength(pCards, Suit.HEARTS); // Hearts
		tNullCount += getSuitNullLength(pCards, Suit.DIAMONDS); // Diamonds

		if (tNullCount == 10) {
			oNullHand = true;
			oNullOuvert = true;
		}
		if (tNullCount >= 7) {
			return true;
		}

		return false;
	}

	private boolean check4Grand(final CardList pCards) {
		pCards.sort(GameType.GRAND);
		// if not enough Jacks -> no Grand
		final int tGrandBinary = Helper.getGrandBinary(pCards);
		if (!(tGrandBinary > 1024 || tGrandBinary == 832)) {
			return false;
		}

		// Count Jacks
		final int tJackCount = Helper.countJacks(pCards);

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

		// Count Blanksuits
		int tBlankSuits = 0;
		if (pCards.getSuitCount(Suit.CLUBS, false) == 0) {
			tBlankSuits++; // Clubs
		}
		if (pCards.getSuitCount(Suit.SPADES, false) == 0) {
			tBlankSuits++; // Spades
		}
		if (pCards.getSuitCount(Suit.HEARTS, false) == 0) {
			tBlankSuits++; // Hearts
		}
		if (pCards.getSuitCount(Suit.DIAMONDS, false) == 0) {
			tBlankSuits++; // Diamonds
		}

		// Count Flutelength
		int tFluteCount = 0;
		tFluteCount += getSuitFluteLength(pCards, Suit.CLUBS); // Clubs
		tFluteCount += getSuitFluteLength(pCards, Suit.SPADES); // Spades
		tFluteCount += getSuitFluteLength(pCards, Suit.HEARTS); // Hearts
		tFluteCount += getSuitFluteLength(pCards, Suit.DIAMONDS); // Diamonds

		// Player has a cardlist to win all tricks
		if (pCards.contains(Card.CJ) && pCards.contains(Card.SJ)
				&& oPlayersPosition.equals(Player.FOREHAND)
				&& tFluteCount + tJackCount == 9
				|| pCards.contains(Card.CJ)
						&& pCards.contains(Card.SJ) && tJackCount >= 3
						&& tFluteCount + tJackCount == 10) {
			oGrandHand = true;
			oGrandSchneider = true;
			oGrandSchwarz = true;
			oGrandOuvert = true;
			return true;
		}
		// Player has a cardlist to win 9 tricks
		if (pCards.contains(Card.CJ) && pCards.contains(Card.SJ)
				&& oPlayersPosition.equals(Player.FOREHAND)
				&& tFluteCount + tJackCount == 9
				|| pCards.contains(Card.CJ)
						&& pCards.contains(Card.SJ) && tJackCount >= 3
						&& tFluteCount + tJackCount == 9) {
			oGrandHand = true;
			oGrandSchneider = true;
			return true;
		}
		if (tAcesCount + t10DuoCount >= 3
				|| tAcesCount + t10DuoCount == 2
						&& (tBlankSuits >= 1 && tJackCount >= 2
								&& pCards.get(1) == Card.SJ
								|| tFluteCount >= 4
										&& tJackCount >= 3
								|| tFluteCount >= 5
										&& pCards.contains(Card.CJ) && pCards.contains(Card.SJ))
				|| tFluteCount >= 7 || tFluteCount >= 2 && tBlankSuits == 2) {
			return true;
		}

		return false;
	}

	private void getHighestSuitMultiplier(final CardList pCards) {
		final Suit tMostFrequentSuitColor = pCards.getMostFrequentSuit();
		final int tMultiplier = Helper.getSuitMultiplier(pCards,
				tMostFrequentSuitColor);
		final int tNumberOfTrumpCards = pCards.getTrumpCount(tMostFrequentSuitColor);

		// Count fast blank
		int tBlankCount = 0;
		int t1CardCount = 0;
		int t2CardCount = 0;
		for (final Suit s : Suit.values()) {
			if (pCards.getSuitCount(s, false) == 0) {
				tBlankCount++;
			} else if (pCards.getSuitCount(s, false) == 1
					&& !pCards.contains(Card.getCard(s, Rank.ACE))) {
				t1CardCount++;
			} else if (pCards.getSuitCount(s, false) == 2
					&& !pCards.contains(Card.getCard(s, Rank.ACE))
					&& !pCards.contains(Card.getCard(s, Rank.TEN))) {
				t2CardCount++;
			}
		}

		// Count Jacks
		final int tJackCount = Helper.countJacks(pCards);

		// Count nicht Trumpf-Asse oder 10+Lusche
		int tWinCardCount = 0;
		if (tMostFrequentSuitColor != Suit.CLUBS
				&& (pCards.contains(Card.CA) || pCards.contains(Card.CT)
						&& pCards.getSuitCount(Suit.CLUBS, false) >= 2)) {
			tWinCardCount++; // Clubs
		}
		if (tMostFrequentSuitColor != Suit.SPADES
				&& (pCards.contains(Card.SA) || pCards.contains(Card.ST)
						&& pCards.getSuitCount(Suit.SPADES, false) >= 2)) {
			tWinCardCount++; // Spades
		}
		if (tMostFrequentSuitColor != Suit.HEARTS
				&& (pCards.contains(Card.HA) || pCards.contains(Card.HT)
						&& pCards.getSuitCount(Suit.HEARTS, false) >= 2)) {
			tWinCardCount++; // Hearts
		}
		if (tMostFrequentSuitColor != Suit.DIAMONDS
				&& (pCards.contains(Card.DA) || pCards.contains(Card.DT)
						&& pCards.getSuitCount(Suit.DIAMONDS, false) >= 2)) {
			tWinCardCount++; // Diamonds
		}

		// Wenn 8 oder mehr Trumpfkarten
		if (tNumberOfTrumpCards >= 8 && pCards.contains(Card.CJ)) {
			if (tWinCardCount > 0) {
				oSuitSchneider = true;
			}
			oSuitHand = true;
			final int tMaxBid = (tMultiplier + 1)
					* SkatConstants.getGameBaseValue(GameType
							.valueOf(tMostFrequentSuitColor.getLongString()
									.toUpperCase()),
							false, false);
			if (tMaxBid > oMaxBid) {
				oSuggestedGameType = GameType.valueOf(tMostFrequentSuitColor
						.getLongString().toUpperCase());
				oSuggestedTrumpSuit = tMostFrequentSuitColor;
				oMaxBid = tMaxBid;
			}
		}
		// Wenn mit 4 und Ass
		else if (pCards.contains(Card.CJ) && tMultiplier >= 5
				|| tNumberOfTrumpCards >= 5 && tWinCardCount >= 1
						&& tBlankCount + t1CardCount > 0
				|| tNumberOfTrumpCards >= 6
						&& (tWinCardCount >= 1 || t1CardCount == 2 && t2CardCount == 1)) {
			final int tMaxBid = tMultiplier
					* SkatConstants.getGameBaseValue(GameType
							.valueOf(tMostFrequentSuitColor.getLongString()
									.toUpperCase()),
							false, false);
			if (tMaxBid > oMaxBid) {
				oSuggestedGameType = GameType.valueOf(tMostFrequentSuitColor
						.getLongString().toUpperCase());
				oSuggestedTrumpSuit = tMostFrequentSuitColor;
				oMaxBid = tMaxBid;
			}
		}
	}

	private int getSuitNullLength(final CardList pCards, final Suit pSuit) {
		int tNullLength = 0;
		if (pCards.contains(Card.getCard(pSuit, Rank.SEVEN))) {
			tNullLength++;
			if (pCards.contains(Card.getCard(pSuit, Rank.EIGHT))) {
				tNullLength++;
				if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) {
					tNullLength = pCards.getSuitCount(pSuit, true);
				} else if (pCards.contains(Card.getCard(pSuit, Rank.TEN))) {
					tNullLength = pCards.getSuitCount(pSuit, true);
				} else if (pCards.contains(Card.getCard(pSuit, Rank.JACK))) {
					tNullLength = pCards.getSuitCount(pSuit, true);
				}
			} else if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) {
				tNullLength++;
				if (pCards.contains(Card.getCard(pSuit, Rank.TEN))) {
					tNullLength = pCards.getSuitCount(pSuit, true);
				} else if (pCards.contains(Card.getCard(pSuit, Rank.JACK))) {
					tNullLength = pCards.getSuitCount(pSuit, true);
				}
			} else {
				tNullLength++;
				if (pCards.contains(Card.getCard(pSuit, Rank.EIGHT))) {
					tNullLength++;
				}
				if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) {
					tNullLength++;
				}
			}
		}
		return tNullLength;
	}

	private int getSuitFluteLength(final CardList pCards, final Suit pSuit) {
		int tFluteLength = 0;
		if (pCards.contains(Card.getCard(pSuit, Rank.ACE))) { // ACE
			tFluteLength++;
			if (pCards.contains(Card.getCard(pSuit, Rank.TEN))) { // TEN
				tFluteLength++;
				if (pCards.contains(Card.getCard(pSuit, Rank.KING))) { // KING
					tFluteLength++;
					if (pCards.contains(Card.getCard(pSuit, Rank.QUEEN))) { // QUEEN
						tFluteLength++;
					}
					if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) { // NINE
						tFluteLength++;
					}
					if (pCards.contains(Card.getCard(pSuit, Rank.EIGHT))) { // EIGHT
						tFluteLength++;
					}
					if (pCards.contains(Card.getCard(pSuit, Rank.SEVEN))) { // SEVEN
						tFluteLength++;
					}
				}
			}
		}
		return tFluteLength;
	}

	/**
	 * Gets the maximum bid value of the player
	 *
	 * @return maximum bid value
	 */
	public int getMaxBid() {
		return oMaxBid;
	}

	/**
	 * tells the AI player whether to pick up the skat or not
	 *
	 * @return true, if the skat should be picked up;<br>
	 *         false, for a hand game
	 */
	public boolean canPlayHandGame() {
		switch (oSuggestedGameType) {
		case GRAND:
			return oGrandHand;
		case NULL:
			return oNullHand;
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
			return oSuitHand;
		}
		return false;
	}

	public boolean canPlayOuvert() {
		switch (oSuggestedGameType) {
		case NULL:
			return oNullOuvert;
		case GRAND:
			if (oGrandSchneider && oGrandHand && oGrandSchwarz) {
				return oGrandOuvert;
			}
			break;
		default:
		}
		return false;
	}

	public boolean canPlaySchneider() {
		switch (oSuggestedGameType) {
		case GRAND:
			if (oGrandHand) {
				return oGrandSchneider;
			}
			break;
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
			if (oSuitHand) {
				return oSuitSchneider;
			}
			break;
		default:
		}
		return false;
	}

	public boolean canPlaySchwarz() {
		switch (oSuggestedGameType) {
		case GRAND:
			if (oGrandHand && oGrandSchneider) {
				return oGrandSchwarz;
			}
			break;
		default:
		}
		return false;
	}

	public GameType getSuggestedGameType() {
		return oSuggestedGameType;
	}

	public Suit getSuggestedTrumpSuit() {
		return oSuggestedTrumpSuit;
	}
}