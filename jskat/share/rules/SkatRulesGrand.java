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
import jskat.share.SkatConstants.Suits;

/**
 * Implementation of skat rules for Grand games
 *
 */
public class SkatRulesGrand extends SkatRulesSuitGrand implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card, jskat.share.Card, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardBeatsCard(Card card, Card cardToBeat, Card initialTrickCard, SkatConstants.Suits trump) {

		// TODO Auto-generated method stub
		boolean result = false;
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRulesSuitGrand#isCardAllowed(jskat.share.Card, jskat.share.CardVector, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump) {

		boolean result = false;
		
		if (initialCard.isTrump(SkatConstants.GameTypes.GRAND)) {
			
			if (card.isTrump(SkatConstants.GameTypes.GRAND)) {
				
				result = true;
			}
		}
		else {
			
			if (initialCard.getSuit() == card.getSuit()) {
				
				result = true;
			}
			else if (!hand.hasSuit(SkatConstants.GameTypes.GRAND, initialCard.getSuit())) {
				
				result = true;
			}
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRulesSuitGrand#getGameResult(jskat.data.SkatGameData)
	 */
	@Override
	public int getGameResult(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see jskat.share.rules.SkatRules#hasSuit(jskat.share.CardVector, jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean hasSuit(CardVector hand, Suits trump, Suits suit) {
		
		boolean result = false;
		
		int index = 0;
		while (result == false && index < hand.size()) {
			
			if (hand.getCard(index).getSuit() == suit
					&& hand.getCard(index).getRank() != SkatConstants.Ranks.JACK) {

				result = true;
			}
			
			index++;
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isTrump(jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isTrump(Card card, Suits trump) {
		
		return card.getRank() == SkatConstants.Ranks.JACK;
	}
}
