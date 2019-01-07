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
package org.jskat.util;

import org.jskat.util.rule.SkatRuleFactory;
import org.jskat.util.rule.SuitGrandRamschRule;

/**
 * All cards in a skat game
 */
public enum Card {

	/**
	 * Ace of clubs
	 */
	CA(Suit.CLUBS, Rank.ACE),
	/**
	 * Ten of clubs
	 */
	CT(Suit.CLUBS, Rank.TEN),
	/**
	 * King of clubs
	 */
	CK(Suit.CLUBS, Rank.KING),
	/**
	 * Queen of clubs
	 */
	CQ(Suit.CLUBS, Rank.QUEEN),
	/**
	 * Jack of clubs
	 */
	CJ(Suit.CLUBS, Rank.JACK),
	/**
	 * Nine of clubs
	 */
	C9(Suit.CLUBS, Rank.NINE),
	/**
	 * Eight of clubs
	 */
	C8(Suit.CLUBS, Rank.EIGHT),
	/**
	 * Seven of clubs
	 */
	C7(Suit.CLUBS, Rank.SEVEN),
	/**
	 * Ace of spades
	 */
	SA(Suit.SPADES, Rank.ACE),
	/**
	 * Ten of spades
	 */
	ST(Suit.SPADES, Rank.TEN),
	/**
	 * King of spades
	 */
	SK(Suit.SPADES, Rank.KING),
	/**
	 * Queen of spades
	 */
	SQ(Suit.SPADES, Rank.QUEEN),
	/**
	 * Jack of spades
	 */
	SJ(Suit.SPADES, Rank.JACK),
	/**
	 * Nine of spades
	 */
	S9(Suit.SPADES, Rank.NINE),
	/**
	 * Eight of spades
	 */
	S8(Suit.SPADES, Rank.EIGHT),
	/**
	 * Seven of spades
	 */
	S7(Suit.SPADES, Rank.SEVEN),
	/**
	 * Ace of hearts
	 */
	HA(Suit.HEARTS, Rank.ACE),
	/**
	 * Ten of hearts
	 */
	HT(Suit.HEARTS, Rank.TEN),
	/**
	 * King of hearts
	 */
	HK(Suit.HEARTS, Rank.KING),
	/**
	 * Queen of hearts
	 */
	HQ(Suit.HEARTS, Rank.QUEEN),
	/**
	 * Jack of hearts
	 */
	HJ(Suit.HEARTS, Rank.JACK),
	/**
	 * Nine of hearts
	 */
	H9(Suit.HEARTS, Rank.NINE),
	/**
	 * Eight of hearts
	 */
	H8(Suit.HEARTS, Rank.EIGHT),
	/**
	 * Seven of hearts
	 */
	H7(Suit.HEARTS, Rank.SEVEN),
	/**
	 * Ace of diamonds
	 */
	DA(Suit.DIAMONDS, Rank.ACE),
	/**
	 * Ten of diamonds
	 */
	DT(Suit.DIAMONDS, Rank.TEN),
	/**
	 * King of diamonds
	 */
	DK(Suit.DIAMONDS, Rank.KING),
	/**
	 * Queen of diamonds
	 */
	DQ(Suit.DIAMONDS, Rank.QUEEN),
	/**
	 * Jack of diamonds
	 */
	DJ(Suit.DIAMONDS, Rank.JACK),
	/**
	 * Nine of diamonds
	 */
	D9(Suit.DIAMONDS, Rank.NINE),
	/**
	 * Eight of diamonds
	 */
	D8(Suit.DIAMONDS, Rank.EIGHT),
	/**
	 * Seven of diamonds
	 */
	D7(Suit.DIAMONDS, Rank.SEVEN);

	private final Suit suit;
	private final Rank rank;

	private Card(final Suit newSuit, final Rank newRank) {

		suit = newSuit;
		rank = newRank;
	}

	/**
	 * Gets the suit of the card
	 *
	 * @return Suit of the card
	 */
	public Suit getSuit() {

		return suit;
	}

	/**
	 * Gets the rank of the card
	 *
	 * @return Rank of the card
	 */
	public Rank getRank() {

		return rank;
	}

	/**
	 * Compares the Cards whether the suit is the same or not
	 *
	 * @param card
	 *            Card to compare to
	 * @return TRUE if the suits are the same
	 */
	public boolean isSameSuit(final Card card) {

		return suit.equals(card.getSuit());
	}

	/**
	 * Compares the Cards whether the rank is the same or not
	 *
	 * @param card
	 *            Card to compare to
	 * @return TRUE if the ranks are the same
	 */
	public boolean isSameRank(final Card card) {

		return rank.equals(card.getRank());
	}

	/**
	 * Get the card value
	 *
	 * @return Points of the card
	 */
	public int getPoints() {

		return rank.getPoints();
	}

	/**
	 * Get the card order value for suit or grand games
	 *
	 * @return Order of the card in suit or Grand games
	 */
	public int getSuitGrandOrder() {

		return rank.getSuitGrandOrder();
	}

	/**
	 * Get the card order value for null games
	 *
	 * @return Order of the card in null games
	 */
	public int getNullOrder() {

		return rank.getNullOrder();
	}

	/**
	 * Get the card order value for ramsch games
	 *
	 * @return Order of the card in ramsch games
	 */
	public int getRamschOrder() {

		return rank.getRamschOrder();
	}

	/**
	 * Checks whether a Card is allowed to be played
	 *
	 * @param gameType
	 *            The game type within the card is checked
	 * @param initialCard
	 *            First card played in the trick
	 * @param hand
	 *            All cards on players hand
	 * @return TRUE, when the card is allowed to be played
	 */
	public boolean isAllowed(final GameType gameType, final Card initialCard,
			final CardList hand) {

		boolean result = false;

		if (gameType != GameType.PASSED_IN) {
			result = SkatRuleFactory.getSkatRules(gameType).isCardAllowed(
					gameType, initialCard, hand, this);
		}

		return result;
	}

	/**
	 * Checks whether a Card is a trump card or not
	 *
	 * @param gameType
	 *            The game type within the card is checked
	 * @return TRUE, when the card is a trump card
	 */
	public boolean isTrump(final GameType gameType) {

		boolean result = false;

		if (gameType != GameType.NULL) {

			result = ((SuitGrandRamschRule) SkatRuleFactory
					.getSkatRules(gameType)).isTrump(gameType, this);
		}

		return result;
	}

	/**
	 * Checks whether the card beats another given card under the current game type
	 * and trump color with respect to the initial card on the table
	 *
	 * @param gameType
	 *            Game type
	 * @param cardToBeat
	 *            Card to beat
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(final GameType gameType, final Card cardToBeat) {

		return SkatRuleFactory.getSkatRules(gameType).isCardBeatsCard(gameType,
				cardToBeat, this);
	}

	/**
	 * Creates a list of all cards that would beat the given card under the current
	 * game type and trump color with respect to the initial card on the table
	 *
	 * @param gameType
	 *            Game type
	 * @param cardToBeat
	 *            Card to beat
	 * @return a CardList with all the cards that would beat the initial card
	 */
	public static CardList getBeatingCards(final GameType gameType,
			final Card cardToBeat) {
		// TODO (mjl 23.08.2011) write unit tests for Card.getBeatingCards()
		// FIXME (mjl 05.09.2011) is this supposed to consider trump cards?
		final CardList beatingCards = new CardList();
		for (final Card card : Card.values()) {
			if (card.beats(gameType, cardToBeat)) {
				beatingCards.add(card);
			}
		}
		return beatingCards;
	}

	/**
	 * Gets a card from a string
	 *
	 * @param cardAsString
	 *            Card as string
	 * @return Card
	 */
	public static Card getCardFromString(final String cardAsString) {

		final Suit suit = Suit.getSuitFromString(cardAsString);
		final Rank rank = Rank.getRankFromString(cardAsString);

		return getCard(suit, rank);
	}

	/**
	 * Gets a card with suit and rank
	 *
	 * @param suit
	 *            Suit
	 * @param rank
	 *            Rank
	 * @return Card
	 */
	public static Card getCard(final Suit suit, final Rank rank) {

		Card result = null;

		for (final Card card : Card.values()) {

			if (card.getSuit() == suit && card.getRank() == rank) {

				result = card;
			}
		}

		return result;
	}

	/**
	 * converts the rank of a card to a specific int value (7=1, 8=2, 9=4, ... A=64,
	 * J=128)
	 *
	 * @return an int representation of the card's rank
	 */
	int toBinaryFlag() {
		return getRank().toBinaryFlag();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return suit.getSymbol() + rank.getShortString();
	}
}
