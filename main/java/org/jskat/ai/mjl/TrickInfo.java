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
package org.jskat.ai.mjl;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
public class TrickInfo {

	/**
	 * Constructor
	 */
	public TrickInfo() {
	}

	/**
	 * get the current gameInfo
	 * 
	 * @return gameInfo
	 */
	public GameInfo getGameInfo() {
		return gameInfo;
	}

	/**
	 * get the current trick
	 * 
	 * @return current trick
	 */
	public CardList getTrick() {
		return trick;
	}

	/**
	 * set the current gameInfo
	 * 
	 * @param info
	 *            current game info to be set
	 */
	public void setGameInfo(GameInfo info) {
		gameInfo = info;
	}

	/**
	 * set the current trick
	 * 
	 * @param trick
	 *            current trick to be set
	 */
	public void setTrick(CardList trick) {
		this.trick = trick;
	}

	/**
	 * get the forehand player for the current trick
	 * 
	 * @return ID of the forehand player
	 */
	public int getForehandPlayer() {
		return forehandPlayer;
	}

	/**
	 * set the forehand player for the current trick
	 * 
	 * @param id
	 *            forehand player in the current trick
	 */
	public void setForehandPlayer(int id) {
		forehandPlayer = id;
	}

	/**
	 * get the position of the single player within the trick
	 * 
	 * @return position in the current trick (0-2)
	 */
	public int getSinglePlayerPos() {
		return singlePlayerPos;
	}

	/**
	 * set the position of the single player within the trick
	 * 
	 * @param pos
	 *            position of the single player
	 */
	public void setSinglePlayerPos(int pos) {
		singlePlayerPos = pos;
	}

	/**
	 * Get a certain card of the trick
	 * 
	 * @param index
	 * @return card, null if index not available
	 */
	public Card getCard(int index) {
		if (trick.size() < index + 1)
			return null;
		return trick.get(index);
	}

	/**
	 * Convenience method for getting the size of the trick
	 * 
	 * @return size
	 */
	public int size() {
		return trick.size();
	}

	/**
	 * Convenience method for getting the suit color of the initial card
	 * 
	 * @return suit value
	 */
	public Suit getDemandSuit() {
		if (trick.size() < 1)
			return null;
		return trick.get(0).getSuit();
	}

	/**
	 * Calculates the value of all the cards in the trick
	 * 
	 * @return sum of the values
	 */
	public int getTrickValue() {
		int result = 0;
		int count = 0;
		while (++count < trick.size()) {
			result += trick.get(count - 1).getPoints();
		}

		return result;
	}

	/**
	 * Convenience method for getting the current game type
	 * 
	 * @return game type
	 */
	public GameType getGameType() {
		return gameInfo.getGameType();
	}

	/**
	 * Concenience method for getting the current trump color
	 * 
	 * @return trump color
	 */
	public Suit getTrump() {
		return gameInfo.getTrump();
	}

	/** cards played in the current trick */
	private CardList trick;
	/** id of the forehand player in this trick */
	private int forehandPlayer;
	/** position of the single player in this trick */
	private int singlePlayerPos;
	/** game info for the current trick */
	private GameInfo gameInfo;
}
