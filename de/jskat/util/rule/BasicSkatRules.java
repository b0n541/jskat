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
import de.jskat.util.Suit;

/**
 * Interface for all skat rules objects
 */
public interface BasicSkatRules {

	/**
	 * Checks, whether the given card is allowed to be played, also considering
	 * the rest of the hand
	 * 
	 * @param gameType
	 * 			  Game type
	 * @param initialCard
	 *            First card in the trick
	 * @param hand
	 *            All cards on the hand
	 * @param card
	 *            Card to be checked
	 * @return TRUE if the card is allowed to be played
	 */
	public boolean isCardAllowed(GameType gameType, Card initialCard, CardList hand, Card card);

	/**
	 * Checks whether a card beats another card
	 * 
	 * @param gameType
	 * 			  Game type
	 * @param cardToBeat
	 *            Card to be beaten
	 * @param card
	 *            Card to be checked
	 * @return TRUE if the card beats the other card
	 */
	public boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card);

	/**
	 * Checks whether a game is won
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was won
	 */
	public boolean calcGameWon(SkatGameData gameData);

	/**
	 * Computes the value for a game
	 * 
	 * @param gameData
	 *            Game data
	 * @return Game result
	 */
	public int calcGameResult(SkatGameData gameData);

	/**
	 * Checks whether one or more cards of a given suit are on the hand
	 * 
	 * @param gameType
	 * 			  Game type
	 * @param hand
	 *            Cards on the players hand
	 * @param suit
	 *            Suit color to search for
	 * @return TRUE if one or more cards are on the hand
	 */
	public boolean hasSuit(GameType gameType, CardList hand, Suit suit);
}
