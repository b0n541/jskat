/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-20
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
/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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
package org.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;

/**
 * CardMemory builds the memory of all the cards that have been played during a
 * certain game. It remembers all the initial cards as well as the individual
 * hands of all players.
 * 
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
public class CardMemory {

	private Log log = LogFactory.getLog(CardMemory.class);

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
	 * @param trump
	 */
	public CardMemory(GameType gameType, Suit trump) {
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
	public void addTrick(CardList trick, int forehandPlayer) {
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
	public int timesPlayedByPlayer(int player, int suit) {
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
	public int timesDemanded(int suit) {
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
	public int remainingCards(int suit, CardList myCards) {
		int count = 0;
		return count;
	}

	/** number of tricks that have been played so far */
	private int tricksPlayed;
	/**
	 * type of the game
	 * 
	 * @see jskat.share.SkatConstants
	 */
	private GameType gameType;
	/**
	 * trump suit in the current game
	 * 
	 * @see jskat.share.SkatConstants
	 */
	private Suit trump;
	/** a vector of all the cards that have been played in this game so far */
	private CardList allCardsPlayed;
	/**
	 * reflects all the cards that have been played by each player in this game
	 * so far
	 */
	private CardList[] cardsPlayed;
	/**
	 * reflects all the initial cards that have been played (i.e. what has been
	 * demanded already?)
	 */
	private CardList initialCardsPlayed;
}
