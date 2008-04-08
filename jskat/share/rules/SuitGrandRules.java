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
 * Implements some methods of the interface SkatRules that are the same in suit
 * and grand games
 */
public abstract class SuitGrandRules implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#isCardAllowed(jskat.share.Card,
	 *      jskat.share.CardVector, jskat.share.Card,
	 *      jskat.share.SkatConstants.Suits)
	 */
	public abstract boolean isCardAllowed(Card card, CardVector hand,
			Card initialCard, SkatConstants.Suits trump);

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card,
	 *      jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public abstract boolean isCardBeatsCard(Card card, Card cardToBeat,
			SkatConstants.Suits trump);

	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	public abstract int getGameResult(SkatGameData gameData);

	/**
	 * @see jskat.share.rules.SkatRules#isGameWon(jskat.data.SkatGameData)
	 */
	public final boolean isGameWon(SkatGameData gameData) {
		
		boolean result = false;
		
		if (gameData.getScore(gameData.getSinglePlayer()) > 60) {
			// the single player has made more than 60 points
			result = true;
		}
		
		return result;
	}
}
