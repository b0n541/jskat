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
import jskat.share.SkatConstants.Suits;

/**
 * Abstract Decorator for special rules in different game types
 */
public abstract class RuleDecorator implements SkatRules {

	protected SkatRules decoratedRules;
	
	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	public int getGameResult(SkatGameData gameData) {
		
		return decoratedRules.getGameResult(gameData);
	}

	/**
	 * @see jskat.share.rules.SkatRules#hasSuit(jskat.share.CardVector, jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Suits)
	 */
	public boolean hasSuit(CardVector hand, Suits trump, Suits suit) {
		
		return decoratedRules.hasSuit(hand, trump, suit);
	}

	/**
	 * @see jskat.share.rules.SkatRules#isCardAllowed(jskat.share.Card, jskat.share.CardVector, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			Suits trump) {
		
		return decoratedRules.isCardAllowed(card, hand, initialCard, trump);
	}

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card, jskat.share.Card, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardBeatsCard(Card card, Card cardToBeat,
			Card initialCard, Suits trump) {
		
		return decoratedRules.isCardBeatsCard(card, cardToBeat, initialCard, trump);
	}

	/**
	 * @see jskat.share.rules.SkatRules#isGameWon(jskat.data.SkatGameData)
	 */
	public boolean isGameWon(SkatGameData gameData) {
		
		return decoratedRules.isGameWon(gameData);
	}
}
