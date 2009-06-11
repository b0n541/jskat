/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import de.jskat.util.Card;


/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
interface CardPlayer {

	/** Gets the next card, that the player wants to play
	 * @param trickInfo all necessary information about the trick
	 * @return the card to play
	 */
	Card playNextCard(TrickInfo trickInfo);

}
