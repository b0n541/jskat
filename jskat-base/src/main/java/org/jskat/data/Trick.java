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
package org.jskat.data;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Holds data for a single trick
 */
public class Trick {

	private int trickNumberInGame;

	private final Player foreHand;
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
	 * Checks whether the trick is finished or not.
	 *
	 * @return TRUE, if the trick is finished
	 */
	public boolean isTrickFinished() {
		return firstCard != null && secondCard != null && thirdCard != null
				&& trickWinner != null;
	}

	/**
	 * Gets the fore hand player of this trick
	 *
	 * @return Fore hand player
	 */
	public Player getForeHand() {

		return foreHand;
	}

	/**
	 * Gets the middle hand player of this trick
	 *
	 * @return Fore hand player
	 */
	public Player getMiddleHand() {
		return foreHand.getLeftNeighbor();
	}

	/**
	 * Gets the rear hand player of this trick
	 *
	 * @return Fore hand player
	 */
	public Player getRearHand() {
		return foreHand.getRightNeighbor();
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
	 * @param card
	 *            Card to be added
	 */
	public void addCard(Card card) {

		if (firstCard == null) {
			firstCard = card;
		} else if (secondCard == null) {
			secondCard = card;
		} else if (thirdCard == null) {
			thirdCard = card;
		}
	}

	/**
	 * Removes a card from the trick.
	 *
	 * @param card
	 *            Card to be removed
	 */
	public void removeCard(Card card) {
		if (card.equals(thirdCard)) {
			thirdCard = null;
			trickWinner = null;
		} else if (card.equals(secondCard)) {
			secondCard = null;
		} else if (card.equals(firstCard)) {
			firstCard = null;
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
	public int getValue() {

		return getCardList().getTotalValue();
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
	public Object clone() {

		Trick clone = new Trick(trickNumberInGame, foreHand);

		clone.addCard(firstCard);
		clone.addCard(secondCard);
		clone.addCard(thirdCard);

		clone.setTrickWinner(trickWinner);

		return clone;
	}

	@Override
	public String toString() {
		return "Trick " + (trickNumberInGame + 1) + ": " + firstCard + " "
				+ secondCard + " " + thirdCard;
	}
}
