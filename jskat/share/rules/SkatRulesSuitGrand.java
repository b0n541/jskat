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

/**
 * Implements some methods of the interface SkatRules that are the same
 * in suit and grand games
 */
public abstract class SkatRulesSuitGrand implements SkatRules {

	@Override
	public boolean isGameWon(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSchneider(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSchwarz(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDurchMarsch(int playerID, SkatGameData gameData) {
		return false;
	}

	@Override
	public boolean isJungFrau(int playerID, SkatGameData gameData) {
		return false;
	}

	@Override
	public abstract int getGameResult(SkatGameData gameData);

	@Override
	public abstract boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatGameData gameData);

	@Override
	public abstract boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard);
}
