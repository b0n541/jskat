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
 * Interface for all skat rules objects
 * 
 */
public interface SkatRules {

	static Logger log = Logger.getLogger(jskat.share.rules.SkatRules.class);

	/**
	 * Checks, whether the given card is allowed to be played, also considering
	 * the rest of the hand
	 * 
	 * @param card
	 *            Card to be checked
	 * @param hand
	 *            All cards on the hand
	 * @param initialCard
	 *            First card in the trick
	 * @param trump
	 *            Trump color
	 * @return TRUE if the card is allowed to be played
	 */
	public boolean isCardAllowed(Card card, CardVector hand, Card initialCard,
			SkatConstants.Suits trump);

	/**
	 * Checks whether a card beats another card
	 * 
	 * @param card
	 *            Card to be checked
	 * @param cardToBeat
	 *            Card to be beaten
	 * @param initialCard
	 *            First card of the trick
	 * @param trump
	 *            Trump suit in suit games, NULL otherwise
	 * @return TRUE if the card beats the other card
	 */
	public boolean isCardBeatsCard(Card card, Card cardToBeat,
			Card initialCard, SkatConstants.Suits trump);

	/**
	 * Checks whether a game is won
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was won
	 */
	public boolean isGameWon(SkatGameData gameData);

	/**
	 * Computes the value for a game
	 * 
	 * @param gameData
	 *            Game data
	 * @return Game result
	 */
	public int getGameResult(SkatGameData gameData);

	/**
	 * Checks whether one or more cards of a given suit are on the hand
	 * 
	 * @param hand
	 *            Cards on the players hand
	 * @param trump
	 *            Trump suit in suit games, NULL otherwise
	 * @param suit
	 *            Suit color to search for
	 * @return TRUE if one or more cards are on the hand
	 */
	public boolean hasSuit(CardVector hand, SkatConstants.Suits trump,
			SkatConstants.Suits suit);
}
