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

import org.jskat.util.GameType;
import org.jskat.util.Suit;

/**
 * @author Markus J. Luzius (markus@luzius.de)
 * 
 */
public class GameInfo {

	/**
	 * default constructor
	 */
	public GameInfo() {
	}

	/**
	 * Constructor for setting up the game info with initial values
	 * 
	 * @param type
	 *            game type
	 * @param suit
	 *            trump color
	 * @param player
	 *            id of the single player
	 */
	public GameInfo(GameType type, Suit suit, int player) {
		gameType = type;
		trump = suit;
		singlePlayer = player;
	}

	/**
	 * get the current game type
	 * 
	 * @return current game type
	 */
	public GameType getGameType() {
		return gameType;
	}

	/**
	 * get the current single player
	 * 
	 * @return ID of the current single player
	 */
	public int getSinglePlayer() {
		return singlePlayer;
	}

	/**
	 * get the current trump suit
	 * 
	 * @return current trump suit
	 */
	public Suit getTrump() {
		return trump;
	}

	/**
	 * set the current game type
	 * 
	 * @param gameType
	 *            game type
	 */
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	/**
	 * set the current single player
	 * 
	 * @param id
	 *            ID of the single player
	 */
	public void setSinglePlayer(int id) {
		singlePlayer = id;
	}

	/**
	 * set the trump suit
	 * 
	 * @param suit
	 *            trump suit
	 */
	public void setTrump(Suit suit) {
		trump = suit;
	}

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
	/** id of the single player */
	private int singlePlayer;
}
