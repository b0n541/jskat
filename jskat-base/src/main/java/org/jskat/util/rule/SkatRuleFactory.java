
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
