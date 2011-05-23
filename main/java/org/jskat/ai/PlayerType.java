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

/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package org.jskat.ai;

/**
 * All implemented player types
 */
public enum PlayerType {

	/**
	 * Random player
	 */
	RANDOM,
	/**
	 * Algorithmic player
	 */
	ALGORITHMIC,
	/**
	 * Neural network player
	 */
	NEURAL_NETWORK,
	/**
	 * Human player
	 */
	HUMAN;
	
	/**
	 * Gets a player type from a string
	 * 
	 * @param playerString Player type as string
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
