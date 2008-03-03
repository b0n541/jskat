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
 * Interface for all skat rules objects
 * 
 */
public interface SkatRules {

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
	 *            First card in the trick
	 * @param trump 
	 * 			  Trump suit in suit games, NULL otherwise
	 * @return TRUE if the card beats the other card
	 */
	public boolean isCardBeatsCard(Card card, Card cardToBeat, Card initialCard, SkatConstants.Suits trump);

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
	 * Checks whether a game was a schneider game<br>
	 * schneider means one party made only 30 points or below
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schneider game
	 */
	public boolean isSchneider(SkatGameData gameData);

	/**
	 * Checks whether a game was a schwarz game<br>
	 * schwarz means one party made no trick
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was a schwarz game
	 */
	public boolean isSchwarz(SkatGameData gameData);

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
	public boolean isDurchMarsch(int playerID, SkatGameData gameData);

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
	public boolean isJungFrau(int playerID, SkatGameData gameData);

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
	
	/**
	 * Checks whether a card is a trump card
	 * 
	 * @param card
	 * 			Card to be checked
	 * @param trump
	 * 			Trump suit in suit games, NULL otherwise
	 * @return TRUE if the card is a trump card
	 */
	public boolean isTrump(Card card, SkatConstants.Suits trump);
}
