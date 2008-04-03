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
 * Implements the trump rules for suit games
 */
public class SuitTrumpRules extends TrumpDecorator {

	SuitTrumpRules(SkatRules rules) {
		
		decoratedRules = rules;
	}

	/**
	 * @see jskat.share.rules.TrumpDecorator#isTrump(jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isTrump(Card card, Suits trump) {
		
		return (card.getRank() == SkatConstants.Ranks.JACK || card.getSuit() == trump);
	}
}
