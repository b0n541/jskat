/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

import java.util.Vector;

import org.apache.log4j.Logger;

import jskat.share.CardVector;
import jskat.share.Card;
import jskat.share.SkatConstants;
import jskat.share.Tools;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class RamschSkatProcessor {
    
    private static final Logger log = Logger.getLogger(RamschSkatProcessor.class);

    private void testProcessor(CardVector cards, CardVector skat) {
    	int[] cardBin = new int[4];
        cardBin[3] = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.DIAMONDS);
        cardBin[2] = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.HEARTS);
        cardBin[1] = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.SPADES);
        cardBin[0] = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.CLUBS);

        Vector<Double> relevance = new Vector<Double>();
        cards.add(skat.remove(0));
        cards.add(skat.remove(0));
        for (int i=0;i<cards.size();i++) {
        	Card c = cards.getCard(i);
        	double rel = 1.0 * (double) c.getValue() / 6.0;
        	if(c.getValue()==SkatConstants.JACK) {
        		rel = 0.0;
        	}
        	log.debug("Card("+i+"): "+c+", Rel="+rel);
        	int tmpBin = cardBin[c.getSuit()];
        	log.debug("suit="+bin(tmpBin, 8)+" & 15 = "+(tmpBin & 15));
        	if(Tools.isIn(tmpBin & 15, new int[] {7, 11, 13})) {
        		rel = 0.0;
        	}
        	else if(Tools.isIn(tmpBin & 15, new int[] {3, 5})) {
        		rel = rel * 0.1;
        	}
        	else if(Tools.isIn(tmpBin & 15, new int[] {1, 2, 6, 9})) {
        		rel = rel * 0.4;
        	}
        	else if(Tools.isIn(tmpBin & 15, new int[] {4, 10})) {
        		rel = rel * 0.5;
        	}
        	log.debug("Card("+i+"): "+c+", Rel="+rel);
        	relevance.add(new Double(rel));
        }
        log.debug("cards    ="+cards);
        log.debug("relevance="+relevance);
        
        int maxIndex = findMax(relevance);
        log.debug("Max: "+maxIndex);
        skat.add(cards.remove(maxIndex));
        relevance.remove(maxIndex);
        maxIndex = findMax(relevance);
        log.debug("Max: "+maxIndex);
        skat.add(cards.remove(maxIndex));
        relevance.remove(maxIndex);
        log.debug("New skat: "+skat);
    }
    
    /**
     * Processes the skat for a Ramsch game
     * @param cards the player's hand
     * @param skat the skat
     */
    public void processSkat(CardVector cards, CardVector skat) {
    	log.debug("\n\n================================================================");
    	testProcessor(cards, skat);
    	log.debug("\n================================================================\n\n");
    	// TODO (mjl) check for potential Durchmarsch when processing the skat
        log.debug("My cards:"+cards+", Skat="+skat);
        int cDiamonds = cards.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.DIAMONDS);
        int cHearts = cards.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.HEARTS);
        int cSpades = cards.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.SPADES);
        int cClubs = cards.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.CLUBS);
        
        cDiamonds += skat.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.DIAMONDS);
        cHearts   += skat.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.HEARTS);
        cSpades   += skat.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.SPADES);
        cClubs    += skat.getSuitColorCount(SkatConstants.RAMSCH, SkatConstants.CLUBS);
        
        log.debug("#: C="+cClubs+", S="+cSpades+", H="+cHearts+", D="+cDiamonds);
        
        int diamonds = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.DIAMONDS);
        int hearts   = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.HEARTS);
        int spades   = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.SPADES);
        int clubs    = Helper.suitCardsToBinaryWithSkat(cards, skat, SkatConstants.CLUBS);
        
        log.debug("cards: C="+bin(clubs,8)+", S="+bin(spades,8)+", H="+bin(hearts,8)+", D="+bin(diamonds,8));
        
        int saveSuits = 0;
        if(Tools.isIn((diamonds & 31), new int[] {7,11,13,15,19,21})) saveSuits += 1;
        if(Tools.isIn((hearts & 31),   new int[] {7,11,13,15,19,21})) saveSuits += 2;
        if(Tools.isIn((spades & 31),   new int[] {7,11,13,15,19,21})) saveSuits += 4;
        if(Tools.isIn((clubs & 31),    new int[] {7,11,13,15,19,21})) saveSuits += 8;
        log.debug("saveSuits="+bin(saveSuits,4));

        int possibleSkatSuits = 0;
        if (cDiamonds > 0 && cDiamonds < 3 && (diamonds & 64) == 0 && !Tools.isIn((diamonds & 7), new int[] {1,3,5})) possibleSkatSuits += 1;
        if (cHearts > 0   && cHearts < 3   && (hearts & 64)   == 0 && !Tools.isIn((hearts & 7),   new int[] {1,3,5})) possibleSkatSuits += 2;
        if (cSpades > 0   && cSpades < 3   && (spades & 64)   == 0 && !Tools.isIn((spades & 7),   new int[] {1,3,5})) possibleSkatSuits += 4;
        if (cClubs > 0    && cClubs < 3    && (clubs & 64)    == 0 && !Tools.isIn((clubs & 7),    new int[] {1,3,5})) possibleSkatSuits += 8;
        log.debug("possibleSkatSuits="+bin(possibleSkatSuits,4));
        
        Card skatOne = skat.remove(0);
        Card skatTwo = skat.remove(0);
        
        if (possibleSkatSuits > 0) {
            int skatSuit = Helper.binaryToSuit(possibleSkatSuits);
            if(skatSuit<0) {
                // if more than one possible color: do nothing for the time being
                // TODO (mjl): skat processing with more than one possible color
            	log.debug("More than one possible color found...");
            	if(skatOne.getValue()==SkatConstants.JACK) {
            		int index = 0;
            		while(cards.getCard(index).getValue()==SkatConstants.JACK) index++;
                    skat.add(cards.remove(index));
                    cards.add(skatOne);
            	} else {
                    skat.add(skatOne);
            	}
            	if(skatTwo.getValue()==SkatConstants.JACK) {
            		int index = 0;
            		while(cards.getCard(index).getValue()==SkatConstants.JACK) index++;
                    skat.add(cards.remove(index));
                    cards.add(skatTwo);
            	} else {
                    skat.add(skatTwo);
            	}
            }
            else {
                log.debug("Color for skat:"+skatSuit);
                if(skatOne.getSuit() == skatSuit || cards.getSuitColorCount(SkatConstants.RAMSCH, skatSuit)<1) {
                	if(skatOne.getValue()==SkatConstants.JACK) {
                		int index = 0;
                		while(cards.getCard(index).getValue()==SkatConstants.JACK) index++;
                        skat.add(cards.remove(index));
                        cards.add(skatOne);
                	} else {
                        skat.add(skatOne);
                	}
                } else {
                    skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
                    cards.add(skatOne);
                }
                if(skatTwo.getSuit()==skatSuit || cards.getSuitColorCount(SkatConstants.RAMSCH, skatSuit)<1) {
                	if(skatTwo.getValue()==SkatConstants.JACK) {
                		int index = 0;
                		while(cards.getCard(index).getValue()==SkatConstants.JACK) index++;
                        skat.add(cards.remove(index));
                        cards.add(skatTwo);
                	} else {
                        skat.add(skatTwo);
                	}
                } else {
                    skat.add(cards.remove(cards.getFirstIndexOfSuit(skatSuit)));
                    cards.add(skatTwo);
                }
            }
        } else {
            // if more than one possible color: do nothing for the time being
            // TODO: skat processing with no possible color from the first iteration
        	if(skatOne.getValue()==SkatConstants.JACK) {
        		int index = 0;
        		while(cards.getCard(index).getValue()==SkatConstants.JACK) index++;
                skat.add(cards.remove(index));
                cards.add(skatOne);
        	} else {
                skat.add(skatOne);
        	}
        	if(skatTwo.getValue()==SkatConstants.JACK) {
        		int index = 0;
        		while(cards.getCard(index).getValue()==SkatConstants.JACK) index++;
                skat.add(cards.remove(index));
                cards.add(skatTwo);
        	} else {
                skat.add(skatTwo);
        	}
        }
        
        log.debug("Done - my cards:"+cards+", Skat="+skat);
    }
    
    /**
     * Decides, whether the AI player should look at the skat or rather play hand
     * @param cards the player's hand
     * @return true, if the player should look at the skat
     */
    public boolean lookAtSkat(CardVector cards) {
        return true;
    }
    
	private int pow(int a, int b) {
		int res = 1;
		for(int i=0;i<b;i++) {
			res = res * a;
		}
		return res;
	}

	private String bin(int i, int bits) {
    	StringBuffer sb = new StringBuffer();
    	for(int j=bits-1;j>=0;j--) {
    		if((i & pow(2, j)) > 0) {
    			sb.append("X");
    		}
    		else {
    			sb.append("-");
    		}
    	}
    	return sb.toString();
    }
	
	private int findMax(Vector<Double> list) {
		int res = 0;
		int pos = 1;
		while(list.size() > pos) {
			if(list.get(pos).doubleValue() > list.get(res).doubleValue()) res = pos;
			pos++;
		}
		return res;
	}
}
