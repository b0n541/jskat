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

/**
 * Implements some methods of the interface SkatRules that are the same in suit
 * and grand games
 */
public abstract class SkatRulesSuitGrand implements SkatRules {

	/**
	 * @see jskat.share.rules.SkatRules#isCardAllowed(jskat.share.Card,
	 *      jskat.share.CardVector, jskat.share.Card,
	 *      jskat.share.SkatConstants.Suits)
	 */
	public abstract boolean isCardAllowed(Card card, CardVector hand,
			Card initialCard, SkatConstants.Suits trump);

	/**
	 * @see jskat.share.rules.SkatRules#isCardBeatsCard(jskat.share.Card,
	 *      jskat.share.Card, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	public abstract boolean isCardBeatsCard(Card card, Card cardToBeat,
			Card initialCard, SkatConstants.Suits trump);

	/**
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	public abstract int getGameResult(SkatGameData gameData);

	/**
	 * @see jskat.share.rules.SkatRules#isGameWon(jskat.data.SkatGameData)
	 */
	public boolean isGameWon(SkatGameData gameData) {
		
		boolean result = false;
		
		if (gameData.getScore(gameData.getSinglePlayer()) > 60) {
			// the single player has made more than 60 points
			result = true;
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchneider(jskat.data.SkatGameData)
	 */
	public boolean isSchneider(SkatGameData gameData) {
		
		boolean result = false;
		
		int singlePlayer = gameData.getSinglePlayer();
		
		if (gameData.isGameLost()) {
			
			if (gameData.getScore(singlePlayer) < 31) {
				// single player lost and has also played schneider
				result = true;
			}
		}
		else {
			
			if (gameData.getScore((singlePlayer + 1) % 3) < 31 ||
					gameData.getScore((singlePlayer + 3) % 3) < 31) {
				// one of the opponents has played schneider
				result = true;
			}
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isSchwarz(jskat.data.SkatGameData)
	 */
	public boolean isSchwarz(SkatGameData gameData) {
		
		boolean result = false;
		
		int trickWinnerCount[] = {0, 0, 0};
		
		for (int i = 0; i < gameData.getTricks().size(); i++) {
			// count all tricks made by the players
			trickWinnerCount[gameData.getTrickWinner(i)]++;
		}
		
		int singlePlayer = gameData.getSinglePlayer();
		
		if (gameData.isGameLost()) {
			
			if (trickWinnerCount[singlePlayer] == 0) {
				// single player lost and has also played schwarz
				result = true;
			}
			else {
				
				if (trickWinnerCount[(singlePlayer + 1) % 3] == 0 ||
						trickWinnerCount[(singlePlayer + 2) % 3] == 0) {
					// one of the opponents has played schwarz
					result = true;
				}
			}
		}
		
		return result;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isDurchMarsch(int,
	 *      jskat.data.SkatGameData)
	 */
	public boolean isDurchMarsch(int playerID, SkatGameData gameData) {
		// there is no durchmarsch in suit or grand games
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRules#isJungFrau(int, jskat.data.SkatGameData)
	 */
	public boolean isJungFrau(int playerID, SkatGameData gameData) {
		// there is no jungfrau in suit or grand games
		return false;
	}
}
