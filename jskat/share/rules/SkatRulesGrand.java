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
 * Implementation of skat rules for Grand games
 *
 */
public class SkatRulesGrand extends SkatRulesSuitGrand implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRulesSuitGrand#isCardBeats(jskat.share.Card, jskat.share.Card, jskat.share.Card)
	 */
	@Override
	public boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRulesSuitGrand#isCardAllowed(jskat.share.Card, jskat.share.CardVector, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRulesSuitGrand#getGameResult(jskat.data.SkatGameData)
	 */
	@Override
	public int getGameResult(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return 0;
	}
}
