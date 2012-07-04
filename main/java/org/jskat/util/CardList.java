/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds Cards on a hand or in the Skat
 */
public class CardList implements Iterable<Card> {

	private static Logger log = LoggerFactory.getLogger(CardList.class);

	protected List<Card> cards = new ArrayList<Card>();

	/**
	 * Constructor
	 */
	public CardList() {

	}

	/**
	 * Constructor with predefined cards
	 * 
	 * @param newCards
	 *            Predefined cards
	 */
	public CardList(final List<Card> newCards) {
		cards.addAll(newCards);
	}

	/**
	 * Constructor with predefined cards
	 * 
	 * @param newCards
	 *            Predefined cards
	 */
	public CardList(final CardList newCards) {
		cards.addAll(newCards.cards);
	}

	/**
	 * Gets a copy of a card list that is immutable
	 * 
	 * @return Immutable copy of a card list
	 */
	public CardList getImmutableCopy() {
		CardList cardList = new CardList();
		cardList.setCards(Collections.unmodifiableList(cards));
		return cardList;
	}

	private void setCards(final List<Card> newCards) {
		cards = newCards;
	}

	/**
	 * @see Collection#remove(Object)
	 */
	public boolean remove(final Card card) {
		return cards.remove(card);
	}

	/**
	 * @see Collection#add(Object)
	 */
	public boolean add(final Card card) {
		return cards.add(card);
	}

	/**
	 * @see Collection#addAll(Collection)
	 */
	public boolean addAll(final CardList newCards) {
		return addAll(newCards.cards);
	}

	/**
	 * @see Collection#addAll(Collection)
	 */
	public boolean addAll(final Collection<Card> newCards) {
		return cards.addAll(newCards);
	}

	/**
	 * @see Collection#size()
	 */
	public int size() {
		return cards.size();
	}

	/**
	 * @see Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}

	/**
	 * @see Collection#iterator()
	 */
	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	/**
	 * @see Collection#contains(Object)
	 */
	public boolean contains(final Card card) {
		return cards.contains(card);
	}

	/**
	 * @see Collection#remove(int)
	 */
	public Card remove(final int index) {
		return cards.remove(index);
	}

	/**
	 * @see Collection#removeAll(Collection)
	 */
	public boolean removeAll(final Collection<Card> cardsToRemove) {
		return cards.removeAll(cardsToRemove);
	}

	/**
	 * @see Collection#removeAll(Collection)
	 */
	public boolean removeAll(final CardList cardsToRemove) {
		return removeAll(cardsToRemove.cards);
	}

	/**
	 * @see Collection#clear()
	 */
	public void clear() {
		cards.clear();
	}

	/**
	 * @see List#indexOf(Object)
	 */
	public int indexOf(final Card card) {
		return cards.indexOf(card);
	}

	/**
	 * Tests whether a card with a suit is in the CardList or not
	 * 
	 * @param gameType
	 *            Game type of the game played
	 * @param suit
	 *            Suit color
	 * @return TRUE, when a card with the suit is found
	 */
	public boolean hasSuit(final GameType gameType, final Suit suit) {

		return SkatRuleFactory.getSkatRules(gameType).hasSuit(gameType, this, suit);
	}

	/**
	 * Tests whether a trump card is in the CardList or not
	 * 
	 * @param gameType
	 *            Game type of the game played
	 * @return TRUE, when a trump card was found in the CardList
	 */
	public boolean hasTrump(final GameType gameType) {

		boolean result = false;

		int i = 0;
		while (result == false && i < cards.size()) {

			if (get(i).isTrump(gameType)) {

				result = true;
			}
			i++;
		}

		return result;
	}

	/**
	 * Checks whether a jack of a given suit is in the CardList
	 * 
	 * @param suit
	 *            Suit to check
	 * @return TRUE if the jack of the tested suit is in the CardList
	 */
	public boolean hasJack(final Suit suit) {

		boolean result = false;

		for (Card card : cards) {

			if (card.getRank() == Rank.JACK) {

				if (card.getSuit() == suit) {

					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * @see List#set(int, Object)
	 */
	public void set(final int index, final Card card) {
		cards.set(index, card);
	}

	/**
	 * Overrides the standard get method (@see {@link List#get(int)}) to handle
	 * the "not available" index value of -1, so that in this case the method
	 * returns null
	 */
	// FIXME (jansch 02.05.2012) seems wrong to me
	// Exceptions are used to control program execution
	public Card get(final int index) {
		try {
			return cards.get(index);
		} catch (IndexOutOfBoundsException e) {
			if (index == -1) {
				return null;
			}
			throw e;
		}
	}

	/**
	 * Gets the index of a card in the CardList
	 * 
	 * @param card
	 *            Card to be searched
	 * @return Index of the Card in the CardList
	 */
	public int getIndexOf(final Card card) {

		int index = -1;

		if (cards.contains(card)) {

			int currIndex = 0;

			while (index == -1 && currIndex < cards.size()) {

				if (get(currIndex).getSuit() == card.getSuit() && get(currIndex).getRank() == card.getRank()) {

					index = currIndex;
				}
				currIndex++;
			}
		}

		return index;
	}

	/**
	 * Gets the suit with the most Cards in the CardList (without considering
	 * the jacks!)
	 * 
	 * @return Suit with most Cards in the CardList,<br>
	 *         the highest ranking suit, if there the highest count gives more
	 *         than one suit
	 */
	public Suit getMostFrequentSuit() {
		return getMostFrequentSuit(null);
	}

	/**
	 * Gets the suit with the most Cards in the CardList (without considering
	 * the jacks!), without considering the given suit
	 * 
	 * @param exclude
	 *            suit to exclude from calculating the most frequent suit
	 *            (normally the trump suit)
	 * @return Suit with most Cards in the CardList,<br>
	 *         the highest ranking suit, if there the highest count gives more
	 *         than one suit
	 */
	public Suit getMostFrequentSuit(final Suit exclude) {

		int maxCount = 0;
		Suit mostFrequentSuitColor = null;
		for (Suit suit : Suit.values()) {
			if (exclude != null && suit == exclude) {
				continue;
			}
			int cardCount = getSuitCount(suit, false);
			if (cardCount > maxCount) {
				mostFrequentSuitColor = suit;
				maxCount = cardCount;
			}
		}

		return mostFrequentSuitColor;
	}

	/**
	 * Returns the number of cards with a given suit dependent on a game type
	 * 
	 * @param suit
	 *            The suit to search for
	 * @param countJack
	 *            TRUE if all of the jack should count to the number of suit
	 *            cards (max=8), FALSE otherwise (max=7)
	 * @return Number of cards with this suit
	 */
	public int getSuitCount(final Suit suit, final boolean countJack) {

		int count = 0;

		for (Card card : cards) {
			if (card.getSuit() == suit && (card.getRank() != Rank.JACK || countJack)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Returns the number of potential trump cards for a given suit
	 * 
	 * @param trumpSuit
	 *            The potential trump suit to search for
	 * @return Number of trump cards for this suit
	 */
	public int getTrumpCount(final Suit trumpSuit) {

		int count = 0;

		for (Card card : cards) {
			if (card.getRank() == Rank.JACK || card.getSuit() == trumpSuit) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Changes two cards in the CardList Helper function for sorting
	 */
	private void changeCards(final int cardOne, final int cardTwo) {
		Collections.swap(cards, cardOne, cardTwo);
	}

	/**
	 * Sorts the Cards in the CardList according the sort type SkatConstants
	 * 
	 * @param gameType
	 *            Game type for sorting
	 */
	public void sort(final GameType gameType) {

		if (gameType == null) {
			sortGrandGame();
			return;
		}
		if (!containsHiddenCards()) {
			switch (gameType) {
			case NULL:
				sortNullGame();
				break;
			case GRAND:
				sortGrandGame();
				break;
			case RAMSCH:
				sortRamschGame();
				break;
			case CLUBS:
			case SPADES:
			case HEARTS:
			case DIAMONDS:
				sortSuitGame(gameType);
				break;
			default:
				sortGrandGame();
				break;
			}
		}
	}

	private boolean containsHiddenCards() {

		boolean result = false;

		for (Card card : cards) {
			if (null == card) {
				result = true;
			}
		}

		return result;
	}

	private void sortNullGame() {

		for (int i = 0; i < cards.size() - 1; i++) {
			for (int j = i + 1; j < cards.size(); j++) {

				if (get(j).getSuit().getSuitOrder() > get(i).getSuit().getSuitOrder()
						|| (get(j).getSuit() == get(i).getSuit() && get(j).getNullOrder() >= get(i).getNullOrder())) {

					log.debug("i=" + i + ", j=" + j + ", " + get(i) //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
							+ " vs. " + get(j) + ", cards(1): [" + this //$NON-NLS-1$ //$NON-NLS-2$
							+ "]"); //$NON-NLS-1$

					changeCards(i, j);
				}
			}
		}
	}

	private int sortJacks() {

		int sortedCards = 0;

		// First find the Jacks
		if (cards.contains(Card.CJ)) {

			changeCards(sortedCards, getIndexOf(Card.CJ));
			sortedCards++;
		}
		if (cards.contains(Card.SJ)) {

			changeCards(sortedCards, getIndexOf(Card.SJ));
			sortedCards++;
		}
		if (cards.contains(Card.HJ)) {

			changeCards(sortedCards, getIndexOf(Card.HJ));
			sortedCards++;
		}
		if (cards.contains(Card.DJ)) {

			changeCards(sortedCards, getIndexOf(Card.DJ));
			sortedCards++;
		}

		return sortedCards;
	}

	private void sortRamschGame() {

		// first sort jacks
		int sortedCards = sortJacks();

		// then cycle through the colors for the remaining colors
		for (int i = sortedCards; i < cards.size() - 1; i++) {
			for (int j = i + 1; j < cards.size(); j++) {

				if (get(j).getSuit().getSuitOrder() > get(i).getSuit().getSuitOrder()
						|| (get(j).getSuit() == get(i).getSuit() && get(j).getRamschOrder() >= get(i).getRamschOrder())) {

					//					log.debug("i=" + i + ", j=" + j + ", " + get(i) //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					//							+ " vs. " + get(j) + ", cards(1): [" + this //$NON-NLS-1$ //$NON-NLS-2$
					//							+ "]"); //$NON-NLS-1$

					changeCards(i, j);
				}
			}
		}
	}

	private void sortSuitGame(final GameType gameType) {

		log.debug("sort " + this + " according " + gameType); //$NON-NLS-1$ //$NON-NLS-2$
		// first sort jacks
		int sortedCards = sortJacks();

		Suit trumpSuit = gameType.getTrumpSuit();

		// then cycle through the colors for the remaining colors
		for (int i = sortedCards; i < cards.size() - 1; i++) {
			for (int j = i + 1; j < cards.size(); j++) {

				if (// prefer trump cards
				(get(j).getSuit() == trumpSuit && get(i).getSuit() != trumpSuit || get(j).getSuit() == trumpSuit
						&& get(i).getSuit() == trumpSuit && get(j).getSuitGrandOrder() >= get(i).getSuitGrandOrder())
						||
						// normal sorting, different suits
						(get(j).getSuit() != trumpSuit && get(i).getSuit() != trumpSuit && get(j).getSuit()
								.getSuitOrder() > get(i).getSuit().getSuitOrder())
						||
						// normal sorting, same suits
						(get(j).getSuit() == get(i).getSuit() && get(j).getSuitGrandOrder() >= get(i)
								.getSuitGrandOrder())) {

					//					log.debug("i=" + i + ", j=" + j + ", " + get(i) //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					//							+ " vs. " + get(j) + ", cards(1): [" + this //$NON-NLS-1$ //$NON-NLS-2$
					//							+ "]"); //$NON-NLS-1$

					changeCards(i, j);
				}
			}
		}

		log.debug("result: " + this); //$NON-NLS-1$
	}

	private void sortGrandGame() {

		// same order as club order
		this.sortSuitGame(GameType.CLUBS);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer output = new StringBuffer();

		output.append("{"); //$NON-NLS-1$

		Iterator<Card> iter = cards.iterator();
		while (iter.hasNext()) {

			output.append(iter.next());

			if (iter.hasNext()) {
				output.append(' ');
			}
		}
		output.append("}"); //$NON-NLS-1$

		return output.toString();
	}

	/**
	 * Returns the first index of a card with the given suit.
	 * 
	 * @param suit
	 *            Suit to search
	 * @param includeJacks
	 *            flag whether to include jacks in the result
	 * @return First index of a card with the given suit, -1 if there is no such
	 *         card
	 */
	public int getFirstIndexOfSuit(final Suit suit, final boolean includeJacks) {
		int result = -1;
		int index = 0;
		for (Card card : cards) {
			if (result == -1 && card.getSuit() == suit) {
				if (card.getRank() != Rank.JACK || (card.getRank() == Rank.JACK && includeJacks)) {
					result = index;
				}
			}
			index++;
		}
		return result;
	}

	/**
	 * Returns the first index of a card with the given suit (including jacks!)
	 * 
	 * @param suit
	 *            Suit to search
	 * @return First index of a card with the given suit, -1 if there is no such
	 *         card
	 */
	public int getFirstIndexOfSuit(final Suit suit) {

		return getFirstIndexOfSuit(suit, true);
	}

	/**
	 * Returns the last index of a card with the given suit (including jacks!)
	 * 
	 * @param suit
	 *            Suit to search
	 * @return Last index of a card with the given suit, -1 if there is no such
	 *         card
	 */
	public int getLastIndexOfSuit(final Suit suit) {

		return getLastIndexOfSuit(suit, true);
	}

	/**
	 * Returns the last index of a card with the given suit
	 * 
	 * @param suit
	 *            Suit to search
	 * @param includeJacks
	 *            flag whether to include jacks in the result
	 * @return Last index of a card with the given suit, -1 if there is no such
	 *         card
	 */
	public int getLastIndexOfSuit(final Suit suit, final boolean includeJacks) {

		int result = -1;
		int index = 0;
		for (Card card : cards) {
			if (card.getSuit() == suit) {
				if (card.getRank() != Rank.JACK || (card.getRank() == Rank.JACK && includeJacks)) {
					result = index;
				}
			}
			index++;
		}
		return result;
	}

	/**
	 * Counts the total points of this CardList
	 * 
	 * @return the points of the CardList
	 */
	public int getTotalValue() {
		int result = 0;
		for (Card c : cards) {
			result += c.getPoints();
		}

		return result;
	}

	/**
	 * converts the CardList to an int[4] array for a bitwise representation of
	 * the CardList, with one int value per suit. The index of the array equals
	 * the Suit ordinal (0=Clubs, 3=Diamonds).<br>
	 * &nbsp;<br>
	 * Using this representation, bitwise operations can be performed on the
	 * CardList, e.g. by AI players.
	 * 
	 * @return an int[4] array
	 */
	public int[] toBinary() {
		int[] result = new int[4];
		for (Card c : cards) {
			result[c.getSuit().ordinal()] += c.toBinaryFlag();
		}
		return result;
	}

	/**
	 * Provides a String view on the binary representation of the CardList for
	 * logging purposes
	 * 
	 * @return a loggable String (containing CR/LF chars!)
	 */
	public String dumpFlag() {
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		for (int i : toBinary()) {
			for (int j = 0; j < 8; j++) {
				sb.append((i & (int) Math.pow(2, j)) > 0 ? '1' : '0');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
