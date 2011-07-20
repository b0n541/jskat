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
package org.jskat.util.rule;

import org.jskat.util.GameType;

/**
 * Builds a SkatRules object according to the given game type
 */
public class SkatRuleFactory {

	/**
	 * Returns the right SkatRules object according to the game type
	 * 
	 * @param gameType
	 *            Game type
	 * @return SkatRules object
	 */
	public static BasicSkatRules getSkatRules(GameType gameType) {

		BasicSkatRules rules = null;

		switch (gameType) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
			rules = suitRules;
			break;
		case GRAND:
			rules = grandRules;
			break;
		case NULL:
			rules = nullRules;
			break;
		case RAMSCH:
			rules = ramschRules;
			break;
		case PASSED_IN:
			break;
		}

		return rules;
	}

	private static SuitRules suitRules = new SuitRules();

	private static GrandRules grandRules = new GrandRules();

	private static BasicSkatRules nullRules = new NullRules();

	private static BasicSkatRules ramschRules = new RamschRules();
}
