/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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

import org.jskat.util.GameType;
import org.jskat.util.Suit;

/**
 * @author Markus J. Luzius <markus@luzius.de>
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
	 * @see jskat.share.SkatConstants
	 */
	private GameType gameType;
	/**
	 * trump suit in the current game
	 * 
	 * @see jskat.share.SkatConstants
	 */
	private Suit trump;
	/** id of the single player */
	private int singlePlayer;
}
