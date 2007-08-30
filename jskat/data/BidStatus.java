/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import java.util.Observable;

import jskat.share.SkatConstants;

/** Holds all bidding data during the bidding
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class BidStatus extends Observable {
    
    /** Creates a new instance of BidStatus */
    public BidStatus() {
    }
    
    /** Resets the BidStatus to default values at the beginning of the bidding
     * @param foreHandPlayer The ID of the current Forehand player
     */
    public void resetBidStatus(int foreHandPlayer) {
        
        this.foreHandPlayer = foreHandPlayer;
        buttonsEnabled = false;
        foreHandDoesBid = true;
        foreHandWasAsked = false;
        foreHandBidValue = 0;
        middleHandDoesBid = true;
        middleHandWasAsked = false;
        middleHandBidValue = 0;
        hindHandDoesBid = true;
        hindHandBidValue = 0;
        hindHandWasAsked = false;
        
        setChanged();
        notifyObservers(new Integer(foreHandPlayer));
    }
    
    /** Gets the current Forehand player
     * @return The ID of the current Forehand player
     */
    public int getForeHandPlayer() {
        
        return foreHandPlayer;
    }
    
    /** Enable/Disable the Bidding buttons
     * @param enabled TRUE if the buttons should be enabled otherwise FALSE
     */
    public void setButtonsEnabled(boolean enabled) {
        
        buttonsEnabled = enabled;
        
        setChanged();
        notifyObservers();
    }
    
    /** Gets the information whether the buttons are enabled or not
     * @return TRUE if the Bidding buttons are enabled otherwise FALSE
     */
    public boolean getButtonsEnabled() {
        
        return buttonsEnabled;
    }
    
    /** Sets the status whether the player does the bid or not
     * @param hand Which hand should be set (SkatConstants.FORE_HAND, SkatConstants.MIDDLE_HAND,
     * SkatConstants.BACK_HAND)
     * @param handDoesBid TRUE if hand does the bid otherwise FALSE
     */
    public void setHandDoesBid(int hand, boolean handDoesBid) {
        
        switch(hand) {
            
            case(SkatConstants.FORE_HAND):
                
                foreHandDoesBid = handDoesBid;
                break;
                
            case(SkatConstants.MIDDLE_HAND):
                
                middleHandDoesBid = handDoesBid;
                break;
                
            case(SkatConstants.BACK_HAND):
                
                hindHandDoesBid = handDoesBid;
                break;
        }
        
        foreHandWasAsked = false;
        middleHandWasAsked = false;
        hindHandWasAsked = false;
        
        setChanged();
        notifyObservers(new Integer(hand));
    }
    
    /** Gets the status whether the hand does the bid or not
     * @return TRUE if the hand does the bid otherwise FALSE
     * @param hand The player that should be asked (SkatConstants.FORE_HAND,
     * SkatConstants.MIDDLE_HAND, SkatConstants.HINDHAND)
     */
    public boolean getHandDoesBid(int hand) {
        
        boolean returnValue = false;
        
        switch(hand) {
            
            case(SkatConstants.FORE_HAND):
                returnValue = foreHandDoesBid;
                break;
                
            case(SkatConstants.MIDDLE_HAND):
                returnValue = middleHandDoesBid;
                break;
                
            case(SkatConstants.BACK_HAND):
                returnValue = hindHandDoesBid;
                break;
                
            default:
            	break;
        }
        
        return returnValue;
    }
    
    /** Sets current bid value for a hand
     * @param hand The hand for which the bid value should be set
     * @param newBidValue The new bid value that should be set
     */
    public void setBidValue(int hand, int newBidValue) {
        
        switch(hand) {
            
            case(SkatConstants.FORE_HAND):
                
                foreHandBidValue = newBidValue;
                
                break;
            case(SkatConstants.MIDDLE_HAND):
                
                middleHandBidValue = newBidValue;
                
                break;
            case(SkatConstants.BACK_HAND):
                
                hindHandBidValue = newBidValue;
                
                break;
        }
        
        setChanged();
        notifyObservers(new Integer(hand));
    }
    
    /** Gets the current bid value for a hand
     * @param hand The hand from where the bid value should be retrieved.
     * @return The current bid value for this hand
     */
    public int getBidValue(int hand) {
        
        int returnValue = 0;
        
        switch(hand) {
            
            case(SkatConstants.FORE_HAND):
                
                returnValue = foreHandBidValue;
                
                break;
            case(SkatConstants.MIDDLE_HAND):
                
                returnValue = middleHandBidValue;
                
                break;
            case(SkatConstants.BACK_HAND):
                
                returnValue = hindHandBidValue;
                
                break;
        }
        
        return returnValue;
    }
    
    public int getHighestBidValue() {
        
        int highestBidValue = foreHandBidValue;
        
        if (highestBidValue < middleHandBidValue) {
            
            highestBidValue = middleHandBidValue;
        }
        if (highestBidValue < hindHandBidValue) {
            
            highestBidValue = hindHandBidValue;
        }
        
        return highestBidValue;
    }
    
    public int getNextBidValue() {
        
        int highestBidValue = getHighestBidValue();
        
        int i = 0;
        while (SkatConstants.bidOrder[i] <= highestBidValue && i < SkatConstants.bidOrder.length) {
            
            i++;
        }
        
        if (SkatConstants.bidOrder[i] == highestBidValue) {
            
            return -1;
        }
        else {
            
            return SkatConstants.bidOrder[i];
        }
    }
    
    public void setWasAsked(int hand) {
        
        switch(hand) {
            
            case(SkatConstants.FORE_HAND):
                
                foreHandWasAsked = true;
                middleHandWasAsked = false;
                hindHandWasAsked = false;
                
                break;
            case(SkatConstants.MIDDLE_HAND):
                
                foreHandWasAsked = false;
                middleHandWasAsked = true;
                hindHandWasAsked = false;
                
                break;
            case(SkatConstants.BACK_HAND):
                
                foreHandWasAsked = false;
                middleHandWasAsked = false;
                hindHandWasAsked = true;
                
                break;
        }
        
        setChanged();
        notifyObservers(new Integer(hand));
    }
    
    public boolean getWasAsked(int hand) {
        
        boolean returnValue = false;
        
        switch(hand) {
            
            case(SkatConstants.FORE_HAND):
                
                returnValue = foreHandWasAsked;
                
                break;
            case(SkatConstants.MIDDLE_HAND):
                
                returnValue = middleHandWasAsked;
                
                break;
            case(SkatConstants.BACK_HAND):
                
                returnValue = hindHandWasAsked;
                
                break;
        }
        
        return returnValue;
    }
    
    private int foreHandPlayer = 0;
    private boolean buttonsEnabled = false;
    private boolean foreHandDoesBid = true;
    private boolean foreHandWasAsked = false;
    private int foreHandBidValue = 0;
    private boolean middleHandDoesBid = true;
    private boolean middleHandWasAsked = false;
    private int middleHandBidValue = 0;
    private boolean hindHandDoesBid = true;
    private int hindHandBidValue = 0;
    private boolean hindHandWasAsked = false;
}
