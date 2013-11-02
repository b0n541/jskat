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
public class SinglePlayer extends AbstractCardPlayer {

	/** log */
	private static Logger log = LoggerFactory.getLogger(SinglePlayer.class);
	private final SkatRule rules;

	/**
	 * Constructor
	 * 
	 * @param cards
	 * @param rules
	 */
	public SinglePlayer(final CardList cards, final SkatRule rules) {
		super(cards);
		log.debug("Constructing new single player.");
		this.rules = rules;
	}

	CardList discardSkat(final CardList skat) {
		// should be done: check which cards should best be discarded
		cards.remove(skat.get(0));
		cards.remove(skat.get(1));
		log.debug("no algorithm yet, discarding original skat of [" + skat + "], cards.size=" + cards.size());

		return skat;
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
		int result = -1;
		// TODO implementation of single player strategies...
		if (result < 0) {
			return null;
		}
		return cards.remove(result);
	}

}
