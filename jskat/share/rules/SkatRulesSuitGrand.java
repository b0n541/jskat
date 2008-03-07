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
 * Implements some methods of the interface SkatRules that are the same
 * in suit and grand games
 */
public abstract class SkatRulesSuitGrand implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#isCardAllowed(jskat.share.Card, jskat.share.CardVector, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public abstract boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump);

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card, jskat.share.Card, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public abstract boolean isCardBeatsCard(Card card, Card cardToBeat, Card initialCard, SkatConstants.Suits trump);

	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	public abstract int getGameResult(SkatGameData gameData);

	/**
	 * @see jskat.share.rules.SkatRules#isGameWon(jskat.data.SkatGameData)
	 */
	public boolean isGameWon(SkatGameData gameData) {
		// TODO implement it
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchneider(jskat.data.SkatGameData)
	 */
	public boolean isSchneider(SkatGameData gameData) {
		// TODO implement it
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchwarz(jskat.data.SkatGameData)
	 */
	public boolean isSchwarz(SkatGameData gameData) {
		// TODO implement it
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isDurchMarsch(int, jskat.data.SkatGameData)
	 */
	public boolean isDurchMarsch(int playerID, SkatGameData gameData) {
		// there is no durchmarsch in suit or grand games
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isJungFrau(int, jskat.data.SkatGameData)
	 */
	public boolean isJungFrau(int playerID, SkatGameData gameData) {
		// there is no jungfrau in suit or grand games
		return false;
	}
}
