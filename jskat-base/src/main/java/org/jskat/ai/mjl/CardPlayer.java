
package org.jskat.ai.mjl;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

/**
 * @author Markus J. Luzius (markus@luzius.de)
 * 
 */
interface CardPlayer {

	/**
	 * Gets the next card, that the player wants to play
	 * 
	 * @param knowledge
	 *            all necessary information about the game
	 * @return the card to play
	 */
	Card playNextCard(ImmutablePlayerKnowledge knowledge);

	void startGame(ImmutablePlayerKnowledge knowledge);

}
