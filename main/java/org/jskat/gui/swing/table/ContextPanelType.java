/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.1
 * Copyright (C) 2013-05-10
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
	 * Trick playing panel
	 */
	TRICK_PLAYING,
	/**
	 * Game over panel
	 */
	GAME_OVER
}
