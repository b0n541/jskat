/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

import org.apache.log4j.Logger;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.SkatRules;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class RamschPlayer implements CardPlayer {

	/** log */
	private static final Logger log = Logger.getLogger(RamschPlayer.class);
	
	/** Constructor
	 * @param id playerID
	 */
	public RamschPlayer(int id) {
		log.debug("Constructing new single player.");
		this.playerID = id;
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
		int result = 0;
		if(trick.size() == 0) {
		    result = playInitialCard(cards);
		    log.info(".playNextCard(): playing "+(cards.getCard(result)));
			return result;
		}
		else if(trick.size() == 1) {
		    result = playOtherCard(cards, trick.getCard(0));
		}
		else {
		    if(trick.getCard(0).beats(trick.getCard(1), SkatConstants.RAMSCH, -1, trick.getCard(1).getSuit())) {
			    result = playOtherCard(cards, trick.getCard(0));
		    } 
		    else {
		        if(trick.getCard(1).getValue() == SkatConstants.JACK) {
		            if(trick.getCard(0).getValue() == SkatConstants.JACK) {
					    result = playOtherCard(cards, trick.getCard(1));
		            }
		            else {
					    result = playOtherCard(cards, trick.getCard(0));
		            }
		        }
		        else {
				    result = playOtherCard(cards, trick.getCard(1));
		        }
		    }
		}
		
		// make sure that the card is allowed
		if(!SkatRules.isCardAllowed(cards.getCard(result), cards, trick.getCard(0), trick.getGameType(), trick.getTrump())) {
			// if it's not allowed, take another one
		    for(int i=0;i<cards.size();i++) {
				if(SkatRules.isCardAllowed(cards.getCard(i), cards, trick.getCard(0), trick.getGameType(), trick.getTrump())) {
					result = i;
				}
			}
		}
	    log.info(".playNextCard(): playing "+(cards.getCard(result)));
		return result;
	}
	
	private int playInitialCard(CardVector cards) {
	    return cards.size()-1;
	}

	private int playOtherCard(CardVector cards, Card cardToMatch) {
	    return cards.size()-1;
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
}
