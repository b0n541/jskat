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

	public boolean isGameWon(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSchneider(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSchwarz(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDurchMarsch(int playerID, SkatGameData gameData) {
		return false;
	}

	public boolean isJungFrau(int playerID, SkatGameData gameData) {
		return false;
	}

	public abstract int getGameResult(SkatGameData gameData);

	public abstract boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump);

	public abstract boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard);
}
