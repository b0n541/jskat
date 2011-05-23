/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0
 * Build date: 2011-05-23 18:13:47
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


package org.jskat.data;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Holds data for a single trick
 */
public class Trick {

	private int trickNumberInGame;

	private Player foreHand;
	private Player trickWinner;

	private Card firstCard;
	private Card secondCard;
	private Card thirdCard;

	/**
	 * Creates a new instance of Trick
	 * 
	 * @param newTrickNumber
	 *            Trick number in game
	 * 
	 * @param newForeHand
	 *            Player ID of the fore hand player
	 */
	public Trick(int newTrickNumber, Player newForeHand) {

		trickNumberInGame = newTrickNumber;
		foreHand = newForeHand;
	}

	/**
	 * Gets the fore hand player
	 * 
	 * @return Fore hand player
	 */
	public Player getForeHand() {

		return foreHand;
	}

	/**
	 * Gets the first card
	 * 
	 * @return First card
	 */
	public Card getFirstCard() {

		return firstCard;
	}

	/**
	 * Sets the first card
	 * 
	 * @param newFirstCard
	 *            First card
	 */
	public void setFirstCard(Card newFirstCard) {

		firstCard = newFirstCard;
	}

	/**
	 * Gets the second card
	 * 
	 * @return Second card
	 */
	public Card getSecondCard() {

		return secondCard;
	}

	/**
	 * Sets the second card
	 * 
	 * @param newSecondCard
	 *            Second card
	 */
	public void setSecondCard(Card newSecondCard) {

		secondCard = newSecondCard;
	}

	/**
	 * Gets the third card
	 * 
	 * @return Third card
	 */
	public Card getThirdCard() {

		return thirdCard;
	}

	/**
	 * Sets the third card
	 * 
	 * @param newThirdCard
	 *            Third card
	 */
	public void setThirdCard(Card newThirdCard) {

		thirdCard = newThirdCard;
	}

	/**
	 * Gets a card from the trick
	 * 
	 * @param player
	 *            Player
	 * @return Card played by the player
	 */
	public Card getCard(Player player) {

		Card returnCard = null;

		switch (player) {

		case FOREHAND:
			returnCard = getFirstCard();
			break;
		case MIDDLEHAND:
			returnCard = getSecondCard();
			break;
		case REARHAND:
			returnCard = getThirdCard();
			break;
		}

		return returnCard;
	}

	/**
	 * Gets the trick winner
	 * 
	 * @return Trick winner
	 */
	public Player getTrickWinner() {

		return trickWinner;
	}

	/**
	 * Sets the trick winner
	 * 
	 * @param newTrickWinner
	 *            Trick winner
	 */
	public void setTrickWinner(Player newTrickWinner) {

		trickWinner = newTrickWinner;
	}

	/**
	 * Adds a card to the trick
	 * 
	 * @param newCard
	 *            Card to be added
	 */
	public void addCard(Card newCard) {

		if (firstCard == null) {

			firstCard = newCard;
		} else if (secondCard == null) {

			secondCard = newCard;
		} else if (thirdCard == null) {

			thirdCard = newCard;
		}
	}

	/**
	 * Returns the cards of the trick as CardList
	 * 
	 * @return The cards of the trick
	 */
	public CardList getCardList() {

		CardList returnList = new CardList();

		if (firstCard != null) {

			returnList.add(firstCard);
		}
		if (secondCard != null) {

			returnList.add(secondCard);
		}
		if (thirdCard != null) {

			returnList.add(thirdCard);
		}

		return returnList;
	}

	/**
	 * Gets the sum of all card points in the CardList
	 * 
	 * @return Sum of all card points
	 */
	public int getCardValueSum() {

		return getCardList().getCardValueSum();
	}

	/**
	 * Gets the trick number in the game
	 * 
	 * @return Trick number in the game
	 */
	public int getTrickNumberInGame() {
		return trickNumberInGame;
	}

	/**
	 * Sets the trick number in the game
	 * 
	 * @param newTrickNumber
	 *            Trick number in the game
	 */
	public void setTrickNumberInGame(int newTrickNumber) {
		trickNumberInGame = newTrickNumber;
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		Trick clone = new Trick(trickNumberInGame, foreHand);

		clone.addCard(firstCard);
		clone.addCard(secondCard);
		clone.addCard(thirdCard);

		clone.setTrickWinner(trickWinner);

		return clone;
	}
}
