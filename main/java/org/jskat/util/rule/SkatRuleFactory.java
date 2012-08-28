/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
	public static SkatRule getSkatRules(GameType gameType) {

		SkatRule rules = null;

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

	private static SuitRule suitRules = new SuitRule();

	private static GrandRule grandRules = new GrandRule();

	private static SkatRule nullRules = new NullRule();

	private static SkatRule ramschRules = new RamschRule();
}
