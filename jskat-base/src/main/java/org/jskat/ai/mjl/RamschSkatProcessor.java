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
package org.jskat.ai.mjl;

import java.util.Vector;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius (markus@luzius.de)
 * 
 */
public class RamschSkatProcessor {

	private static Logger log = LoggerFactory
			.getLogger(RamschSkatProcessor.class);

	private void testProcessor(final CardList cards, final CardList skat) {
		int[] cardBin = new int[4];
		cardBin[3] = Helper.suitCardsToBinaryWithSkat(cards, skat,
				Suit.DIAMONDS);
		cardBin[2] = Helper.suitCardsToBinaryWithSkat(cards, skat, Suit.HEARTS);
		cardBin[1] = Helper.suitCardsToBinaryWithSkat(cards, skat, Suit.SPADES);
		cardBin[0] = Helper.suitCardsToBinaryWithSkat(cards, skat, Suit.CLUBS);

		Vector<Double> relevance = new Vector<Double>();
		cards.add(skat.remove(0));
		cards.add(skat.remove(0));
		for (int i = 0; i < cards.size(); i++) {
			Card c = cards.get(i);
			// TODO (js) changed due to refactoring,
			// don't know if it's still work as intended
			// double rel = 1.0 * (double) c.getRank() / 6.0;
			double rel = 1.0 * c.getRank().getSuitGrandOrder() / 6.0;
			if (c.getRank() == Rank.JACK) {
				rel = 0.0;
			}
			log.debug("Card(" + i + "): " + c + ", Rel=" + rel);
			// TODO (js) changed due to refactoring,
			// don't know if it's still work as intended
			// int tmpBin = cardBin[c.getSuit()];
			int tmpBin = Helper.suitCardsToBinaryWithSkat(cards, skat,
					c.getSuit());
			log.debug("suit=" + bin(tmpBin, 8) + " & 15 = " + (tmpBin & 15));
			if (Tools.isIn(tmpBin & 15, new int[] { 7, 11, 13 })) {
				rel = 0.0;
			} else if (Tools.isIn(tmpBin & 15, new int[] { 3, 5 })) {
				rel = rel * 0.1;
			} else if (Tools.isIn(tmpBin & 15, new int[] { 1, 2, 6, 9 })) {
				rel = rel * 0.4;
			} else if (Tools.isIn(tmpBin & 15, new int[] { 4, 10 })) {
				rel = rel * 0.5;
			}
			log.debug("Card(" + i + "): " + c + ", Rel=" + rel);
			relevance.add(new Double(rel));
		}
		log.debug("cards    =" + cards);
		log.debug("relevance=" + relevance);

		int maxIndex = findMax(relevance);
		log.debug("Max: " + maxIndex);
		skat.add(cards.remove(maxIndex));
		relevance.remove(maxIndex);
		maxIndex = findMax(relevance);
		log.debug("Max: " + maxIndex);
		skat.add(cards.remove(maxIndex));
		relevance.remove(maxIndex);
		log.debug("New skat: " + skat);
	}

	/**
	 * Processes the skat for a Ramsch game
	 * 
	 * @param cards
	 *            the player's hand
	 * @param skat
	 *            the skat
	 */
	public void processSkat(final CardList cards, final CardList skat) {
		log.debug("\n\n================================================================");
		testProcessor(cards, skat);
		log.debug("\n================================================================\n\n");
		// TODO (mjl) check for potential Durchmarsch when processing the skat
		log.debug("My cards:" + cards + ", Skat=" + skat);
		// int cDiamonds = cards.getSuitCount(GameType.RAMSCH, Suit.DIAMONDS);
		// int cHearts = cards.getSuitCount(GameType.RAMSCH, Suit.HEARTS);
		// int cSpades = cards.getSuitCount(GameType.RAMSCH, Suit.SPADES);
		// int cClubs = cards.getSuitCount(GameType.RAMSCH, Suit.CLUBS);
		//
		// cDiamonds += skat.getSuitCount(GameType.RAMSCH, Suit.DIAMONDS);
		// cHearts += skat.getSuitCount(GameType.RAMSCH, Suit.HEARTS);
		// cSpades += skat.getSuitCount(GameType.RAMSCH, Suit.SPADES);
		// cClubs += skat.getSuitCount(GameType.RAMSCH, Suit.CLUBS);
		//
		// log.debug("#: C="+cClubs+", S="+cSpades+", H="+cHearts+", D="+cDiamonds);
		//
		// int diamonds = Helper.suitCardsToBinaryWithSkat(cards, skat,
		// Suit.DIAMONDS);
		// int hearts = Helper.suitCardsToBinaryWithSkat(cards, skat,
		// Suit.HEARTS);
		// int spades = Helper.suitCardsToBinaryWithSkat(cards, skat,
		// Suit.SPADES);
		// int clubs = Helper.suitCardsToBinaryWithSkat(cards, skat,
		// Suit.CLUBS);
		//
		// log.debug("cards: C="+bin(clubs,8)+", S="+bin(spades,8)+", H="+bin(hearts,8)+", D="+bin(diamonds,8));
		//
		// int saveSuits = 0;
		// if(Tools.isIn((diamonds & 31), new int[] {7,11,13,15,19,21}))
		// saveSuits += 1;
		// if(Tools.isIn((hearts & 31), new int[] {7,11,13,15,19,21})) saveSuits
		// += 2;
		// if(Tools.isIn((spades & 31), new int[] {7,11,13,15,19,21})) saveSuits
		// += 4;
		// if(Tools.isIn((clubs & 31), new int[] {7,11,13,15,19,21})) saveSuits
		// += 8;
		// log.debug("saveSuits="+bin(saveSuits,4));
		//
		// int possibleSkatSuits = 0;
		// if (cDiamonds > 0 && cDiamonds < 3 && (diamonds & 64) == 0 &&
		// !Tools.isIn((diamonds & 7), new int[] {1,3,5})) possibleSkatSuits +=
		// 1;
		// if (cHearts > 0 && cHearts < 3 && (hearts & 64) == 0 &&
		// !Tools.isIn((hearts & 7), new int[] {1,3,5})) possibleSkatSuits += 2;
		// if (cSpades > 0 && cSpades < 3 && (spades & 64) == 0 &&
		// !Tools.isIn((spades & 7), new int[] {1,3,5})) possibleSkatSuits += 4;
		// if (cClubs > 0 && cClubs < 3 && (clubs & 64) == 0 &&
		// !Tools.isIn((clubs & 7), new int[] {1,3,5})) possibleSkatSuits += 8;
		// log.debug("possibleSkatSuits="+bin(possibleSkatSuits,4));
		//
		// Card skatOne = skat.remove(0);
		// Card skatTwo = skat.remove(0);
		//
		// if (possibleSkatSuits > 0) {
		// Suit skatSuit = Helper.binaryToSuit(possibleSkatSuits);
		// if(skatSuit != null) {
		// // if more than one possible color: do nothing for the time being
		// // TODO (mjl): skat processing with more than one possible color
		// log.debug("More than one possible color found...");
		// if(skatOne.getRank()==Rank.JACK) {
		// int index = 0;
		// while(cards.get(index).getRank()==Rank.JACK) index++;
		// skat.add(cards.remove(index));
		// cards.add(skatOne);
		// } else {
		// skat.add(skatOne);
		// }
		// if(skatTwo.getRank()==Rank.JACK) {
		// int index = 0;
		// while(cards.get(index).getRank()==Rank.JACK) index++;
		// skat.add(cards.remove(index));
		// cards.add(skatTwo);
		// } else {
		// skat.add(skatTwo);
		// }
		// }
		// else {
		// log.debug("Color for skat:"+skatSuit);
		// if(skatOne.getSuit() == skatSuit ||
		// cards.getSuitCount(GameType.RAMSCH, skatSuit)<1) {
		// if(skatOne.getRank()==Rank.JACK) {
		// int index = 0;
		// while(cards.get(index).getRank()==Rank.JACK) index++;
		// skat.add(cards.remove(index));
		// cards.add(skatOne);
		// } else {
		// skat.add(skatOne);
		// }
		// } else {
		// skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
		// cards.add(skatOne);
		// }
		// if(skatTwo.getSuit()==skatSuit || cards.getSuitCount(GameType.RAMSCH,
		// skatSuit)<1) {
		// if(skatTwo.getRank()==Rank.JACK) {
		// int index = 0;
		// while(cards.get(index).getRank()==Rank.JACK) index++;
		// skat.add(cards.remove(index));
		// cards.add(skatTwo);
		// } else {
		// skat.add(skatTwo);
		// }
		// } else {
		// skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
		// cards.add(skatTwo);
		// }
		// }
		// } else {
		// // if more than one possible color: do nothing for the time being
		// // TODO: skat processing with no possible color from the first
		// iteration
		// if(skatOne.getRank()==Rank.JACK) {
		// int index = 0;
		// while(cards.get(index).getRank()==Rank.JACK) index++;
		// skat.add(cards.remove(index));
		// cards.add(skatOne);
		// } else {
		// skat.add(skatOne);
		// }
		// if(skatTwo.getRank()==Rank.JACK) {
		// int index = 0;
		// while(cards.get(index).getRank()==Rank.JACK) index++;
		// skat.add(cards.remove(index));
		// cards.add(skatTwo);
		// } else {
		// skat.add(skatTwo);
		// }
		// }

		log.debug("Done - my cards:" + cards + ", Skat=" + skat);
	}

	/**
	 * Decides, whether the AI player should look at the skat or rather play
	 * hand
	 * 
	 * @param cards
	 *            the player's hand
	 * @return true, if the player should look at the skat
	 */
	public boolean lookAtSkat(final CardList cards) {
		return true;
	}

	private int pow(final int a, final int b) {
		int res = 1;
		for (int i = 0; i < b; i++) {
			res = res * a;
		}
		return res;
	}

	private String bin(final int i, final int bits) {
		StringBuffer sb = new StringBuffer();
		for (int j = bits - 1; j >= 0; j--) {
			if ((i & pow(2, j)) > 0) {
				sb.append("X");
			} else {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	private int findMax(final Vector<Double> list) {
		int res = 0;
		int pos = 1;
		while (list.size() > pos) {
			if (list.get(pos).doubleValue() > list.get(res).doubleValue()) {
				res = pos;
			}
			pos++;
		}
		return res;
	}
}
