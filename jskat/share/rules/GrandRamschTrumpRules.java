/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package jskat.share.rules;

import jskat.share.Card;
import jskat.share.SkatConstants;
import jskat.share.SkatConstants.Suits;

/**
 * Implements the trump rules for grand and ramsch games
 */
public class GrandRamschTrumpRules extends TrumpDecorator {

	GrandRamschTrumpRules(SkatRules rules) {
		
		decoratedRules = rules;
	}

	/**
	 * @see jskat.share.rules.TrumpDecorator#isTrump(jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isTrump(Card card, Suits trump) {
		
		return card.getRank() == SkatConstants.Ranks.JACK;
	}
}
