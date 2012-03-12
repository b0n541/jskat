/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
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
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * Holds Cards on a hand or in the Skat
 */
public class CardList extends ArrayList<Card> {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(CardList.class);

	/**
	 * Constructor
	 */
	public CardList() {

		super();
	}

	/**
	 * Additional Constructor - only valid for test Classes
	 */
	public CardList(Card[] cards) {
		super();
		for(Card c: cards) {
			add(c);
		}
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
	public boolean hasSuit(GameType gameType, Suit suit) {

		return SkatRuleFactory.getSkatRules(gameType).hasSuit(gameType, this, suit);
	}

	/**
	 * Tests whether a trump card is in the CardList or not
	 * 
	 * @param gameType
	 *            Game type of the game played
	 * @return TRUE, when a trump card was found in the CardList
	 */
	public boolean hasTrump(GameType gameType) {

		boolean result = false;

		int i = 0;
		while (result == false && i < this.size()) {

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
	public boolean hasJack(Suit suit) {

		boolean result = false;

		for (Card card : this) {

			if (card.getRank() == Rank.JACK) {

				if (card.getSuit() == suit) {

					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Overrides the stanjdard get method to handle the "not available" index value of -1, 
	 * so that in this case the method returns null
	 */
	/* (non-Javadoc)
	 * @see java.util.ArrayList#get(int)
	 */
	@Override 
	public Card get(int index) {
		try {
			return super.get(index);
		} catch (RuntimeException e) {
			if(index==-1) return null;
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
	public int getIndexOf(Card card) {

		int index = -1;

		if (contains(card)) {

			int currIndex = 0;

			while (index == -1 && currIndex < this.size()) {

				if (get(currIndex).getSuit() == card.getSuit() && get(currIndex).getRank() == card.getRank()) {

					index = currIndex;
				}
				currIndex++;
			}
		}

		return index;
	}

	/**
	 * Gets the suit with the most Cards in the CardList (without considering the jacks!)
	 * 
	 * @return Suit with most Cards in the CardList,<br>
	 * the highest ranking suit, if there the highest count gives more than one suit
	 */
	public Suit getMostFrequentSuit() {
		return getMostFrequentSuit(null);
	}

	/**
	 * Gets the suit with the most Cards in the CardList (without considering the jacks!), 
	 * without considering the given suit
	 * 
	 * @param exclude suit to exclude from calculating the most frequent suit (normally the trump suit)
	 * @return Suit with most Cards in the CardList,<br>
	 * the highest ranking suit, if there the highest count gives more than one suit
	 */
	public Suit getMostFrequentSuit(Suit exclude) {

		int maxCount = 0;
		Suit mostFrequentSuitColor = null;
		for(Suit suit: Suit.values()) {
			if(exclude!=null && suit==exclude) continue;
			int cardCount = getSuitCount(suit, false);
			if(cardCount>maxCount) {
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
	 *            TRUE if all of the jack should count to the number of suit cards (max=8), FALSE otherwise (max=7)
	 * @return Number of cards with this suit
	 */
	public int getSuitCount(Suit suit, boolean countJack) {

		int count = 0;

		for (Card card : this) {
			if (card.getSuit() == suit && (card.getRank()!=Rank.JACK || countJack)) {
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
	public int getTrumpCount(Suit trumpSuit) {

		int count = 0;

		for (Card card : this) {
			if (card.getRank() == Rank.JACK || card.getSuit() == trumpSuit) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Changes two cards in the CardList Helper function for sorting
	 */
	private void changeCards(int cardOne, int cardTwo) {

		Card helper = get(cardOne);
		this.set(cardOne, get(cardTwo));
		this.set(cardTwo, helper);

		helper = null;
	}

	/**
	 * Sorts the Cards in the CardList according the sort type SkatConstants
	 * 
	 * @param gameType
	 *            Game type for sorting
	 */
	public void sort(GameType gameType) {

		if (gameType==null) {
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

		for (Card card : this) {
			if (null == card) {
				result = true;
			}
		}

		return result;
	}

	private void sortNullGame() {

		for (int i = 0; i < this.size() - 1; i++) {
			for (int j = i + 1; j < this.size(); j++) {

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
		if (contains(Card.CJ)) {

			changeCards(sortedCards, getIndexOf(Card.CJ));
			sortedCards++;
		}
		if (contains(Card.SJ)) {

			changeCards(sortedCards, getIndexOf(Card.SJ));
			sortedCards++;
		}
		if (contains(Card.HJ)) {

			changeCards(sortedCards, getIndexOf(Card.HJ));
			sortedCards++;
		}
		if (contains(Card.DJ)) {

			changeCards(sortedCards, getIndexOf(Card.DJ));
			sortedCards++;
		}

		return sortedCards;
	}

	private void sortRamschGame() {

		// first sort jacks
		int sortedCards = sortJacks();

		// then cycle through the colors for the remaining colors
		for (int i = sortedCards; i < this.size() - 1; i++) {
			for (int j = i + 1; j < this.size(); j++) {

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

	private void sortSuitGame(GameType gameType) {

		log.debug("sort " + this + " according " + gameType); //$NON-NLS-1$ //$NON-NLS-2$
		// first sort jacks
		int sortedCards = sortJacks();

		Suit trumpSuit = gameType.getTrumpSuit();

		// then cycle through the colors for the remaining colors
		for (int i = sortedCards; i < this.size() - 1; i++) {
			for (int j = i + 1; j < this.size(); j++) {

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

		Iterator<Card> iter = this.iterator();
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
	public int getFirstIndexOfSuit(Suit suit, boolean includeJacks) {
		int result = -1;
		int index = 0;
		for (Card card : this) {
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
	public int getFirstIndexOfSuit(Suit suit) {

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
	public int getLastIndexOfSuit(Suit suit) {

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
	public int getLastIndexOfSuit(Suit suit, boolean includeJacks) {

		int result = -1;
		int index = 0;
		for (Card card : this) {
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
		for (Card c : this)
			result += c.getPoints();

		return result;
	}
	
	/** 
	 * converts the CardList to an int[4] array for a bitwise representation of the CardList, 
	 * with one int value per suit. The index of the array equals the Suit ordinal  (0=Clubs, 3=Diamonds).<br>&nbsp;<br>
	 * Using this representation, bitwise operations can be performed on the CardList, e.g. by AI players.
	 * 
	 * @return an int[4] array 
	 */
	public int[] toBinary() {
		int[] result = new int[4];
		for(Card c: this) {
			result[c.getSuit().ordinal()] += c.toBinaryFlag();
		}
		return result;
	}
	
	/**
	 * Provides a String view on the binary representation of the CardList for logging purposes
	 * 
	 * @return a loggable String (containing CR/LF chars!)
	 */
	public String dumpFlag() {
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		for(int i: toBinary()) {
			for(int j=0;j<8;j++) {
				sb.append((i&(int)Math.pow(2, j))>0?'1':'0');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
