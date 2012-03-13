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
package org.jskat.util.rule;

import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Suit;

/**
 * Interface for all skat rules objects
 */
public interface BasicSkatRules {

	/**
	 * Checks, whether the given card is allowed to be played, also considering
	 * the rest of the hand
	 * 
	 * @param gameType
	 *            Game type
	 * @param initialCard
	 *            First card in the trick
	 * @param hand
	 *            All cards on the hand
	 * @param card
	 *            Card to be checked
	 * @return TRUE if the card is allowed to be played
	 */
	public boolean isCardAllowed(GameType gameType, Card initialCard, CardList hand, Card card);

	/**
	 * Checks whether a card beats another card
	 * 
	 * @param gameType
	 *            Game type
	 * @param cardToBeat
	 *            Card to be beaten
	 * @param card
	 *            Card to be checked
	 * @return TRUE if the card beats the other card
	 */
	public boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card);

	/**
	 * Checks whether a game is won
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was won
	 */
	public boolean calcGameWon(SkatGameData gameData);

	/**
	 * Computes the value for a game
	 * 
	 * @param gameData
	 *            Game data
	 * @return Game result
	 */
	public int calcGameResult(SkatGameData gameData);

	/**
	 * Checks whether one or more cards of a given suit are on the hand
	 * 
	 * @param gameType
	 *            Game type
	 * @param hand
	 *            Cards on the players hand
	 * @param suit
	 *            Suit color to search for
	 * @return TRUE if one or more cards are on the hand
	 */
	public boolean hasSuit(GameType gameType, CardList hand, Suit suit);

	/**
	 * Calculates the trick winner
	 * 
	 * @param gameType
	 *            Game type
	 * @param trick
	 *            Trick
	 * @return Trick winner
	 */
	public Player calculateTrickWinner(GameType gameType, Trick trick);

	/**
	 * calculates the multiplier of a card list with regard to the ruleset
	 * 
	 * @param gameData
	 *            Game data
	 * @return the multiplier for bidding and game announcement
	 */
	public int getMultiplier(SkatGameData gameData);

	/**
	 * Checks whether the game is played with jacks or without
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game is played with jacks
	 */
	public boolean isPlayWithJacks(SkatGameData gameData);
}
