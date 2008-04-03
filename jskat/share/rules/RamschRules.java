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
 * Implementation of skat rules for Ramsch games
 *
 */
public class RamschRules implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	public int getGameResult(SkatGameData gameData) {

		int multiplier = 1;

		// TODO two player can be jungfrau
		if (gameData.isJungfrau()) {
			multiplier = multiplier * 2;
		}

		multiplier = multiplier * (new Double(Math.pow(2, gameData.getGeschoben()))).intValue();

		if (gameData.isGameLost()) {
			multiplier = multiplier * -1;
		}

		// FIXME Ramsch games have no single player
		return gameData.getScore(gameData.getSinglePlayer()) * multiplier;
	}
	
	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card, 
	 * jskat.share.Card, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardBeatsCard(Card card, Card cardToBeat, Card initialCard, 
			SkatConstants.Suits trump) {
		// TODO Auto-generated method stub
		boolean result = false;
		
		if (initialCard.isTrump(SkatConstants.GameTypes.RAMSCH)) {
			// trump card was played as first card
			if (card.isTrump(SkatConstants.GameTypes.RAMSCH)) {
				// card is a trump card
				if (cardToBeat.isTrump(SkatConstants.GameTypes.RAMSCH)) {
					// card to beat is a trump card too
					if (card.getSuit().getSuitOrder() > cardToBeat.getSuit().getSuitOrder()) {
						// only JACKS are trump
						result = true;
					}
				}
				else {
					// card to beat is not a trump card
					result = true;
				}
			}
		}
		else {
			// first card was not a trump card
			if (card.isTrump(SkatConstants.GameTypes.RAMSCH)) {
				// card is a trump card
				if (cardToBeat.isTrump(SkatConstants.GameTypes.RAMSCH)) {
					// card to beat is a trump card too
					if (card.getSuit().getSuitOrder() > cardToBeat.getSuit().getSuitOrder()) {
						// only JACKS are trump
						result = true;
					}
				}
				else {
					// card to beat is not a trump card
					result = true;
				}
			}
			else {
				// card is not a trump card
				if (initialCard.getSuit() == card.getSuit()) {
					// card has same suit as first card
					if (card.getSuit() == cardToBeat.getSuit()) {
						// card to beat has same suit as first card
						if (card.getRamschOrder() > cardToBeat.getRamschOrder()) {
							// card has higher order in ramsch games
							result = true;
						}
					}
					else {
						// card to beat has not same suit as first card
						result = true;
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isCardAllowed(jskat.share.Card, jskat.share.CardVector, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Checks whether a player did a durchmarsch (walkthrough) in a ramsch game<br>
	 * durchmarsch means one player made all tricks
	 * 
	 * @param playerID
	 *            Player ID of the player to be checked
	 * @param gameData
	 *            Game data
	 * @return TRUE if the player played a durchmarsch
	 */
	public final boolean isDurchmarsch(int playerID, SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Checks whether a player was jungfrau (virgin) in a ramsch game<br>
	 * jungfrau means one player made no trick<br>
	 * two players who played jungfrau means a durchmarsch for the third player
	 * 
	 * @param playerID
	 *            Player ID of the player to be checked
	 * @param gameData
	 *            Game data
	 * @return TRUE if the player was jungfrau
	 */
	public final boolean isJungfrau(int playerID, SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isGameWon(jskat.data.SkatGameData)
	 */
	public boolean isGameWon(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#hasSuit(jskat.share.CardVector, jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Suits)
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
