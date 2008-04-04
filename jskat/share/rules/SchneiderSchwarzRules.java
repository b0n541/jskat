/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package jskat.share.rules;

import jskat.data.SkatGameData;
import jskat.share.Card;
import jskat.share.SkatConstants.Suits;

/**
 * Implements schneider and schwarz rules
 */
public class SchneiderSchwarzRules extends TrumpDecorator {

	SchneiderSchwarzRules(TrumpDecorator rules) {
		
		decoratedRules = rules;
	}
	
	/**
	 * Checks whether a game was a schneider game<br>
	 * schneider means one party made only 30 points or below
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schneider game
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
					gameData.getScore((singlePlayer + 2) % 3) < 31) {
				// one of the opponents has played schneider
				result = true;
			}
		}
		
		return result;
	}

	/**
	 * Checks whether a game was a schwarz game<br>
	 * schwarz means one party made no trick
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schwarz game
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
	 * @see jskat.share.rules.TrumpDecorator#isTrump(jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isTrump(Card card, Suits trump) {
		
		return ((TrumpDecorator)decoratedRules).isTrump(card, trump);
	}
}
