/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.rule.SkatRule;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
public class RamschPlayer extends AbstractCardPlayer {

	/** log */
	private Log log = LogFactory.getLog(RamschPlayer.class);
	private CardList cards;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            playerID
	 */
	public RamschPlayer(CardList cards, int id, SkatRule rules) {
		super(cards);
		log.debug("Constructing new single player.");
		this.playerID = id;
		this.rules = rules;
	}

	/**
	 * Gets the next card, that the player wants to play
	 * 
	 * @param knowledge
	 *            all necessary information about the game
	 * @return index of the card to play
	 */
	public Card playNextCard(PlayerKnowledge knowledge) {
		log.debug(".playNextCard(): Processing hand: " + cards);
		log.debug(".playNextCard(): Not really implemented yet...");
		int result = 0;
		if (knowledge.getTrickCards().size() == 0) {
			result = playInitialCard(cards);
			log.info(".playNextCard(): playing " + (cards.get(result)));
			return cards.remove(result);
		} else if (knowledge.getTrickCards().size() == 1) {
			result = playOtherCard(cards, knowledge.getTrickCards().get(0));
		} else {
			// if(trick.getCard(0).beats(trick.getCard(1), GameType.RAMSCH,
			// null, trick.getCard(1))) {
			// result = playOtherCard(cards, trick.getCard(0));
			// }
			// else {
			// if(trick.getCard(1).getRank() == Rank.JACK) {
			// if(trick.getCard(0).getRank() == Rank.JACK) {
			// result = playOtherCard(cards, trick.getCard(1));
			// }
			// else {
			// result = playOtherCard(cards, trick.getCard(0));
			// }
			// }
			// else {
			// result = playOtherCard(cards, trick.getCard(1));
			// }
			// }
		}

		// make sure that the card is allowed
		// if(!rules.isCardAllowed(cards.get(result), cards, trick.getCard(0),
		// null)) {
		// // if it's not allowed, take another one
		// for(int i=0;i<cards.size();i++) {
		// if(rules.isCardAllowed(cards.get(i), cards, trick.getCard(0), null))
		// {
		// result = i;
		// }
		// }
		// }
		log.info(".playNextCard(): playing " + (cards.get(result)));
		return cards.remove(result);
	}

	private int playInitialCard(CardList cards) {
		return cards.size() - 1;
	}

	private int playOtherCard(CardList cards, Card cardToMatch) {
		return cards.size() - 1;
	}

	/**
	 * Gets the player ID
	 * 
	 * @return player id
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Sets the player ID
	 * 
	 * @param i
	 */
	public void setPlayerID(int i) {
		playerID = i;
	}

	/** player id */
	private int playerID = -1;
	private SkatRule rules;

}
