/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import de.jskat.util.CardList;


/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public interface CardPlayer {

	/** Gets the next card, that the player wants to play
	 * @param cards hand of the player
	 * @param trickInfo all necessary information about the trick
	 * @return index of the card to play
	 */
	public int playNextCard(CardList cards, TrickInfo trickInfo);

	/**
	 *
	 * @return player id
	 */
	public int getPlayerID();

	/**
	 *
	 * @param i
	 */
	public void setPlayerID(int i);
}
