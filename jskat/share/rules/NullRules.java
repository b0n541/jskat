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
 * Implementation of skat rules for Null games
 * 
 */
public class NullRules implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	public int getGameResult(SkatGameData gameData) {

		int gameValue = SkatConstants.NULL_VAL; // At first set to standard
		// value for a Null game
		int multiplier = 1;

		if (gameData.isHand()) {

			// if it was played Hand and game was not lost
			gameValue = SkatConstants.NULLHAND_VAL;

			if (gameData.isOuvert()) {

				// if it was played Hand and Ouvert
				gameValue = SkatConstants.NULLHANDOUVERT_VAL;
			}

		} else {

			if (gameData.isOuvert()) {

				// if it was only played Ouvert
				gameValue = SkatConstants.NULLOUVERT_VAL;
			}
		}

		// TODO: if handled correctly in the game announcement dialog,
		// overbidding should not be possible for null games
		while (gameValue < gameData.getBidValue()) {

			log.debug("Überreizt!!!");

			gameData.setGameLost(true);

			if (gameValue == SkatConstants.NULL_VAL) {

				gameValue = SkatConstants.NULLHAND_VAL;

			} else if (gameValue == SkatConstants.NULLHAND_VAL) {

				gameValue = SkatConstants.NULLOUVERT_VAL;

			} else if (gameValue == SkatConstants.NULLOUVERT_VAL) {

				gameValue = SkatConstants.NULLHANDOUVERT_VAL;
			}

			log.debug("grading up value to " + gameValue);
		}

		if (gameData.isGameLost()) {

			// Lost game is always counted double
			multiplier = multiplier * -2;
		}

		return gameValue * multiplier;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card,
	 *      jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardBeatsCard(Card card, Card cardToBeat,
			SkatConstants.Suits trump) {

		boolean result = false;

		if (cardToBeat.getSuit() == card.getSuit()) {
			
			if (cardToBeat.getNullOrder() < card.getNullOrder()) {
				
				result = true;
			}
		}

		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isCardAllowed(jskat.share.Card,
	 *      jskat.share.CardVector, jskat.share.Card,
	 *      jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump) {
		
		boolean result = false;
		
		if (card.getSuit() == initialCard.getSuit()) {
			
			result = true;
		}
		else if (!hand.hasSuit(SkatConstants.GameTypes.NULL, initialCard.getSuit())) {
			
			result = true;
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isGameWon(jskat.data.SkatGameData)
	 */
	public boolean isGameWon(SkatGameData gameData) {
		
		boolean result = true;
		
		for (int i = 0; i < gameData.getTricks().size(); i++) {
			
			if (gameData.getTrickWinner(i) == gameData.getSinglePlayer()) {
				// the single player won at least one trick
				result = false;
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

			if (hand.getCard(index).getSuit() == suit) {

				result = true;
			}

			index++;
		}

		return result;
	}
}
