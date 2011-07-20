/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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
package org.jskat.util;

/**
 * Defines all modes for a skat list
 */
public enum SkatListMode {
	
	/**
	 * Normal mode: plus and minus points for declarer
	 */
	NORMAL,
	/**
	 * Tournament mode: like normal mode plus extra points used in tournaments
	 *                  after Seeger-Fabian system
	 */
	TOURNAMENT,
	/**
	 * Bierlachs mode: Only minus points are listed
	 */
	BIERLACHS;
}
