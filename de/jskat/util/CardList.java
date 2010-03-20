/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.util.rule.SkatRuleFactory;

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
	 * Tests whether a card with a suit is in the CardList or not
	 * 
	 * @param gameType
	 *            Game type of the game played
	 * @param suit
	 *            Suit color
	 * @return TRUE, when a card with the suit is found
	 */
	public boolean hasSuit(GameType gameType, Suit suit) {

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
	 * Gets the suit with the most Cards in the CardList
	 * 
	 * @return Suit with most Cards in the CardList
	 */
	public Suit getMostFrequentSuit() {

		// TODO re-implement it

		Suit mostFrequentSuitColor = Suit.CLUBS;
		int highestCardCount = getSuitCount(Suit.CLUBS, true);
		int currentCardCount = 0;

		currentCardCount = getSuitCount(Suit.SPADES, true);

		if (currentCardCount > highestCardCount) {

			highestCardCount = currentCardCount;
			mostFrequentSuitColor = Suit.SPADES;
		}

		currentCardCount = getSuitCount(Suit.HEARTS, true);

		if (currentCardCount > highestCardCount) {

			highestCardCount = currentCardCount;
			mostFrequentSuitColor = Suit.HEARTS;
		}
		currentCardCount = getSuitCount(Suit.DIAMONDS, true);

		if (currentCardCount > highestCardCount) {

			highestCardCount = currentCardCount;
			mostFrequentSuitColor = Suit.DIAMONDS;
		}

		return mostFrequentSuitColor;
	}

	/**
	 * Returns the number of cards with a given suit dependent on a game type
	 * 
	 * @param suit
	 *            The suit to search for
	 * @param countJacks
	 *            TRUE if the jacks should count to the number of suit cards
	 * @return Number of cards with this suit
	 */
	public int getSuitCount(Suit suit, boolean countJacks) {

		int count = 0;

		for (Card card : this) {

			if (card.getSuit() == suit) {

				if (card.getRank() == Rank.JACK && countJacks) {
					// count jacks only if needed
					count++;
				} else {
					// count every other card with the same suit
					count++;
				}
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

				if (get(j).getSuit().getSuitOrder() > get(i).getSuit()
						.getSuitOrder()
						|| (get(j).getSuit() == get(i).getSuit() && get(j)
								.getNullOrder() >= get(i).getNullOrder())) {

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

				if (get(j).getSuit().getSuitOrder() > get(i).getSuit()
						.getSuitOrder()
						|| (get(j).getSuit() == get(i).getSuit() && get(j)
								.getRamschOrder() >= get(i).getRamschOrder())) {

					log.debug("i=" + i + ", j=" + j + ", " + get(i) //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
							+ " vs. " + get(j) + ", cards(1): [" + this //$NON-NLS-1$ //$NON-NLS-2$
							+ "]"); //$NON-NLS-1$

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
				(get(j).getSuit() == trumpSuit && get(i).getSuit() != trumpSuit || get(
						j).getSuit() == trumpSuit
						&& get(i).getSuit() == trumpSuit
						&& get(j).getSuitGrandOrder() >= get(i)
								.getSuitGrandOrder())
						||
						// normal sorting, different suits
						(get(j).getSuit() != trumpSuit
								&& get(i).getSuit() != trumpSuit && get(j)
								.getSuit().getSuitOrder() > get(i).getSuit()
								.getSuitOrder()) ||
						// normal sorting, same suits
						(get(j).getSuit() == get(i).getSuit() && get(j)
								.getSuitGrandOrder() >= get(i)
								.getSuitGrandOrder())) {

					log.debug("i=" + i + ", j=" + j + ", " + get(i) //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
							+ " vs. " + get(j) + ", cards(1): [" + this //$NON-NLS-1$ //$NON-NLS-2$
							+ "]"); //$NON-NLS-1$

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
	 * Gets the sum of all card points in the CardList
	 * 
	 * @return Sum of all card points
	 */
	public int getCardValueSum() {

		int sum = 0;

		for (Card card : this) {

			sum += card.getRank().getPoints();
		}

		return sum;
	}

	/**
	 * Returns the first index of a card with the given suit
	 * 
	 * @param suit
	 *            Suit to search
	 * @return First index of a card with the given suit, -1 if there is no such
	 *         card
	 */
	public int getFirstIndexOfSuit(Suit suit) {

		int result = -1;

		int index = 0;
		for (Card card : this) {

			if (result == -1 && card.getSuit() == suit) {

				result = index;
			}

			index++;
		}

		return result;
	}
}
