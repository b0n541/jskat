/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import de.jskat.data.SkatGameData;
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.Rank;

/**
 * Implementation of skat rules for Suit games
 */
public class SuitRules extends SuitGrandRules {

	/**
	 * @see SuitGrandRules#getBaseMultiplier(SkatGameData)
	 */
	@Override
	protected int getBaseMultiplier(SkatGameData gameData) {

		int multiplier = getJackMultiplier(gameData);
		
		if (multiplier == 4 && gameData.getClubJack()) {
			// consider all trump cards too
			CardList playerCards = gameData.getPlayerCards(gameData.getDeclarer());
			playerCards.addAll(gameData.getSkat());

			for (Card currTrumpCard : getTrumpCards(gameData.getGameType())) {
				
				if (playerCards.contains(currTrumpCard)) {
					
					multiplier++;
				}
				else {
					// trump is not continous anymore
					break;
				}
			}
		}
		
		return multiplier;
	}
	
	private CardList getTrumpCards(GameType gameType) {
		
		CardList trumpCards = new CardList();
		
		for (Rank currRank : Rank.values()) {
			
			trumpCards.add(Card.getCardFromString(gameType.getTrumpSuit().shortString().concat(currRank.shortString())));
		}
		trumpCards.sort(gameType);
		
		return trumpCards;
	}
}
