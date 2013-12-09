/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.table;

/**
 * Panel types for all game contexts
 */
public enum ContextPanelType {

	/**
	 * Start panel
	 */
	START,
	/**
	 * Bidding panel
	 */
	BIDDING,
	/**
	 * Declaring panel
	 */
	DECLARING,
	/**
	 * Schieberamsch panel
	 */
	SCHIEBERAMSCH,
	/**
	 * Calling Re after Contra panel
	 */
	RE_AFTER_CONTRA,
	/**
	 * Trick playing panel
	 */
	TRICK_PLAYING,
	/**
	 * Game over panel
	 */
	GAME_OVER
}
