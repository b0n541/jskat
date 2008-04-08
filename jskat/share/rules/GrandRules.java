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
import jskat.share.SkatConstants.GameTypes;

/**
 * Implementation of skat rules for Grand games
 * 
 */
public class GrandRules extends SuitGrandRules implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card,
	 *      jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardBeatsCard(Card card, Card cardToBeat,
			SkatConstants.Suits trump) {

		boolean result = false;

		if (cardToBeat.isTrump(GameTypes.GRAND)) {
			// card to beat is a trump card
			if (card.isTrump(GameTypes.GRAND) &&
					cardToBeat.getSuit().getSuitOrder() < card.getSuit().getSuitOrder()) {
				// card is a trump card too and has higher suit order
				result = true;
			}
		} else {
			// card to beat is not a trump card
			if (card.isTrump(GameTypes.GRAND)) {
				// card is a trump card
				result = true;
			}
			else if (cardToBeat.getSuit() == card.getSuit() &&
				cardToBeat.getSuitGrandOrder() < card.getSuitGrandOrder()) {
				// cards have the same suit and card has higher order in suit/grand games
				result = true;
			}
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SuitGrandRules#isCardAllowed(jskat.share.Card,
	 *      jskat.share.CardVector, jskat.share.Card,
	 *      jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump) {

		boolean result = false;

		if (initialCard.isTrump(SkatConstants.GameTypes.GRAND)) {

			if (card.isTrump(SkatConstants.GameTypes.GRAND)) {

				result = true;
			}
		} else {

			if (initialCard.getSuit() == card.getSuit()) {

				result = true;
			} else if (!hand.hasSuit(SkatConstants.GameTypes.GRAND, initialCard
					.getSuit())) {

				result = true;
			}
		}

		return result;
	}

	/**
	 * @see jskat.share.rules.SuitGrandRules#getGameResult(jskat.data.SkatGameData)
	 */
	@Override
	public int getGameResult(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see jskat.share.rules.SkatRules#hasSuit(jskat.share.CardVector,
	 *      jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Suits)
	 */
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
}
