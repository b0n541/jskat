/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/
package jskat.share.rules;

import jskat.data.SkatGameData;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

/**
 * Implementation of skat rules for Ramsch games
 *
 */
public class SkatRulesRamsch implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	public int getGameResult(SkatGameData gameData) {

		int multiplier = 1;

		// TODO two player can be jungfrau
		if (gameData.isJungFrau()) {
			multiplier = multiplier * 2;
		}

		multiplier = multiplier * (new Double(Math.pow(2, gameData.getGeschoben()))).intValue();

		if (gameData.isGameLost()) {
			multiplier = multiplier * -1;
		}

		// FIXME Ramsch games have no single player
		return gameData.getScore(gameData.getSinglePlayer()) * multiplier;
	}
	
	/**
	 * @see jskat.share.rules.SkatRules#isCardBeats(jskat.share.Card, jskat.share.Card, jskat.share.Card)
	 */
	public boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchneider(jskat.data.SkatGameData)
	 */
	public boolean isSchneider(SkatGameData gameData) {
		// there is no schneider in Ramsch games
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchwarz(jskat.data.SkatGameData)
	 */
	public boolean isSchwarz(SkatGameData gameData) {
		// there is no schwarz in Ramsch games
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
	 * @see jskat.share.rules.SkatRules#isDurchMarsch(int, jskat.data.SkatGameData)
	 */
	public boolean isDurchMarsch(int playerID, SkatGameData gameData) {
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
	 * @see jskat.share.rules.SkatRules#isJungFrau(int, jskat.data.SkatGameData)
	 */
	public boolean isJungFrau(int playerID, SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
