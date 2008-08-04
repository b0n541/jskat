/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jskat.share.CardVector;
import jskat.share.rules.SkatRules;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class SinglePlayer implements CardPlayer {

	/** log */
	private Log log = LogFactory.getLog(SinglePlayer.class);
	
	/** Constructor
	 * @param id playerID
	 */
	public SinglePlayer(int id, SkatRules rules) {
		log.debug("Constructing new single player.");
		this.playerID = id;
		this.rules = rules;
	}

	/** Gets the next card, that the player wants to play
	 * @see jskat.player.AIPlayerMJL.CardPlayer#playNextCard(jskat.share.CardVector, jskat.player.AIPlayerMJL.TrickInfo)
	 * @param cards hand of the player
	 * @param trick all necessary information about the trick
	 * @return index of the card to play
	 */
	public int playNextCard(CardVector cards, TrickInfo trick) {
		log.debug(".playNextCard(): Processing hand: "+cards);
		log.debug(".playNextCard(): Not really implemented yet...");
		if(trick.size()==0) return 0;
		int result = 0;
		for(int i=0;i<cards.size();i++) {
			if(rules.isCardAllowed(cards.getCard(i), cards, trick.getCard(0), trick.getTrump())) {
				result = i;
				break;
			}
		}
		return result;
	}

	/** Gets the player ID
	 * @return player id
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Sets the player ID
	 * @param i
	 */
	public void setPlayerID(int i) {
		playerID= i;
	}

	/** player id */
	private int playerID = -1;
	
	private SkatRules rules;
}
