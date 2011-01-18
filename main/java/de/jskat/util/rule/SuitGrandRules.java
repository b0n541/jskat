/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData;
import de.jskat.util.Player;
import de.jskat.util.SkatConstants;

/**
 * Implements some methods of the interface SkatRules that are the same in suit
 * and grand games
 */
public abstract class SuitGrandRules extends SuitGrandRamschRules {

	private static Log log = LogFactory.getLog(SuitGrandRules.class);

	/**
	 * @see BasicSkatRules#calcGameWon(SkatGameData)
	 */
	public final boolean calcGameWon(SkatGameData gameData) {

		boolean result = false;

		if (gameData.getScore(gameData.getDeclarer()) > 60) {
			// the single player has made more than 60 points
			result = true;
		}

		return result;
	}

	/**
	 * @see SuitGrandRamschRules#calcGameResult(SkatGameData)
	 */
	public int calcGameResult(SkatGameData gameData) {

		int multiplier = getBaseMultiplier(gameData);

		log.debug("calcSuitResult: after Jacks and Trump: multiplier " + multiplier); //$NON-NLS-1$

		if (gameData.isHand() && !gameData.isGameLost()) {

			// Hand game is only counted when game was not lost
			multiplier++;

			log.debug("calcSuitResult: hand game: multiplier " + multiplier); //$NON-NLS-1$
		}

		if (gameData.isOuvert()) {

			multiplier++;
		}

		if (gameData.isSchneider()) {

			multiplier++;

			if (gameData.isSchneiderAnnounced()) {

				multiplier++;
			}

			log.debug("calcSuitResult: Schneider: multiplier " + multiplier); //$NON-NLS-1$
		}

		if (gameData.isSchwarz()) {

			multiplier++;

			if (gameData.isSchwarzAnnounced()) {

				multiplier++;
			}

			log.debug("calcSuitResult: Schwarz: multiplier " + multiplier); //$NON-NLS-1$
		}

		int gameValue = SkatConstants.getGameBaseValue(gameData.getGameType(),
				gameData.isHand(), gameData.isOuvert());

		log.debug("gameMultiplier " + gameValue); //$NON-NLS-1$

		int result = gameValue * multiplier;

		if (gameData.isGameLost()) {

			// penalty if game lost
			result = result * -2;
		}

		return result;
	}

	/**
	 * Gets the multiplier for the game only considering jacks
	 * 
	 * @param gameData
	 *            Game data
	 * @return Multiplier for the game only considering jacks
	 */
	protected int getJackMultiplier(SkatGameData gameData) {

		// FIXME: multiplier should already be calculated at the beginning of
		// the game - skat & suit cards also need to be considered

		int multiplier = 2; // it's the lowest multiplier,
		// "with/without one play two"
		if (gameData.getClubJack()) {
			// game was played with jacks
			if (gameData.getSpadeJack()) {
				multiplier++;
				if (gameData.getHeartJack()) {
					multiplier++;
					if (gameData.getDiamondJack()) {
						multiplier++;
					}
				}
			}
			log.debug("game played with " + (multiplier - 1) + " jack(s)"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			// game was played without jacks
			if (!gameData.getSpadeJack()) {
				multiplier++;
				if (!gameData.getHeartJack()) {
					multiplier++;
					if (!gameData.getDiamondJack()) {
						multiplier++;
					}
				}
			}
			log.debug("game played without " + (multiplier - 1) + " jack(s)"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return multiplier;
	}

	/**
	 * Gets the base multiplier for the game
	 * 
	 * @param gameData
	 *            Game data
	 * @return Base multiplier
	 */
	protected abstract int getBaseMultiplier(SkatGameData gameData);

	/**
	 * Checks whether a game was a schneider game<br>
	 * schneider means one party made only 30 points or below
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schneider game
	 */
	public boolean isSchneider(SkatGameData gameData) {

		boolean result = false;

		Player singlePlayer = gameData.getDeclarer();

		if (gameData.isGameLost()) {

			if (gameData.getScore(singlePlayer) < 31) {
				// single player lost and has also played schneider
				result = true;
			}
		} else {

			if (gameData.getScore(singlePlayer.getLeftNeighbor()) < 31
					|| gameData.getScore(singlePlayer.getRightNeighbor()) < 31) {
				// one of the opponents has played schneider
				result = true;
			}
		}

		return result;
	}

	/**
	 * Checks whether a game was a schwarz game<br>
	 * schwarz means one party made no trick
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schwarz game
	 */
	public boolean isSchwarz(SkatGameData gameData) {

		boolean result = false;

		int trickWinnerCount[] = { 0, 0, 0 };

		for (int i = 0; i < gameData.getTricks().size(); i++) {
			// count all tricks made by the players
			trickWinnerCount[gameData.getTrickWinner(i).ordinal()]++;
		}

		Player singlePlayer = gameData.getDeclarer();

		if (gameData.isGameLost()) {

			if (trickWinnerCount[singlePlayer.ordinal()] == 0) {
				// single player lost and has also played schwarz
				result = true;
			} else {

				if (trickWinnerCount[(singlePlayer.ordinal() + 1) % 3] == 0
						|| trickWinnerCount[(singlePlayer.ordinal() + 2) % 3] == 0) {
					// one of the opponents has played schwarz
					result = true;
				}
			}
		}

		return result;
	}
}
