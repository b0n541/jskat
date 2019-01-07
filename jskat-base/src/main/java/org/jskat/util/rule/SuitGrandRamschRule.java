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
package org.jskat.util.rule;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

/**
 * Rules that are in common for suit, grand and ramsch games
 */
public abstract class SuitGrandRamschRule extends AbstractSkatRule {

	/**
	 * @see SkatRule#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Override
	public boolean isCardAllowed(GameType gameType, Card initialCard,
			CardList hand, Card card) {

		boolean result = false;

		if (initialCard == null) {
			// no initial card given --> every card is allowed
			result = true;
		} else if (initialCard.isTrump(gameType)) {

			if (card.isTrump(gameType)) {
				// card must serve trump
				result = true;
			} else if (!hand.hasTrump(gameType)) {
				// no trump on hand --> every card is allowed
				result = true;
			}
		} else if (initialCard.getSuit() == card.getSuit()
				&& !card.isTrump(gameType)) {
			// card must serve suit
			result = true;
		} else if (!hand.hasSuit(gameType, initialCard.getSuit())) {
			// no suit on hand --> every card is allowed
			result = true;
		}

		return result;
	}

	/**
	 * @see SkatRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Override
	public boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card) {

		boolean result = false;

		if (cardToBeat.isTrump(gameType)) {
			// card to beat is a trump card
			if (card.isTrump(gameType)) {

				if (cardToBeat.getSuitGrandOrder() < card.getSuitGrandOrder()) {
					// card is a trump card too and has higher suit order
					result = true;
				} else if (cardToBeat.getSuitGrandOrder() == card
						.getSuitGrandOrder()) {
					// cards have same suit grand order
					// only possible if two jacks are checked
					if (cardToBeat.getSuit().getSortOrder() < card.getSuit()
							.getSortOrder()) {

						result = true;
					}
				}
			}
		} else {
			// card to beat is not a trump card
			if (card.isTrump(gameType)) {
				// card is a trump card
				result = true;
			} else if (cardToBeat.getSuit() == card.getSuit()
					&& cardToBeat.getSuitGrandOrder() < card
							.getSuitGrandOrder()) {
				// cards have the same suit and card has higher order in
				// suit/grand games
				result = true;
			}
		}

		return result;
	}

	/**
	 * @see SkatRule#hasSuit(GameType, CardList, Suit)
	 */
	@Override
	public boolean hasSuit(GameType gameType, CardList hand, Suit suit) {
		for (Card card : hand) {
			if (card.getSuit() == suit && !card.isTrump(gameType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a card is a trump card
	 * 
	 * @param gameType
	 *            Game type in which the Card is checked
	 * @param card
	 *            Card to be checked
	 * @return TRUE if the card is a trump card
	 */
	public boolean isTrump(GameType gameType, Card card) {

		boolean result = false;

		if (gameType == GameType.GRAND || gameType == GameType.RAMSCH) {

			result = card.getRank() == Rank.JACK;

		} else {

			boolean isSuitTrump = false;

			// first check for a Jack
			boolean isJack = card.getRank() == Rank.JACK;

			if (!isJack) {
				// then check the suit
				Suit suit = card.getSuit();

				if (gameType == GameType.CLUBS && suit == Suit.CLUBS
						|| gameType == GameType.SPADES && suit == Suit.SPADES
						|| gameType == GameType.HEARTS && suit == Suit.HEARTS
						|| gameType == GameType.DIAMONDS
						&& suit == Suit.DIAMONDS) {

					isSuitTrump = true;
				}
			}

			result = isJack || isSuitTrump;
		}

		return result;
	}
}
