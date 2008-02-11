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

public class SkatRulesRamsch implements SkatRules {

	@Override
	public int getGameResult(SkatGameData gameData) {

		int multiplier = 1;

		// TODO two player can be jungfrau
		if (gameData.isJungFrau()) {
			multiplier = multiplier * 2;
		}

		multiplier = multiplier * (new Double(Math.pow(2, gameData.getGeschoben()))).intValue();

		if (gameData.isGameLost()) {
			multiplier = multiplier * -1;
		}

		return gameData.getScore(gameData.getSinglePlayer()) * multiplier;
	}
	
	@Override
	public boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSchneider(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSchwarz(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDurchMarsch(int playerID, SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGameWon(SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isJungFrau(int playerID, SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
