/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

import jskat.share.CardVector;

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
	public int playNextCard(CardVector cards, TrickInfo trickInfo);

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
