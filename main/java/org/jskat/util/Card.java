/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0
 * Copyright (C) 2012-03-13
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
package org.jskat.util;

import org.jskat.util.rule.SkatRuleFactory;
import org.jskat.util.rule.SuitGrandRamschRules;

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

	private Card(Suit newSuit, Rank newRank) {

		this.suit = newSuit;
		this.rank = newRank;
	}

	/**
	 * Gets the suit of the card
	 * 
	 * @return Suit of the card
	 */
	public Suit getSuit() {

		return this.suit;
	}

	/**
	 * Gets the rank of the card
	 * 
	 * @return Rank of the card
	 */
	public Rank getRank() {

		return this.rank;
	}

	/**
	 * Compares the Cards whether the suit is the same or not
	 * 
	 * @param card
	 *            Card to compare to
	 * @return TRUE if the suits are the same
	 */
	public boolean isSameSuit(Card card) {

		return this.suit == card.getSuit();
	}

	/**
	 * Compares the Cards whether the rank is the same or not
	 * 
	 * @param card
	 *            Card to compare to
	 * @return TRUE if the ranks are the same
	 */
	public boolean isSameRank(Card card) {

		return this.rank == card.getRank();
	}

	/**
	 * Get the card value
	 * 
	 * @return Points of the card
	 */
	public int getPoints() {

		return this.rank.getPoints();
	}

	/**
	 * Get the card order value for suit or grand games
	 * 
	 * @return Order of the card in suit or Grand games
	 */
	public int getSuitGrandOrder() {

		return this.rank.getSuitGrandOrder();
	}

	/**
	 * Get the card order value for null games
	 * 
	 * @return Order of the card in null games
	 */
	public int getNullOrder() {

		return this.rank.getNullOrder();
	}

	/**
	 * Get the card order value for ramsch games
	 * 
	 * @return Order of the card in ramsch games
	 */
	public int getRamschOrder() {

		return this.rank.getRamschOrder();
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
	public boolean isAllowed(GameType gameType, Card initialCard, CardList hand) {

		boolean result = false;

		if (gameType != GameType.PASSED_IN) {
			result = SkatRuleFactory.getSkatRules(gameType).isCardAllowed(gameType, initialCard, hand, this);
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
	public boolean isTrump(GameType gameType) {

		boolean result = false;

		if (gameType != GameType.NULL) {

			result = ((SuitGrandRamschRules) SkatRuleFactory.getSkatRules(gameType)).isTrump(gameType, this);
		}

		return result;
	}

	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trump color with respect to the initial card on the table
	 * 
	 * @param gameType
	 *            Game type
	 * @param cardToBeat
	 *            Card to beat
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(GameType gameType, Card cardToBeat) {

		return SkatRuleFactory.getSkatRules(gameType).isCardBeatsCard(gameType, cardToBeat, this);
	}

	/**
	 * Creates a list of all cards that would beat the given card under the
	 * current game type and trump color with respect to the initial card on the
	 * table
	 * 
	 * @param gameType
	 *            Game type
	 * @param cardToBeat
	 *            Card to beat
	 * @return a CardList with all the cards that would beat the initial card
	 */
	public static CardList getBeatingCards(GameType gameType, Card cardToBeat) {
		// TODO (mjl 23.08.2011) write unit tests for Card.getBeatingCards()
		// FIXME (mjl 05.09.2011) is this supposed to consider trump cards?
		CardList beatingCards = new CardList();
		for (Card card : Card.values()) {
			if (card.beats(gameType, cardToBeat))
				beatingCards.add(card);
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
	public static Card getCardFromString(String cardAsString) {

		Suit suit = Suit.getSuitFromString(cardAsString);
		Rank rank = Rank.getRankFromString(cardAsString);

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
	public static Card getCard(Suit suit, Rank rank) {

		Card result = null;

		for (Card card : Card.values()) {

			if (card.getSuit() == suit && card.getRank() == rank) {

				result = card;
			}
		}

		return result;
	}

	/**
	 * converts the rank of a card to a specific int value (7=1, 8=2, 9=4, ...
	 * A=64, J=128)
	 * 
	 * @return an int representation of the card's rank
	 */
	int toBinaryFlag() {
		return this.getRank().toBinaryFlag();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return this.suit.shortString() + "-" + this.rank.shortString(); //$NON-NLS-1$
	}
}
