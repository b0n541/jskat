/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

	public CardList(Card... cards) {
		this(Arrays.asList(cards));
	}

	/**
	 * Gets a copy of a card list that is immutable
	 * 
	 * @return Immutable copy of a card list
	 */
	public CardList getImmutableCopy() {
		return new CardList(Collections.unmodifiableList(cards));
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
	 * Removes a card.
	 * 
	 * @param index
	 *            Index of card
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

		return SkatRuleFactory.getSkatRules(gameType).hasSuit(gameType, this,
				suit);
	}

	/**
	 * Tests whether a trump card is in the CardList or not
	 * 
	 * @param gameType
	 *            Game type of the game played
	 * @return TRUE, when a trump card was found in the CardList
	 */
	public boolean hasTrump(final GameType gameType) {
		for (Card card : cards) {
			if (card.isTrump(gameType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a jack of a given suit is in the CardList
	 * 
	 * @param suit
	 *            Suit to check
	 * @return TRUE if the jack of the tested suit is in the CardList
	 */
	public boolean hasJack(final Suit suit) {
		return cards.contains(Card.getCard(suit, Rank.JACK));
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

				if (get(currIndex).getSuit() == card.getSuit()
						&& get(currIndex).getRank() == card.getRank()) {

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
			if (card.getSuit() == suit
					&& (card.getRank() != Rank.JACK || countJack)) {
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
	 * Sorts the Cards in the CardList according the sort type SkatConstants
	 * 
	 * @param gameType
	 *            Game type for sorting
	 */
	public void sort(final GameType gameType) {

		if (gameType == null) {
			Collections.sort(cards, new SuitComparator(GameType.CLUBS));
			return;
		}
		if (!containsHiddenCards()) {
			switch (gameType) {
			case NULL:
				Collections.sort(cards, new NullComparator());
				break;
			case RAMSCH:
				Collections.sort(cards, new RamschComparator());
				break;
			case CLUBS:
			case SPADES:
			case HEARTS:
			case DIAMONDS:
				Collections.sort(cards, new SuitComparator(gameType));
				break;
			case GRAND:
			default:
				Collections.sort(cards, new SuitComparator(GameType.CLUBS));
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
				if (card.getRank() != Rank.JACK || card.getRank() == Rank.JACK
						&& includeJacks) {
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
				if (card.getRank() != Rank.JACK || card.getRank() == Rank.JACK
						&& includeJacks) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cards == null ? 0 : cards.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CardList other = (CardList) obj;
		if (cards == null) {
			if (other.cards != null) {
				return false;
			}
		} else if (!cards.equals(other.cards)) {
			return false;
		}
		return true;
	}

	private class NullComparator implements Comparator<Card> {
		@Override
		public int compare(Card first, Card second) {
			if (first.getSuit().getSuitOrder() < second.getSuit()
					.getSuitOrder()) {
				return -1;
			} else if (first.getSuit().getSuitOrder() > second.getSuit()
					.getSuitOrder()) {
				return 1;
			}

			if (first.getNullOrder() < second.getNullOrder()) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	private class RamschComparator implements Comparator<Card> {
		@Override
		public int compare(Card first, Card second) {
			// first the jacks
			if (first.getRank() == Rank.JACK && second.getRank() == Rank.JACK) {
				if (first.getSuit().getSuitOrder() < second.getSuit()
						.getSuitOrder()) {
					return -1;
				} else if (first.getSuit().getSuitOrder() > second.getSuit()
						.getSuitOrder()) {
					return 1;
				}
			} else if (first.getRank() == Rank.JACK) {
				return -1;
			} else if (second.getRank() == Rank.JACK) {
				return 1;
			}

			if (first.getSuit().getSuitOrder() < second.getSuit()
					.getSuitOrder()) {
				return -1;
			} else if (first.getSuit().getSuitOrder() > second.getSuit()
					.getSuitOrder()) {
				return 1;
			}

			if (first.getRamschOrder() < second.getRamschOrder()) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	private class SuitComparator implements Comparator<Card> {
		private final GameType gameType;

		public SuitComparator(GameType pGameType) {
			gameType = pGameType;
		}

		@Override
		public int compare(Card first, Card second) {
			// first the jacks
			if (first.getRank() == Rank.JACK && second.getRank() == Rank.JACK) {
				if (first.getSuit().getSuitOrder() < second.getSuit()
						.getSuitOrder()) {
					return 1;
				} else if (first.getSuit().getSuitOrder() > second.getSuit()
						.getSuitOrder()) {
					return -1;
				}
			} else if (first.getRank() == Rank.JACK) {
				return -1;
			} else if (second.getRank() == Rank.JACK) {
				return 1;
			}

			// trump cards follow
			if (first.getSuit() == gameType.getTrumpSuit()
					&& second.getSuit() != gameType.getTrumpSuit()) {
				return -1;
			} else if (first.getSuit() != gameType.getTrumpSuit()
					&& second.getSuit() == gameType.getTrumpSuit()) {
				return 1;
			} else if (first.getSuit() == gameType.getTrumpSuit()
					&& second.getSuit() == gameType.getTrumpSuit()) {
				if (first.getSuitGrandOrder() < second.getSuitGrandOrder()) {
					return 1;
				} else {
					return -1;
				}
			}

			// all the other cards
			if (first.getSuit().getSuitOrder() < second.getSuit()
					.getSuitOrder()) {
				return 1;
			} else if (first.getSuit().getSuitOrder() > second.getSuit()
					.getSuitOrder()) {
				return -1;
			}

			if (first.getSuitGrandOrder() < second.getSuitGrandOrder()) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
