package jskat.share.rules;

import jskat.share.Tools;

public class SkatRulesRamsch extends AbstractSkatRules {

	/**
	 * Checks whether a player did a durchmarsch
	 * 
	 * @param playerID Player ID of the player to be checked
	 * @param tricks Tricks of the game
	 * @return TRUE if the player did a durchmarsch
	 */
	public static boolean getDurchmarsch(int playerID, int[] tricks) {
		
		// TODO check whether the implementation is correct
		log.debug("isDurschmarsch: tricks=" + Tools.dump(tricks));
		boolean result = false;
		boolean checker = false;
		for (int i = 0; i < tricks.length; i++) {
			if (tricks[i] == 10) {
				if (!checker) {
					result = i;
				} else {
					log.warn("There are more than 10 tricks! tricks="
							+ Tools.dump(tricks));
					result = i;
				}
			} else if (tricks[i] > 0) {
				if (result < 0) {
					checker = true;
				} else {
					log.warn("There are more than 10 tricks! tricks="
							+ Tools.dump(tricks));
					checker = true;
				}
			}
		}
		return result;
	}

	/**
	 * Checks whether a player is jungfrau or not
	 * 
	 * @param playerID Player ID of the player to be checked
	 * @param tricks Tricks of the game
	 * @return TRUE if the player is jungfrau
	 */
	public static boolean isJungfrau(int playerID, int[] tricks) {
		
		// TODO check whether the implementation is correct
		log.debug("isJungfrau: tricks=" + Tools.dump(tricks));
		boolean result = false;
		for (int i = 0; i < tricks.length; i++) {
			if (tricks[i] == 0) {
				if (result < 0) {
					result = i;
				} else {
					log
							.warn("There is more than one player with no tricks! tricks="
									+ Tools.dump(tricks));
					result = i;
				}
			}
		}
		return result;
	}

}
