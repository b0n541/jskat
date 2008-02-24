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
 * Implementation of skat rules for Suit games
 *
 */
public class SkatRulesSuit extends SkatRulesSuitGrand implements SkatRules {

	static Logger log = Logger.getLogger(jskat.share.rules.SkatRulesSuit.class);

	/**
	 * @see jskat.share.rules.SkatRulesSuitGrand#getGameResult(jskat.data.SkatGameData)
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
			log.debug("game played with "+(multiplier-1)+" jack(s)");
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
			log.debug("game played without "+(multiplier-1)+" jack(s)");
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
		}
		else if (trump == SkatConstants.Suits.SPADES) {
			
			gameValue = SkatConstants.SPADES_VAL;
		}
		else if (trump == SkatConstants.Suits.HEARTS) {
			
			gameValue = SkatConstants.HEARTS_VAL;
		}
		else if (trump == SkatConstants.Suits.DIAMONDS) {
			
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
	 * @see jskat.share.rules.SkatRulesSuitGrand#isCardAllowed(jskat.share.Card, jskat.share.CardVector, jskat.share.Card, jskat.share.SkatConstants.Suits)
	 */
	@Override
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see jskat.share.rules.SkatRulesSuitGrand#isCardBeats(jskat.share.Card, jskat.share.Card, jskat.share.Card)
	 */
	@Override
	public boolean isCardBeats(Card card, Card cardToBeat, Card initialTrickCard) {
		// TODO Auto-generated method stub
		return false;
	}
}
