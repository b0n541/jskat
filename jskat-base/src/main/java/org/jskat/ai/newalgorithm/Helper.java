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
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Loreck (daniel.loreck@gmail.com)
 * 
 */
public class Helper {

	private static Logger oLog = LoggerFactory.getLogger(Helper.class);

	public static Card getHighestSuitTrump(CardList pCards, Suit pSuit) {
		return pCards.get(0);
	}

	public static Card getHighestJack(CardList pCards) {
		return pCards.get(0);
	}

	public static int getHighestSuitTrumpBinary(CardList pCards, Suit pSuit) {
		int tJackBinary = getHighestJackBinary(pCards);
		if (tJackBinary > 0) {
			return tJackBinary;
		}

		Card tHighestTrump = pCards.get(0);
		if (tHighestTrump.getSuit() == pSuit) {
			return tHighestTrump.getRank().toBinaryFlag();
		}

		return 0; // NO Trump
	}

	public static int getHighestJackBinary(CardList pCards) {
		Card tHighestTrump = pCards.get(0);
		if (tHighestTrump.getRank() == Rank.JACK) {
			if (pCards.contains(Card.CJ)) {
				return 1024;
			}
			if (pCards.contains(Card.SJ)) {
				return 512;
			}
			if (pCards.contains(Card.HJ)) {
				return 256;
			}
			return 128; // DIAMONDS
		}

		return 0; // NO Jack
	}

	public static int getSuitCardsToBinary(CardList pCards, Suit pSuit) {
		int tBinary = 0;
		if (pCards.getSuitCount(pSuit, false) > 0) {
			for (int i = pCards.getFirstIndexOfSuit(pSuit, false); i <= pCards
					.getLastIndexOfSuit(pSuit, false); i++) {
				tBinary += pCards.get(i).getRank().toBinaryFlag();
			}
		}
		return tBinary;
	}

	public static int getOpponentsTrumpCount(CardList pCards, Suit pSuit) {
		return 11 - pCards.getSuitCount(pSuit, false);
	}

	public static int getGrandBinary(CardList pCards) {
		int tBinary = 0;
		if (pCards.contains(Card.CJ)) {
			tBinary += 1024;
		}
		if (pCards.contains(Card.SJ)) {
			tBinary += 512;
		}
		if (pCards.contains(Card.HJ)) {
			tBinary += 256;
		}
		if (pCards.contains(Card.DJ)) {
			tBinary += 128;
		}
		return tBinary;
	}

	public static boolean isHighestTrumpCard(Card pPossibleHighestCard,
			GameType pGameType, CardList pPlayedCards) {
		if (null == pPlayedCards) {
			pPlayedCards = new CardList();
		}
		if ((pPossibleHighestCard.getRank() == Rank.JACK || pPossibleHighestCard
				.getSuit() == pGameType.getTrumpSuit())
				&& getTrumpCardsToBinary(pPlayedCards, pGameType.getTrumpSuit())
						+ (2 * pPossibleHighestCard.getRank().toBinaryFlag()) > 2047) {
			return true;
		}
		return false;
	}

	public static boolean isHighestSuitCard(Card pPossibleHighestCard,
			GameType pGameType, CardList pPlayedCards, CardList pTrickCards) {
		if (pPossibleHighestCard.getSuit() == pGameType.getTrumpSuit()) {
			return isHighestTrumpCard(pPossibleHighestCard, pGameType,
					pPlayedCards);
		}
		return isHighestSuitCard(pPossibleHighestCard, pPlayedCards,
				pTrickCards);
	}

	public static boolean isHighestSuitCard(Card pPossibleHighestCard,
			CardList pPlayedCards, CardList pTrickCards) {
		Suit currentSuit = null;
		if (null == pPlayedCards) {
			pPlayedCards = new CardList();
		}
		if (null == pTrickCards) {
			pTrickCards = new CardList();
		}

		if (!pTrickCards.isEmpty()) {
			currentSuit = pTrickCards.get(0).getSuit();
		} else {
			currentSuit = pPossibleHighestCard.getSuit();
		}

		if (pPossibleHighestCard.getSuit() == currentSuit
				&& getSuitCardsToBinary(pPlayedCards, currentSuit)
						+ (2 * pPossibleHighestCard.getRank().toBinaryFlag()) > 127) {
			return true;
		}
		return false;
	}

	public static int getTrumpCardsToBinary(CardList pCards, Suit pSuit) {
		int tBinary = getGrandBinary(pCards);
		tBinary += getSuitCardsToBinary(pCards, pSuit);
		return tBinary;
	}

	public static int getNullSuitCardsToBinary(CardList pCards, Suit pSuit) {
		int counter = 0;
		if (pCards.contains(Card.getCard(pSuit, Rank.SEVEN))) {
			counter += 128;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.EIGHT))) {
			counter += 64;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) {
			counter += 32;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.TEN))) {
			counter += 16;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.JACK))) {
			counter += 8;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.QUEEN))) {
			counter += 4;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.KING))) {
			counter += 2;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.ACE))) {
			counter += 1;
		}
		return counter;
	}

	public static Card getHighestValueCard(CardList pCards, Suit pSuit,
			boolean includeJacks) {
		if ((!includeJacks && pCards.getSuitCount(pSuit, false) == 0)
				|| (includeJacks && pCards.getSuitCount(pSuit, false)
						+ Helper.countJacks(pCards) == 0)) {
			return null;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.ACE))) {
			return Card.getCard(pSuit, Rank.ACE);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.TEN))) {
			return Card.getCard(pSuit, Rank.TEN);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.KING))) {
			return Card.getCard(pSuit, Rank.KING);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.QUEEN))) {
			return Card.getCard(pSuit, Rank.QUEEN);
		}
		if (pCards.contains(Card.DJ) && includeJacks) {
			return Card.DJ;
		}
		if (pCards.contains(Card.HJ) && includeJacks) {
			return Card.HJ;
		}
		if (pCards.contains(Card.SJ) && includeJacks) {
			return Card.SJ;
		}
		if (pCards.contains(Card.CJ) && includeJacks) {
			return Card.CJ;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.SEVEN))) {
			return Card.getCard(pSuit, Rank.SEVEN);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.EIGHT))) {
			return Card.getCard(pSuit, Rank.EIGHT);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) {
			return Card.getCard(pSuit, Rank.NINE);
		}
		return null;
	}

	public static Card getLowestValueCard(CardList pCards, Suit pSuit) {
		if (pCards.getSuitCount(pSuit, false) == 0) {
			return null;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.SEVEN))) {
			return Card.getCard(pSuit, Rank.SEVEN);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.EIGHT))) {
			return Card.getCard(pSuit, Rank.EIGHT);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) {
			return Card.getCard(pSuit, Rank.NINE);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.QUEEN))) {
			return Card.getCard(pSuit, Rank.QUEEN);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.KING))) {
			return Card.getCard(pSuit, Rank.KING);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.TEN))) {
			return Card.getCard(pSuit, Rank.TEN);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.ACE))) {
			return Card.getCard(pSuit, Rank.ACE);
		}
		return null;
	}

	public static Card getLowestTrumpValueCard(CardList pCards, Suit pSuit,
			boolean includeJacks) {
		if ((!includeJacks && pCards.getSuitCount(pSuit, false) == 0)
				|| (includeJacks && pCards.getSuitCount(pSuit, false)
						+ Helper.countJacks(pCards) == 0)) {
			return null;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.SEVEN))) {
			return Card.getCard(pSuit, Rank.SEVEN);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.EIGHT))) {
			return Card.getCard(pSuit, Rank.EIGHT);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.NINE))) {
			return Card.getCard(pSuit, Rank.NINE);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.QUEEN))) {
			return Card.getCard(pSuit, Rank.QUEEN);
		}
		if (pCards.contains(Card.DJ) && includeJacks) {
			return Card.DJ;
		}
		if (pCards.contains(Card.HJ) && includeJacks) {
			return Card.HJ;
		}
		if (pCards.contains(Card.SJ) && includeJacks) {
			return Card.SJ;
		}
		if (pCards.contains(Card.CJ) && includeJacks) {
			return Card.CJ;
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.KING))) {
			return Card.getCard(pSuit, Rank.KING);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.TEN))) {
			return Card.getCard(pSuit, Rank.TEN);
		}
		if (pCards.contains(Card.getCard(pSuit, Rank.ACE))) {
			return Card.getCard(pSuit, Rank.ACE);
		}
		return null;
	}

	public static int getGrandMultiplier(CardList pCards) {
		int tMultiplier = 2;

		if (pCards.contains(Card.CJ)) { // With Jacks
			if (pCards.contains(Card.SJ)) {
				tMultiplier++;
				if (pCards.contains(Card.HJ)) {
					tMultiplier++;
					if (pCards.contains(Card.DJ)) {
						tMultiplier++;
					}
				}
			}
		} else { // Without Jacks
			if (!pCards.contains(Card.SJ)) {
				tMultiplier++;
				if (!pCards.contains(Card.HJ)) {
					tMultiplier++;
					if (!pCards.contains(Card.DJ)) {
						tMultiplier++;
					}
				}
			}
		}
		return tMultiplier;
	}

	public static int getSuitMultiplier(CardList pCards, Suit pSuit) {
		int tMultiplier = 2;
		int tCardsBinary = getTrumpCardsToBinary(pCards, pSuit);
		int tStartBinary = 1024;

		if ((tCardsBinary & tStartBinary) > 0) { // With
			while ((tCardsBinary & (tStartBinary = tStartBinary >> 1)) > 0) {
				tMultiplier++;
			}
		} else { // Without
			while ((tCardsBinary & (tStartBinary = tStartBinary >> 1)) == 0) {
				tMultiplier++;
			}
		}
		return tMultiplier;
	}

	/**
	 * Gets a binary representation of the jacks in the given hand
	 * 
	 * @param cards
	 *            a hand
	 * @return binary value of the available jacks
	 */
	public static int getJacks(CardList cards) {
		int counter = 0;
		if (cards.contains(Card.CJ)) {
			counter += 1;
		}
		if (cards.contains(Card.SJ)) {
			counter += 2;
		}
		if (cards.contains(Card.HJ)) {
			counter += 4;
		}
		if (cards.contains(Card.DJ)) {
			counter += 8;
		}
		return counter;
	}

	/**
	 * Gets the number of jacks in the given hand
	 * 
	 * @param pCards
	 *            a hand
	 * @return number of jacks
	 */
	public static int countJacks(CardList pCards) {
		CardList x = new CardList(pCards);
		x.sort(GameType.GRAND);
		int counter = 0;
		for (Card lCard : x) {
			if (lCard.getRank() == Rank.JACK) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Converts a binary stream to a suit color
	 * 
	 * @param binary
	 *            binary stream (four bits)
	 * @return suit color, -1 if more than one bit is set
	 */
	public static Suit binaryToSuit(int binary) {
		Suit result = null;
		switch (binary) {
		case 1:
			return Suit.DIAMONDS;
		case 2:
			return Suit.HEARTS;
		case 3:
			return Suit.SPADES;
		case 4:
			return Suit.CLUBS;
		}
		oLog.warn(".binaryToSuit(): warning: more than one suit possible! -->"
				+ binary);
		return result;
	}
}