package jskat.share.rules;

import jskat.data.SkatGameData;
import jskat.share.Card;
import jskat.share.SkatConstants;

public class SkatRulesNull extends AbstractSkatRules {

	/** 
	 * @see jskat.share.rules.SkatRules
	 */
	public int getGameResult(SkatGameData gameData) {


		int gameValue = SkatConstants.NULL_VAL; // At first set to standard
		// value for a Null game
		int multiplier = 1;

		if (gameData.isHand()) {
			
			// if it was played Hand and game was not lost
			gameValue = SkatConstants.NULLHAND_VAL;
	
			if (gameData.isOuvert()) {
	
				// if it was played Hand and Ouvert
				gameValue = SkatConstants.NULLHANDOUVERT_VAL;
			}
	
		} else {
	
			if (gameData.isOuvert()) {
	
				// if it was only played Ouvert
				gameValue = SkatConstants.NULLOUVERT_VAL;
			}
		}

		// TODO: if handled correctly in the game announcement dialog,
		// overbidding should not be possible for null games
		while (gameValue < gameData.getBidValue()) {
	
			log.debug("Überreizt!!!");
	
			gameData.setGameLost(true);
	
			if (gameValue == SkatConstants.NULL_VAL) {
	
				gameValue = SkatConstants.NULLHAND_VAL;
	
			} else if (gameValue == SkatConstants.NULLHAND_VAL) {
	
				gameValue = SkatConstants.NULLOUVERT_VAL;
	
			} else if (gameValue == SkatConstants.NULLOUVERT_VAL) {
	
				gameValue = SkatConstants.NULLHANDOUVERT_VAL;
			}
	
			log.debug("grading up value to " + gameValue);
		}
	
		if (gameData.isGameLost()) {
	
			// Lost game is always counted double
			multiplier = multiplier * -2;
		}
	
		return gameValue * multiplier;
	}

	@Override
	public boolean isSchneider(SkatGameData data) {
		return false;
	}

	@Override
	public boolean isSchwarz(SkatGameData data) {
		return false;
	}
}
