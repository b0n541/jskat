/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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

package org.jskat.ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * All implemented player types
 */
public enum PlayerType {

	/**
	 * Algorithmic player
	 */
	 NEW_ALGORITHMIC("org.jskat.ai.algorithmic.AlgorithmicAIPlayer"),
	/**
	 * Random player
	 */
	RANDOM("org.jskat.ai.rnd.AIPlayerRND"),
	/**
	 * Algorithmic player
	 */
	ALGORITHMIC("org.jskat.ai.mjl.AIPlayerMJL"),
	/**
	 * Neural network player
	 */
	NEURAL_NETWORK("org.jskat.ai.nn.AIPlayerNN"),
	/**
	 * Human player
	 */
	HUMAN(null);

	private static Log log = LogFactory.getLog(PlayerType.class);

	private final String implementingClass;

	private PlayerType(String implementingClass) {
		// class is not checked here -
		// otherwise a missing class would already result in an exception here,
		// even if it is not required
		this.implementingClass = implementingClass;
	}

	/**
	 * Creates a new instance of an ai player type<br>
	 * Note: Creation of an ai player fails with an exception only at this point
	 * if this ai player has actually been selected.
	 * 
	 * @param type
	 *            a player type which must not be HUMAN
	 * @return an instance of the ai player's main class
	 * @throws IllegalArgumentException
	 *             if PlayerType==HUMAN
	 */
	public static IJSkatPlayer getPlayerInstance(PlayerType type) {
		if (type == HUMAN)
			throw new IllegalArgumentException(
					".getPlayerInstance(..) cannot be used for human players");
		IJSkatPlayer player = null;

		try {
			player = (IJSkatPlayer) Class.forName(type.implementingClass)
					.newInstance();
		} catch (Exception ex) {
			// handle exception case
			try {
				player = (IJSkatPlayer) Class.forName(RANDOM.implementingClass)
						.newInstance();
			} catch (Exception e) {
				log.warn("Cannot get JSkatPlayer: " + e.getClass() + ": "
						+ e.getMessage());
			}
		}

		return player;
	}

	/**
	 * Gets a player type from a string
	 * 
	 * @param playerString
	 *            Player type as string
	 * @return Player type or NULL if the player type does not exists
	 */
	public static PlayerType getPlayerTypeFromString(String playerString) {

		PlayerType result = null;

		for (PlayerType currPlayerType : PlayerType.values()) {

			if (currPlayerType.toString().equals(playerString)) {

				result = currPlayerType;
			}
		}

		return result;
	}
}
