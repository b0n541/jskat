/**
 * 
 */
package de.jskat.ai.mjl;

import org.apache.log4j.Logger;

import de.jskat.ai.PlayerKnowledge;
import de.jskat.util.CardList;

/**
 * @author Markus J. Luzius <br>
 * created: 24.01.2011 18:20:09
 *
 */
public abstract class AbstractCardPlayer implements CardPlayer {
	private static final Logger log = Logger.getLogger(AbstractCardPlayer.class);
	
	protected CardList cards = null;

	protected AbstractCardPlayer(CardList cards) {
		this.cards = cards;
	}
	
	public void startGame(PlayerKnowledge knowledge) {
		log.debug("Starting game...");
		cards.sort(knowledge.getGame().getGameType());
	}


}
