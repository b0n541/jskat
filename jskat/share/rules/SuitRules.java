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
 * Implementation of skat rules for Suit games
 * 
 */
public class SuitRules extends SuitGrandRules implements SkatRules {

	/**
	 * @see jskat.share.rules.SuitGrandRules#getGameResult(jskat.data.SkatGameData)
	 */
	@Override
	public int getGameResult(SkatGameData gameData) {

		// TODO: multiplier should already be calculated at the beginning of the
		// game - skat & suit cards also need to be considered
		int multiplier = 2; // it's the lowest multiplier,
		// "with/without one play two"

		if (gameData.getClubJack()) {
			// game was played with jacks
			if (gameData.getSpadeJack()) {
				multiplier++;
				if (gameData.getHeartJack()) {
					multiplier++;
					if (gameData.getDiamondJack()) {
						multiplier++;
					}
				}
			}
			log.debug("game played with " + (multiplier - 1) + " jack(s)");
		} else {
			// game was played without jacks
			if (!gameData.getSpadeJack()) {
				multiplier++;
				if (!gameData.getHeartJack()) {
					multiplier++;
					if (!gameData.getDiamondJack()) {
						multiplier++;
					}
				}
			}
			log.debug("game played without " + (multiplier - 1) + " jack(s)");
		}

		log.debug("calcSuitResult: after Jacks: multiplier " + multiplier);

		// TODO: evaluate higher game levels
		// in a suit game, there can even be higher multipliers
		// start with trump ace, ten, etc.

		if (gameData.isHand() && !gameData.isGameLost()) {

			// Hand game is only counted when game was not lost
			multiplier++;

			log.debug("calcSuitResult: hand game: multiplier " + multiplier);
		}

		if (gameData.isOuvert()) {

			multiplier++;
		}

		if (gameData.isSchneider()) {

			multiplier++;

			if (gameData.isSchneiderAnnounced()) {

				multiplier++;
			}

			log.debug("calcSuitResult: Schneider: multiplier " + multiplier);
		}

		if (gameData.isSchwarz()) {

			multiplier++;

			if (gameData.isSchwarzAnnounced()) {

				multiplier++;
			}

			log.debug("calcSuitResult: Schwarz: multiplier " + multiplier);
		}

		int gameValue = 0; // the multiplier for the game type

		SkatConstants.Suits trump = gameData.getTrump();

		if (trump == SkatConstants.Suits.CLUBS) {

			gameValue = SkatConstants.CLUBS_VAL;
		} else if (trump == SkatConstants.Suits.SPADES) {

			gameValue = SkatConstants.SPADES_VAL;
		} else if (trump == SkatConstants.Suits.HEARTS) {

			gameValue = SkatConstants.HEARTS_VAL;
		} else if (trump == SkatConstants.Suits.DIAMONDS) {

			gameValue = SkatConstants.DIAMONDS_VAL;
		}

		log.debug("gameMultiplier " + gameValue);

		int result = gameValue * multiplier;

		log.debug("game value=" + result + ", bid value="
				+ gameData.getBidValue());

		if (result < gameData.getBidValue()) {

			log.debug("Überreizt!!!");
			gameData.setGameLost(true);
			gameData.setOverBidded(true);

			while (result < gameData.getBidValue()) {

				result = gameValue * ++multiplier;
			}

			log.debug("grading up value to " + result);
		}

		if (gameData.isGameLost()) {

			// penalty if game lost
			result = result * -2;
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

		log.debug(card + " " + hand + " " + initialCard + " " + trump);

		if (initialCard.isTrump(SkatConstants.GameTypes.SUIT, trump)) {

			if (card.isTrump(SkatConstants.GameTypes.SUIT, trump)) {
				// only trump cards are allowed
				result = true;
			}
			else if (!hand.hasTrump(SkatConstants.GameTypes.SUIT, trump)) {
				// no trump cards on players hand
				result = true;
			}
		} 
		else {
			
			if (hand.hasSuit(SkatConstants.GameTypes.SUIT, trump,
					initialCard.getSuit())) {
				
				if (initialCard.getSuit() == card.getSuit()
						&& !card.isTrump(SkatConstants.GameTypes.SUIT, trump)) {
					// card has to follow suit 
					result = true;
				}
			} 
			else {
				// no suit of initial card on players hand
				result = true;
			}
		}

		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card,
	 *      jskat.share.Card, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isCardBeatsCard(Card card, Card cardToBeat,
			Card initialCard, SkatConstants.Suits trump) {

		boolean result = false;
		
		log.debug(card + " " + cardToBeat + " " + trump);
	
		if (cardToBeat.isTrump(SkatConstants.GameTypes.SUIT, trump)) {
			// card must be trump to beat other card
			if (card.isTrump(SkatConstants.GameTypes.SUIT, trump) &&
					cardToBeat.getSuitGrandOrder() < card.getSuitGrandOrder()) {
				// card is trump and has higher order in suit/grand games
				result = true;
			}
		}
		else {
			// other card was not trump
			if (card.isTrump(SkatConstants.GameTypes.SUIT, trump)) {
				// card is a trump card 
				result = true;
			}
			else if (card.getSuit() == cardToBeat.getSuit() &&
						cardToBeat.getSuitGrandOrder() < card.getSuitGrandOrder()) {
				// card has higher order in suit/grand games
				result = true;
			}
		}

		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#hasSuit(jskat.share.CardVector,
	 *      jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Suits)
	 */
	public boolean hasSuit(CardVector hand, Suits trump, Suits suit) {

		boolean result = false;

		int index = 0;
		while (result == false && index < hand.size()) {

			Card currCard = hand.getCard(index);

			if (currCard.getSuit() == suit && currCard.getSuit() != trump
					&& currCard.getRank() != SkatConstants.Ranks.JACK) {

				result = true;
			}

			index++;
		}

		return result;
	}
}
