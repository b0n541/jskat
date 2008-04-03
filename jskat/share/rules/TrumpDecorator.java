/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package jskat.share.rules;

import jskat.share.Card;
import jskat.share.SkatConstants;

/**
 * Defines new rules for trump games
 */
public abstract class TrumpDecorator extends RuleDecorator {

	/**
	 * Checks whether a card is a trump card
	 * 
	 * @param card
	 *            Card to be checked
	 * @param trump
	 *            Trump suit in suit games, NULL otherwise
	 * @return TRUE if the card is a trump card
	 */
	public abstract boolean isTrump(Card card, SkatConstants.Suits trump);
}
