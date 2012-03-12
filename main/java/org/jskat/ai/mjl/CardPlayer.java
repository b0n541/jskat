package org.jskat.ai.mjl;

import org.jskat.ai.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * @author Markus J. Luzius <markus@luzius.de>
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
	Card playNextCard(PlayerKnowledge knowledge);

	void startGame(PlayerKnowledge knowledge);

}
