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

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;

/**
 * @author Markus J. Luzius (markus@luzius.de)
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
	 *            Index of card in trick
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
