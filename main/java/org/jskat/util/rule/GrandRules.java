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


package org.jskat.util.rule;

import org.jskat.data.SkatGameData;

/**
 * Implementation of skat rules for Grand games
 */
public class GrandRules extends SuitGrandRules {

	/**
	 * @see SuitGrandRules#getBaseMultiplier(SkatGameData)
	 */
	@Override
	protected int getBaseMultiplier(SkatGameData gameData) {
		
		return this.getJackMultiplier(gameData);
	}
}
