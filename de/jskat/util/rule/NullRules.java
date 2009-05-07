/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData;
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.SkatConstants;
import de.jskat.util.Suit;

/**
 * Implementation of skat rules for Null games
 * 
 */
public class NullRules implements BasicSkatRules {

	private static Log log = LogFactory.getLog(NullRules.class);
	
	/**
	 * @see BasicSkatRules#calcGameResult(SkatGameData)
	 */
	public int calcGameResult(SkatGameData gameData) {

		int gameValue = SkatConstants.getGameBaseValue(gameData.getGameType(), gameData.isHand(), gameData.isOuvert());
		int multiplier = 1;
		
		if (gameData.isGameLost()) {

			// Lost game is always counted double
			multiplier = multiplier * -2;
		}

		return gameValue * multiplier;
	}

	/**
	 * @see BasicSkatRules#isCardBeatsCard(GameType, Card, Card)
	 */
	public boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card) {

		boolean result = false;

		if (cardToBeat.getSuit() == card.getSuit()) {

			if (cardToBeat.getNullOrder() < card.getNullOrder()) {

				result = true;
			}
		}

		return result;
	}

	/**
	 * @see BasicSkatRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	public boolean isCardAllowed(GameType gameType, Card initialCard, CardList hand, Card card) {

		boolean result = false;

		if (initialCard == null) {
			// no intial card is given --> every card is allowed
			result = true;
		}
		else if (card.getSuit() == initialCard.getSuit()) {
			// card must serve same suit
			result = true;
			
		} else if (!hand.hasSuit(gameType, initialCard.getSuit())) {
			// hand has no card of same suit --> every card is allowed
			result = true;
		}

		return result;
	}

	/**
	 * @see BasicSkatRules#calcGameWon(SkatGameData)
	 */
	public boolean calcGameWon(SkatGameData gameData) {

		boolean result = true;

		for (int i = 0; i < gameData.getTricks().size(); i++) {

			if (gameData.getTrickWinner(i) == gameData.getDeclarer()) {
				// the single player has won at least one trick
				result = false;
			}
		}

		return result;
	}

	/**
	 * @see BasicSkatRules#hasSuit(GameType, CardList, Suit)
	 */
	public boolean hasSuit(GameType gameType, CardList hand, Suit suit) {

		boolean result = false;

		int index = 0;
		while (result == false && index < hand.size()) {

			if (hand.get(index).getSuit() == suit) {

				result = true;
			}

			index++;
		}

		return result;
	}
}
