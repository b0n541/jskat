/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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

/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package org.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.rule.BasicSkatRules;


/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class SinglePlayer extends AbstractCardPlayer {

	/** log */
	private Log log = LogFactory.getLog(SinglePlayer.class);
	private BasicSkatRules rules;

	/** Constructor
	 * @param id playerID
	 */
	public SinglePlayer(CardList cards, BasicSkatRules rules) {
		super(cards);
		log.debug("Constructing new single player.");
		this.rules = rules;
	}

	/** Gets the next card, that the player wants to play
	 * @see org.jskat.ai.mjl.CardPlayer#playNextCard(jskat.share.CardList, org.jskat.ai.mjl.TrickInfo)
	 * @param cards hand of the player
	 * @param trick all necessary information about the trick
	 * @return index of the card to play
	 */
	public Card playNextCard(PlayerKnowledge knowledge) {
		log.debug(".playNextCard(): Processing hand: "+cards);
		log.debug(".playNextCard(): Not really implemented yet...");
		int result = -1;
		// TODO implementation of single player strategies...
		if(result<0) return null;
		return cards.remove(result);
	}

}
