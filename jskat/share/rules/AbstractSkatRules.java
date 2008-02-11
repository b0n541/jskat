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
import jskat.share.Tools;

/**
 * Skat rules
 * 
 * @author Jan Sch&auml;fer
 */
public abstract class AbstractSkatRules {

	static Logger log = Logger.getLogger(jskat.share.rules.AbstractSkatRules.class);

	/** Computes the value for a game */
	public final static int getResult(SkatGameData gameData) {

		int result = 0;

		SkatConstants.GameTypes gameType = gameData.getGameType();
		
		if (gameType == SkatConstants.GameTypes.SUIT ||
				gameType == SkatConstants.GameTypes.GRAND ||
				gameType == SkatConstants.GameTypes.RAMSCHGRAND) {

			result = calculateSuitGrandGame(gameData);
		}
		else if (gameType == SkatConstants.GameTypes.NULL) {
			
			result = calculateNullGame(gameData);
		}
		else if (gameType == SkatConstants.GameTypes.RAMSCH) {
			
			result = calculateRamschGame(gameData);
		}

		if (gameData.isContra()) {

			result = result * 2;

			if (gameData.isRe()) {

				result = result * 2;

				if (gameData.isBock())

					result = result * 2;
			}
		}

		log.debug("Game was worth " + result + " points.");

		return result;
	}

	/**
	 * Computes the value for a suit or grand game
	 * 
	 * @param game
	 *            The game data for which the value should be calculated
	 */
	// TODO: multiplier should already be calculated at the beginning of the
	// game - skat & suit cards also need to be considered
	private final static int calculateSuitGrandGame(SkatGameData gameData) {

		int multiplier = 2; // it's the lowest multiplier, "with/without one
		// play two"

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
//		if (multiplier == 5 && gameData.getGameType() == SkatConstants.GameTypes.SUIT) {
//			SkatConstants.Ranks valCounter = SkatConstants.Ranks.ACE;
			// with something like this:
			// while(valCount>=0 && game.getCards().hasCard(game.getTrump(),
			// valCounter)) {
//			while (valCounter >= 0) {
//				// multiplier++;
//				valCounter--;
//			}
//		}

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

		SkatConstants.GameTypes gameType = gameData.getGameType();
		
		if (gameType == SkatConstants.GameTypes.SUIT) {

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
		}
		else if (gameType == SkatConstants.GameTypes.GRAND ||
					gameType == SkatConstants.GameTypes.RAMSCHGRAND) {
			
			gameValue = SkatConstants.GRAND_VAL;
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
	 * Computes the value of a null game
	 * 
	 * @param game
	 *            The game data for which the value should be calculated
	 */
	private final static int calculateNullGame(SkatGameData gameData) {

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
	 * Computes the value of a ramsch game
	 * 
	 * @param game
	 *            The game data for which the value should be calculated
	 */
	private final static int calculateRamschGame(SkatGameData gameData) {

		int multiplier = 1;

		if (gameData.isJungFrau()) {
			multiplier = multiplier * 2;
		}

		multiplier = multiplier * (new Double(Math.pow(2, gameData.getGeschoben()))).intValue();

		if (gameData.isGameLost()) {
			multiplier = multiplier * -1;
		}

		return gameData.getScore(gameData.getSinglePlayer()) * multiplier;
	}

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
