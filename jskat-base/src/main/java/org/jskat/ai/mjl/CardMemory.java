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

import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CardMemory builds the memory of all the cards that have been played during a
 * certain game. It remembers all the initial cards as well as the individual
 * hands of all players.
 * 
 * @author Markus J. Luzius (markus@luzius.de)
 * 
 */
public class CardMemory {

	private static Logger log = LoggerFactory.getLogger(CardMemory.class);

	/**
	 * Default constructor
	 */
	public CardMemory() {
		cardsPlayed = new CardList[3];
		cardsPlayed[0] = new CardList();
		cardsPlayed[1] = new CardList();
		cardsPlayed[2] = new CardList();
		initialCardsPlayed = new CardList();
		allCardsPlayed = new CardList();
		tricksPlayed = 0;
	}

	/**
	 * Extended constructor with the values of the current game
	 * 
	 * @param gameType
	 *            Game type
	 * @param trump
	 *            Trump suit
	 */
	public CardMemory(final GameType gameType, final Suit trump) {
		this.gameType = gameType;
		this.trump = trump;
		cardsPlayed = new CardList[3];
		cardsPlayed[0] = new CardList();
		cardsPlayed[1] = new CardList();
		cardsPlayed[2] = new CardList();
		initialCardsPlayed = new CardList();
		allCardsPlayed = new CardList();
		tricksPlayed = 0;
	}

	/**
	 * Adds the cards of the current trick to the memory
	 * 
	 * @param trick
	 *            current trick
	 * @param forehandPlayer
	 *            player of the first card in the trick
	 */
	public void addTrick(final CardList trick, final int forehandPlayer) {
		tricksPlayed++;
		initialCardsPlayed.add(trick.get(0));
		allCardsPlayed.add(trick.get(0));
		allCardsPlayed.add(trick.get(1));
		allCardsPlayed.add(trick.get(2));
		cardsPlayed[forehandPlayer].add(trick.get(0));
		cardsPlayed[(forehandPlayer + 1) % 3].add(trick.get(1));
		cardsPlayed[(forehandPlayer + 2) % 3].add(trick.get(2));
		log.debug("Cards played so far:");
		log.debug("Player 0: " + cardsPlayed[0]);
		log.debug("Player 1: " + cardsPlayed[1]);
		log.debug("Player 2: " + cardsPlayed[2]);
		log.debug("Initial cards: " + initialCardsPlayed);
	}

	/**
	 * Counts, how many cards of a given suit a player has already played.<br>
	 * In a suit game, for a normal suit the maximum can be 7, of trump it can
	 * be 11. In a null game, the maximum is 8, in a grand game 7 for all suits.
	 * 
	 * @param player
	 *            Player who is evaluated
	 * @param suit
	 *            Suit which is evaluated
	 * @return Number of times the given player has played a card of that suit
	 */
	public int timesPlayedByPlayer(final int player, final int suit) {
		int count = 0;
		return count;
	}

	/**
	 * Counts, how many times the given suit has been demanded as initial card.
	 * 
	 * @param suit
	 *            Suit which is evaluated
	 * @return Number of times the suit has been demanded
	 */
	public int timesDemanded(final int suit) {
		int count = 0;
		return count;
	}

	/**
	 * Calculates how many cards of that suit are still out.
	 * 
	 * @param suit
	 *            Suit which is evaluated
	 * @param myCards
	 *            Hand of the player
	 * @return Number of cards still out for that suit
	 */
	public int remainingCards(final int suit, final CardList myCards) {
		int count = 0;
		return count;
	}

	/** number of tricks that have been played so far */
	private int tricksPlayed;
	/**
	 * type of the game
	 * 
	 * @see org.jskat.util.GameType
	 */
	private GameType gameType;
	/**
	 * trump suit in the current game
	 * 
	 * @see org.jskat.util.Suit
	 */
	private Suit trump;
	/** a vector of all the cards that have been played in this game so far */
	private final CardList allCardsPlayed;
	/**
	 * reflects all the cards that have been played by each player in this game
	 * so far
	 */
	private final CardList[] cardsPlayed;
	/**
	 * reflects all the initial cards that have been played (i.e. what has been
	 * demanded already?)
	 */
	private final CardList initialCardsPlayed;
}
