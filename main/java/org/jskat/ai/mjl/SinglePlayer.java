/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.0
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.mjl;

import org.jskat.player.PlayerKnowledge;
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
	public Card playNextCard(final PlayerKnowledge knowledge) {
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
