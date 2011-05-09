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
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;


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
    public static Suit processSkat(CardList cards, CardList skat) {
        log.debug("My cards:"+cards+", Skat="+skat);
//        int cDiamonds = cards.getSuitCount(GameType.SUIT, Suit.DIAMONDS);
//        int cHearts = cards.getSuitCount(GameType.SUIT, Suit.HEARTS);
//        int cSpades = cards.getSuitCount(GameType.SUIT, Suit.SPADES);
//        int cClubs = cards.getSuitCount(GameType.SUIT, Suit.CLUBS);
//        
//        cDiamonds += skat.getSuitCount(GameType.SUIT, Suit.DIAMONDS);
//        cHearts   += skat.getSuitCount(GameType.SUIT, Suit.HEARTS);
//        cSpades   += skat.getSuitCount(GameType.SUIT, Suit.SPADES);
//        cClubs    += skat.getSuitCount(GameType.SUIT, Suit.CLUBS);
//        
//        log.debug("C="+cClubs+", S="+cSpades+", H="+cHearts+", D="+cDiamonds);
//        
//        int diamonds = Helper.suitCardsToBinaryWithSkat(cards, skat, Suit.DIAMONDS);
//        int hearts   = Helper.suitCardsToBinaryWithSkat(cards, skat, Suit.HEARTS);
//        int spades   = Helper.suitCardsToBinaryWithSkat(cards, skat, Suit.SPADES);
//        int clubs    = Helper.suitCardsToBinaryWithSkat(cards, skat, Suit.CLUBS);
//        
//        log.debug("C="+clubs+", S="+spades+", H="+hearts+", D="+diamonds);
//        
//        int possibleSkatSuits = 0;
//        if (cDiamonds > 0 && cDiamonds < 3 && (diamonds & 64) == 0) possibleSkatSuits += 1;
//        if (cHearts > 0   && cHearts < 3   && (hearts & 64)   == 0) possibleSkatSuits += 2;
//        if (cSpades > 0   && cSpades < 3   && (spades & 64)   == 0) possibleSkatSuits += 4;
//        if (cClubs > 0    && cClubs < 3    && (clubs & 64)    == 0) possibleSkatSuits += 8;
//        
//        Card skatOne = skat.remove(0);
//        Card skatTwo = skat.remove(0);
//        
//        if (possibleSkatSuits > 0) {
//            Suit skatSuit = Helper.binaryToSuit(possibleSkatSuits);
//            if(skatSuit != null) {
//                // if more than one possible color: do nothing for the time being
//                // TODO: skat processing with more than one possible color
//                skat.add(skatOne);
//                skat.add(skatTwo);
//            }
//            else {
//                log.debug("Color for skat:"+skatSuit);
//                if(skatOne.getSuit() == skatSuit || cards.getSuitCount(GameType.SUIT, skatSuit)<1) {
//                    skat.add(skatOne);
//                } else {
//                    skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
//                    cards.add(skatOne);
//                }
//                if(skatTwo.getSuit()==skatSuit || cards.getSuitCount(GameType.SUIT, skatSuit)<1) {
//                    skat.add(skatTwo);
//                } else {
//                    skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
//                    cards.add(skatTwo);
//                }
//            }
//        } else {
//            // if more than one possible color: do nothing for the time being
//            // TODO: skat processing with more than one possible color
//            skat.add(skatOne);
//            skat.add(skatTwo);
//        }
        log.debug("Done - my cards:"+cards+", Skat="+skat);
        return cards.getMostFrequentSuit();
    }
    
    /**
     * Decides, whether the AI player should look at the skat or rather play hand
     * @param cards the player's hand
     * @return true, if the player should look at the skat
     */
    public static boolean lookAtSkat(CardList cards) {
        return true;
    }
}
