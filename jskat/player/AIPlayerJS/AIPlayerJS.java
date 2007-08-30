/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerJS;

import org.apache.log4j.Logger;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.JSkatPlayerStates;
import jskat.share.SkatConstants;
import jskat.data.GameAnnouncement;
import jskat.player.JSkatPlayerImpl;

/**
 * The JSkat Player of Jan Sch&auml;fer
 * 
 * @author Jan Sch&auml;fer
 */
public class AIPlayerJS extends JSkatPlayerImpl {

    static Logger log = Logger.getLogger(jskat.player.AIPlayerJS.AIPlayerJS.class);
    
    /**
     * Creates a new instance of SkatPlayer
     * 
     * @param playerID The ID the player has in the game
     */
    public AIPlayerJS(int playerID) {
        
        super();
        setPlayerID(playerID);
        setPlayerName("Nobody");
        
        log.debug("AIPlayerJS is ready.");
    }
    
    /** 
     * Creates a new instance of SkatPlayer
     */
    public AIPlayerJS() {
        
        super();
        log.debug("AIPlayerJS is ready.");
    }
    
    /**
     * Creates a new instance of SkatPlayer
     * 
     * @param playerID The player ID
     * @param playerName The player name
     */
    public AIPlayerJS(int playerID, String playerName) {
        
        super();
        setPlayerID(playerID);
        setPlayerName(playerName);
    }

    /**
    * Implementation of JSkatPlayer interface
    */
    public void setUpBidding(int initialForehandPlayer) {
        
        setState(JSkatPlayerStates.PLAYER_BIDDING);
    }
    
    /**
     * Implementation of JSkatPlayer interface
     */
	public void takeRamschSkat(CardVector skat, boolean jacksAllowed) {
        
    }
    
    /**
     * Implementation of JSkatPlayer interface
     */
    public boolean lookIntoSkat(boolean isRamsch) {
    
    		// TODO don't look always into skat
    		// Try to play hand games
    		return true;
    }

    /**
     * Sets the new player state
     * 
     * @param newState The new state of the player
     */
    protected void setState(int newState) {
        
        super.setState(newState);
        
        if (playerState == JSkatPlayerStates.PLAYER_BIDDING) {
        	
            highestBid = calculateHighestBid();
        }
    }

    /**
     * Announces a new skat game
     * 
     * @return SkatGame The new SkatGame
     */
    public GameAnnouncement announceGame() {
        
        GameAnnouncement newGame = new GameAnnouncement();
        
        newGame.setGameType(SkatConstants.SUIT);
        newGame.setTrump(cards.getMostFrequentSuitColor());
        
        return newGame;
    }
    
    /**
     * Calculates the highest bid that can be made with the cards
     * 
     * @return Highest bid value
     */
    private int calculateHighestBid() {
        
        highestBid = 0;
        int mostFrequentSuitColor = 0;
        int mostFrequentSuitColorValue = 0;
        int maxMultiplier = 2;
        int multiplier = 0;
        int numberOfJacks = 0;
        
        if (cards.contains(SkatConstants.CLUBS, SkatConstants.JACK)) {
            
            numberOfJacks++;
            
            // game was played with jacks
            if (cards.contains(SkatConstants.SPADES, SkatConstants.JACK)) {
            
                maxMultiplier++;
                numberOfJacks++;
                
                if (cards.contains(SkatConstants.HEARTS, SkatConstants.JACK)) {
                
                    maxMultiplier++;
                    numberOfJacks++;
                    
                    if (cards.contains(SkatConstants.DIAMONDS, SkatConstants.JACK)) {
                    
                        maxMultiplier++;
                        numberOfJacks++;
                    }
                }
            }
        }
        else {
            
            // game was played without jacks
            if (!cards.contains(SkatConstants.SPADES, SkatConstants.JACK)) {
                maxMultiplier++;
                if (!cards.contains(SkatConstants.HEARTS, SkatConstants.JACK)) {
                    maxMultiplier++;
                    if (!cards.contains(SkatConstants.DIAMONDS, SkatConstants.JACK)) {
                        maxMultiplier++;
                    }
                    else {
                    	
                    	numberOfJacks++;
                    }
                }
                else {
                	
                	numberOfJacks++;
                }
            }
            else {
            	
            	numberOfJacks++;
            }
        }
        
        mostFrequentSuitColor = cards.getMostFrequentSuitColor();
        
        if (mostFrequentSuitColor == SkatConstants.CLUBS) {
            
            mostFrequentSuitColorValue = SkatConstants.CLUBS_VAL;
        }
        else if (mostFrequentSuitColor == SkatConstants.SPADES) {
            
            mostFrequentSuitColorValue = SkatConstants.SPADES_VAL;
        }
        else if (mostFrequentSuitColor == SkatConstants.HEARTS) {
            
            mostFrequentSuitColorValue = SkatConstants.HEARTS_VAL;
        }
        else if (mostFrequentSuitColor == SkatConstants.DIAMONDS) {
            
            mostFrequentSuitColorValue = SkatConstants.DIAMONDS_VAL;
        }
        
        int trumpCardCount = cards.getSuitColorCount(SkatConstants.SUIT, mostFrequentSuitColor);
        
        multiplier = maxMultiplier;
        
        if (trumpCardCount == 5 || trumpCardCount == 4)
            multiplier--;
        else if (trumpCardCount == 3)
            multiplier = multiplier - 2;
        else
            multiplier = 0;
        
        if ((numberOfJacks == 0 || numberOfJacks == 1) && trumpCardCount > 5)
            multiplier = 0;
        else if (numberOfJacks == 2 || numberOfJacks == 3)
            multiplier++;
        else if (numberOfJacks == 4)
            multiplier = multiplier + 2;

        if (multiplier > maxMultiplier)
            multiplier = maxMultiplier;
        
        log.debug("multiplier: " + multiplier + " trumpCardCount: " + trumpCardCount + " mostFrequentColor: " + mostFrequentSuitColor);
        
        highestBid = mostFrequentSuitColorValue * multiplier;
        
        log.debug("I will bid until " + highestBid);

        return highestBid;
    }
    
    /**
     * Asks the player whether it wants to bid more or not
     * 
     * @param currBidValue The current bid value
     * @return boolean TRUE if it wants to bid more
     */
    public boolean bidMore(int currBidValue) {
        
        if (highestBid > currBidValue)
            return true;
        else
            return false;
    }
    
    /**
     * Gets the next card from the player
     * 
     * @param trick The current trick
     * @return int The index of the card to be played
     */
    public Card playCard(CardVector trick) {
        
        int index = -1;
        
        // TODO This is a very stupid AIPlayer
        // no real strategy
        // just to go with the rules of the game
        if (trick.size() > 0) {
            
            // At least one card is played in this trick
            // the player is forced to play after the color
            // of the first card
            
            // At first: Is trump played?
            if (trick.getCard(0).getSuit() == currTrump ||
            trick.getCard(0).getValue() == SkatConstants.JACK) {
                
                // Trump is played
                // Does the player has trump?
                if (cards.hasTrump(currGameType, currTrump)) {
                    
                    // Play the highest trump
                    // Check whether there is a Jack in the cards or not
                    if (cards.contains(SkatConstants.CLUBS, SkatConstants.JACK)) {
                        
                    	index = cards.getIndexOf(SkatConstants.CLUBS, SkatConstants.JACK);
                        
                    } else if (cards.contains(SkatConstants.SPADES, SkatConstants.JACK)) {
                        
                    	index = cards.getIndexOf(SkatConstants.SPADES, SkatConstants.JACK);
                        
                    } else if (cards.contains(SkatConstants.HEARTS, SkatConstants.JACK)) {
                        
                    	index = cards.getIndexOf(SkatConstants.HEARTS, SkatConstants.JACK);
                        
                    } else if (cards.contains(SkatConstants.DIAMONDS, SkatConstants.JACK)) {
                        
                    	index = cards.getIndexOf(SkatConstants.DIAMONDS, SkatConstants.JACK);
                        
                    } else {
                        
                        // No Jack in the cards
                    	index = cards.getLastIndexOfSuit(SkatConstants.SUIT, currTrump);
                    }
                    
                } else {
                    
                    // Player doesn't have trump
                    // it doesn't matter what card is played
                    // just play the first card in the CardVector
                	index = (int)(Math.random() * (cards.size() - 1));
                }
                
            } else {
                
                // If trump is not played the player is forced
                // to play the color of the first card
                if (cards.hasSuit(currGameType, trick.getCard(0).getSuit())) {
                    
                    // Player has the color
                    // Play the card with the highest value
                	index = cards.getLastIndexOfSuit(SkatConstants.SUIT, trick.getCard(0).getSuit());
                    
                } else {
                    
                    // Player doesn't have the color
                    // is there any trump in the cards
                    if (cards.hasTrump(currGameType, currTrump)) {
                        
                        // Play the highest trump
                        // Check whether there is a Jack in the cards or not
                        if (cards.contains(SkatConstants.CLUBS, SkatConstants.JACK)) {
                            
                        	index = cards.getIndexOf(SkatConstants.CLUBS, SkatConstants.JACK);
                            
                        } else if (cards.contains(SkatConstants.SPADES, SkatConstants.JACK)) {
                            
                        	index = cards.getIndexOf(SkatConstants.SPADES, SkatConstants.JACK);
                            
                        } else if (cards.contains(SkatConstants.HEARTS, SkatConstants.JACK)) {
                            
                        	index = cards.getIndexOf(SkatConstants.HEARTS, SkatConstants.JACK);
                            
                        } else if (cards.contains(SkatConstants.DIAMONDS, SkatConstants.JACK)) {
                            
                        	index = cards.getIndexOf(SkatConstants.DIAMONDS, SkatConstants.JACK);
                            
                        } else {
                            
                            // No Jack in the cards
                        	index = cards.getLastIndexOfSuit(SkatConstants.SUIT, currTrump);
                        }
                        
                    } else {
                        
                        // it doesn't matter what card is played
                    	index = (int)(Math.random() * (cards.size() - 1));
                    }
                }
            }
            
        } else {
            
            // it doesn't matter what card is played
            // just play the first card in the CardVector
        	index = 0;
        }
        
        log.debug("AIPlayer " + playerID + ": " + cards.getCard(index));
        
        return cards.remove(index);
    }
    
    private int highestBid;

	public void showTrick(CardVector trick, int trickWinner) {
		// TODO Auto-generated method stub
		
	}

	public boolean isAIPlayer() {

		return true;
	}

	public boolean isHumanPlayer() {

		return false;
	}
}