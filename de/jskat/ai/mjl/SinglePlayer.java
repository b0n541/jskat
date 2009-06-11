/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.rule.BasicSkatRules;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class SinglePlayer implements CardPlayer {

	/** log */
	private Log log = LogFactory.getLog(SinglePlayer.class);
	private BasicSkatRules rules;
	private CardList cards;

	/** Constructor
	 * @param id playerID
	 */
	public SinglePlayer(CardList cards, BasicSkatRules rules) {
		log.debug("Constructing new single player.");
		this.rules = rules;
		this.cards = cards;
	}

	/** Gets the next card, that the player wants to play
	 * @see de.jskat.ai.mjl.CardPlayer#playNextCard(jskat.share.CardList, de.jskat.ai.mjl.TrickInfo)
	 * @param cards hand of the player
	 * @param trick all necessary information about the trick
	 * @return index of the card to play
	 */
	public Card playNextCard(TrickInfo trick) {
		log.debug(".playNextCard(): Processing hand: "+cards);
		log.debug(".playNextCard(): Not really implemented yet...");
		int result = -1;
		// TODO implementation of single player strategies...
		if(result<0) return null;
		return cards.remove(result);
	}

}
