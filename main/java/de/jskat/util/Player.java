/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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


package de.jskat.util;

/**
 * Contains all player positions in a trick
 */
public enum Player {
	/**
	 * First player
	 */
	FORE_HAND {
		@Override
		public int getOrder() { return 0; }
		@Override
		public Player getRightNeighbor() { return HIND_HAND; }
		@Override
		public Player getLeftNeighbor() { return MIDDLE_HAND; }
	},
	/**
	 * Second player
	 */
	MIDDLE_HAND { 
		@Override
		public int getOrder() { return 1; } 
		@Override
		public Player getRightNeighbor() { return FORE_HAND; }
		@Override
		public Player getLeftNeighbor() { return HIND_HAND; }
	},
	/**
	 * Third player
	 */
	HIND_HAND { 
		@Override
		public int getOrder() { return 2; } 
		@Override
		public Player getRightNeighbor() { return MIDDLE_HAND; }
		@Override
		public Player getLeftNeighbor() { return FORE_HAND; }
	};
	
	/**
	 * Gets order of a player
	 * 
	 * @return Order of the player
	 */
	public abstract int getOrder();
	
	/**
	 * Gets the player right from the player
	 * 
	 * @return Player right from the player
	 */
	public abstract Player getRightNeighbor();
	
	/**
	 * Gets the player left from the player
	 * 
	 * @return Player left from the player
	 */
	public abstract Player getLeftNeighbor();
}
