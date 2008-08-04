/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jskat.share.CardVector;
import jskat.share.Card;
import jskat.share.SkatConstants;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class SkatProcessor {
    
	private static Log log = LogFactory.getLog(SkatProcessor.class);

    /**
     * Processes the skat after the player has won the bidding.
     * @param cards the player's hand
     * @param skat the skat
     * @return suit to be played (0-3), grand (4), null (-1)
     */
    public static SkatConstants.Suits processSkat(CardVector cards, CardVector skat) {
        log.debug("My cards:"+cards+", Skat="+skat);
        int cDiamonds = cards.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.DIAMONDS);
        int cHearts = cards.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.HEARTS);
        int cSpades = cards.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.SPADES);
        int cClubs = cards.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.CLUBS);
        
        cDiamonds += skat.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.DIAMONDS);
        cHearts   += skat.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.HEARTS);
        cSpades   += skat.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.SPADES);
        cClubs    += skat.getSuitCount(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.CLUBS);
        
        log.debug("C="+cClubs+", S="+cSpades+", H="+cHearts+", D="+cDiamonds);
        
        int diamonds = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.Suits.DIAMONDS);
        int hearts   = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.Suits.HEARTS);
        int spades   = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.Suits.SPADES);
        int clubs    = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.Suits.CLUBS);
        
        log.debug("C="+clubs+", S="+spades+", H="+hearts+", D="+diamonds);
        
        int possibleSkatSuits = 0;
        if (cDiamonds > 0 && cDiamonds < 3 && (diamonds & 64) == 0) possibleSkatSuits += 1;
        if (cHearts > 0   && cHearts < 3   && (hearts & 64)   == 0) possibleSkatSuits += 2;
        if (cSpades > 0   && cSpades < 3   && (spades & 64)   == 0) possibleSkatSuits += 4;
        if (cClubs > 0    && cClubs < 3    && (clubs & 64)    == 0) possibleSkatSuits += 8;
        
        Card skatOne = skat.remove(0);
        Card skatTwo = skat.remove(0);
        
        if (possibleSkatSuits > 0) {
            SkatConstants.Suits skatSuit = Helper.binaryToSuit(possibleSkatSuits);
            if(skatSuit != null) {
                // if more than one possible color: do nothing for the time being
                // TODO: skat processing with more than one possible color
                skat.add(skatOne);
                skat.add(skatTwo);
            }
            else {
                log.debug("Color for skat:"+skatSuit);
                if(skatOne.getSuit() == skatSuit || cards.getSuitCount(SkatConstants.GameTypes.SUIT, skatSuit)<1) {
                    skat.add(skatOne);
                } else {
                    skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
                    cards.add(skatOne);
                }
                if(skatTwo.getSuit()==skatSuit || cards.getSuitCount(SkatConstants.GameTypes.SUIT, skatSuit)<1) {
                    skat.add(skatTwo);
                } else {
                    skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
                    cards.add(skatTwo);
                }
            }
        } else {
            // if more than one possible color: do nothing for the time being
            // TODO: skat processing with more than one possible color
            skat.add(skatOne);
            skat.add(skatTwo);
        }
        log.debug("Done - my cards:"+cards+", Skat="+skat);
        return cards.getMostFrequentSuit();
    }
    
    /**
     * Decides, whether the AI player should look at the skat or rather play hand
     * @param cards the player's hand
     * @return true, if the player should look at the skat
     */
    public static boolean lookAtSkat(CardVector cards) {
        return true;
    }
}
