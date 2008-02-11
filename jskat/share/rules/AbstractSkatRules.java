/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share.rules;

import org.apache.log4j.Logger;

import jskat.data.SkatGameData;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

/**
 * Abstract skat rules, all skat rules are extended from this class
 * 
 * @author Jan Sch&auml;fer
 */
public abstract class AbstractSkatRules {

	static Logger log = Logger.getLogger(jskat.share.rules.AbstractSkatRules.class);

	/**
	 * Checks, whether the given card is allowed to be played, also considering
	 * the rest of the hand
	 * 
	 */
	public static boolean isCardAllowed(Card card, CardVector hand,
			Card initialCard, SkatConstants.GameTypes gameType, SkatConstants.Suits currentTrump) {

		log.debug("isCardAllowed card: " + card + " initialCard: "
				+ initialCard + " gameType: " + gameType + " currentTrump: "
				+ currentTrump+", hand: "+hand);

		boolean result = false;

		if (initialCard.isTrump(gameType, currentTrump)) {
			
			// it's easy is the first card is a trump card
			
			if (card.isTrump(gameType, currentTrump)) {
				
				result = true;
			}
			else if (!hand.hasTrump(gameType, currentTrump)) {
				
				result = true;
			}
		}
		else {
			
			// if the first card isn't a trump card, it's more difficult cause of the jacks
			
			if (initialCard.getSuit() == card.getSuit() && !card.isTrump(gameType, currentTrump)) {
				
				result = true;
			}
			else if (!hand.hasSuit(gameType, initialCard.getSuit())) {
				
				result = true;
			}
		}
/*
		else if (card.isTrump(gameType, currentTrump)) {

			if (initialCard.isTrump(gameType, currentTrump)) {
				log.debug("Card [" + card + "] is allowed on [" + initialCard
						+ "] - both are trump.");
				result = true;
				
			} else if (!hand.hasSuit(gameType, initialCard.getSuit())) {
				log.debug("Card [" + card + "] is allowed on [" + initialCard
						+ "] - card is trump and cannot match initial suit.");
				result = true;
			}
		}
		// card is not trump
		else if (initialCard.isTrump(gameType, currentTrump)
				&& !hand.hasTrump(currentTrump)) {
			log.debug("Card [" + card + "] is allowed on [" + initialCard
					+ "] - initial card is trump and player has no trump.");
			result = true;
			
		} else if (!initialCard.isTrump(gameType, currentTrump)
				&& initialCard.getSuit() == card.getSuit()) {
			log.debug("Card [" + card + "] is allowed on [" + initialCard
					+ "] - no trump and suits match.");
			result = true;
			
		} else if (!initialCard.isTrump(gameType, currentTrump)
				&& !hand.hasSuit(gameType, initialCard.getSuit())) {

			result = true;
			
		} else if (!hand.hasSuit(initialCard.getSuit())) {
			
			// mjl: fixed bug #979031
			if (!(initialCard.isTrump(gameType, currentTrump) && hand
					.hasTrump(currentTrump))) {
				log.debug("Card [" + card + "] is allowed on [" + initialCard
						+ "] - player cannot match initial suit.");
				result = true;
			}
		}
*/
		if (!result)
			
			log.debug("Card [" + card + "] is NOT allowed on [" + initialCard
					+ "].");

		return result;
	}

	public abstract boolean isSchneider(SkatGameData data);
	
	public abstract boolean isSchwarz(SkatGameData data);
	
	public abstract boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard);
}
