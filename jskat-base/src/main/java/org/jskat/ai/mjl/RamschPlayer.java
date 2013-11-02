/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.rule.SkatRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
public class RamschPlayer extends AbstractCardPlayer {

	/** log */
	private static Logger log = LoggerFactory.getLogger(RamschPlayer.class);
	private CardList cards;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            playerID
	 */
	public RamschPlayer(final CardList cards, final int id, final SkatRule rules) {
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
	@Override
	public Card playNextCard(final ImmutablePlayerKnowledge knowledge) {
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

	private int playInitialCard(final CardList cards) {
		return cards.size() - 1;
	}

	private int playOtherCard(final CardList cards, final Card cardToMatch) {
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
	public void setPlayerID(final int i) {
		playerID = i;
	}

	/** player id */
	private int playerID = -1;
	private final SkatRule rules;

}
