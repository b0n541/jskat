/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/
package jskat.share.rules;

import org.apache.log4j.Logger;

import jskat.data.SkatGameData;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

/**
 * Implementation of skat rules for Null games
 *
 */
public class SkatRulesNull implements SkatRules {

	static Logger log = Logger.getLogger(jskat.share.rules.SkatRulesNull.class);

	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
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

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeats(jskat.share.Card, jskat.share.Card, jskat.share.Card)
	 */
	public boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isCardAllowed(jskat.share.Card, jskat.share.CardVector, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isGameWon(jskat.data.SkatGameData)
	 */
	public boolean isGameWon(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchneider(jskat.data.SkatGameData)
	 */
	public boolean isSchneider(SkatGameData gameData) {
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchwarz(jskat.data.SkatGameData)
	 */
	public boolean isSchwarz(SkatGameData gameData) {
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isDurchMarsch(int, jskat.data.SkatGameData)
	 */
	public boolean isDurchMarsch(int playerID, SkatGameData gameData) {
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isJungFrau(int, jskat.data.SkatGameData)
	 */
	public boolean isJungFrau(int playerID, SkatGameData gameData) {
		return false;
	}
}
