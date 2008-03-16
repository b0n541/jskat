/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import java.util.Observable;

import jskat.share.SkatConstants;
import jskat.share.SkatConstants.Player;

/** 
 * Holds all bidding data during the bidding
 */
public class BidStatus extends Observable {
    
    /** 
     * Creates a new instance of BidStatus 
     */
    public BidStatus() {
    }
    
    /** 
     * Resets the BidStatus to default values at the beginning of the bidding
     * 
     * @param foreHandPlayer The ID of the current fore hand player
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
    
    /** 
     * Gets the current fore hand player
     * 
     * @return ID of the current fore hand player
     */
    public int getForeHandPlayer() {
        
        return foreHandPlayer;
    }
    
    /** 
     * Enable/Disable the Bidding buttons
     * 
     * @param enabled TRUE if the buttons should be enabled otherwise FALSE
     */
    public void setButtonsEnabled(boolean enabled) {
        
        buttonsEnabled = enabled;
        
        setChanged();
        notifyObservers();
    }
    
    /** 
     * Gets the information whether the buttons are enabled or not
     * 
     * @return TRUE if the Bidding buttons are enabled otherwise FALSE
     */
    public boolean getButtonsEnabled() {
        
        return buttonsEnabled;
    }
    
    /** 
     * Sets the status whether the player does the bid or not
     * 
     * @param hand Which hand should be set
     * @param handDoesBid TRUE if hand does the bid otherwise FALSE
     */
    public void setHandDoesBid(Player hand, boolean handDoesBid) {
        
		if (hand == Player.FORE_HAND) {
		        
	       foreHandDoesBid = handDoesBid;
		}
		else if (hand == Player.MIDDLE_HAND) {
			
			middleHandDoesBid = handDoesBid;
		}
		else if (hand == Player.BACK_HAND) {
			
			hindHandDoesBid = handDoesBid;
		}
        
        foreHandWasAsked = false;
        middleHandWasAsked = false;
        hindHandWasAsked = false;
        
        setChanged();
        notifyObservers(hand);
    }
    
    /** 
     * Gets the status whether the hand does the bid or not
     * 
     * @param hand Hand that should be asked
     * @return TRUE if the hand does the bid otherwise FALSE
     */
    public boolean getHandDoesBid(Player hand) {
        
        boolean returnValue = false;
        
        if (hand == Player.FORE_HAND) {
        	
        	returnValue = foreHandDoesBid;
        }
        else if (hand == Player.MIDDLE_HAND) {
        	
        	returnValue = middleHandDoesBid;
        }
        else if (hand == Player.BACK_HAND) {
        	
        	returnValue = hindHandDoesBid;
        }
        
        return returnValue;
    }
    
    /** 
     * Sets current bid value for a hand
     * 
     * @param hand The hand for which the bid value should be set
     * @param newBidValue The new bid value that should be set
     */
    public void setBidValue(Player hand, int newBidValue) {
        
        if (hand == Player.FORE_HAND) {
        	
        	foreHandBidValue = newBidValue;
        }
        else if (hand == Player.MIDDLE_HAND) {
        	
        	middleHandBidValue = newBidValue;
        }
        else if (hand == Player.BACK_HAND) {
        	
        	hindHandBidValue = newBidValue;
        }
        
        setChanged();
        notifyObservers(hand);
    }
    
    /** 
     * Gets the current bid value for a hand
     * 
     * @param hand The hand from where the bid value should be retrieved.
     * @return The current bid value for this hand
     */
    public int getBidValue(Player hand) {
        
        int returnValue = 0;
        
        if (hand == Player.FORE_HAND) {
        	
        	returnValue = foreHandBidValue;
        }
        else if (hand == Player.MIDDLE_HAND) {
        	
        	returnValue = middleHandBidValue;
        }
        else if (hand == Player.BACK_HAND) {
        	
        	returnValue = hindHandBidValue;
        }
        
        return returnValue;
    }
    
    /**
     * Gets highest bid value
     * 
     * @return Highest bid value
     */
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
    
    /**
     * Gets next bid value
     * 
     * @return Next bid value
     */
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
    
    /**
     * Sets flag for player that was asked
     * 
     * @param hand Player that was asked
     */
    public void setWasAsked(Player hand) {
        
        if (hand == Player.FORE_HAND) {
        	
        	foreHandWasAsked = true;
        	middleHandWasAsked = false;
        	hindHandWasAsked = false;
        }
        else if (hand == Player.MIDDLE_HAND) {
        	
        	foreHandWasAsked = false;
        	middleHandWasAsked = true;
        	hindHandWasAsked = false;
        }
        else if (hand == Player.BACK_HAND) {
        	
        	foreHandWasAsked = false;
        	middleHandWasAsked = false;
        	hindHandWasAsked = true;
        }
        
        setChanged();
        notifyObservers(hand);
    }
    
    /**
     * Gets the flag for a player
     * 
     * @param hand Player
     * @return TRUE if the player was asked
     */
    public boolean getWasAsked(Player hand) {
        
        boolean returnValue = false;
        
        if (hand == Player.FORE_HAND) {
        	
        	returnValue = foreHandWasAsked;
        }
        else if (hand == Player.MIDDLE_HAND) {
        	
        	returnValue = middleHandWasAsked;
        }
        else if (hand == Player.BACK_HAND) {
        	
        	returnValue = hindHandWasAsked;
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
