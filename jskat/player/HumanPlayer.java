/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.player;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants.Ranks;
import jskat.share.SkatConstants.Suits;
import jskat.data.GameAnnouncement;

/** 
 * Human Player
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class HumanPlayer extends AbstractJSkatPlayer {
    
    /** 
     * Creates a new instance of SkatPlayer
     *  
     * @param playerID Player ID
     */
    public HumanPlayer(int playerID) {
        
        super();
        setPlayerID(playerID);
        setPlayerName("Nobody");
    }
    
    /** 
     * Creates a new instance of SkatPlayer
     *  
     * @param playerID Player ID 
     * @param playerName Player name 
     */
    public HumanPlayer(int playerID, String playerName) {
        
        super();
        setPlayerID(playerID);
        setPlayerName(playerName);
    }
    
    /**
     * @see jskat.player.JSkatPlayer#bidMore(int)
     */
    public boolean bidMore(int currBidValue) {
        
        return false;
    }
    
    /**
     * @see jskat.player.JSkatPlayer#announceGame()
     */
    public GameAnnouncement announceGame() {
        
        return new GameAnnouncement();
    }
    
    /**
     * @see jskat.player.JSkatPlayer#takeRamschSkat(jskat.share.CardVector, boolean)
     */
    public void takeRamschSkat(CardVector skat, boolean jacksAllowed) {
        
    }
    
    /**
     * @see jskat.player.JSkatPlayer#lookIntoSkat(boolean)
     */
	public boolean lookIntoSkat(boolean isRamsch) {
        return true;
    }
    
    /**
     * @see jskat.player.JSkatPlayer#isHumanPlayer()
     */
    public final boolean isHumanPlayer() {
        return true;
    }
    
    /**
     * @see jskat.player.JSkatPlayer#isAIPlayer()
     */
    public final boolean isAIPlayer() {
        return false;
    }

	/**
	 * @see jskat.player.AbstractJSkatPlayer#showTrick(jskat.share.CardVector, int)
	 */
	public void showTrick(CardVector trick, int trickWinner) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see jskat.player.JSkatPlayer#playCard(jskat.share.CardVector)
	 */
	public Card playCard(CardVector trick) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#cardPlayed(jskat.share.Card)
	 */
	public void cardPlayed(Card card) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#discloseOuvertCards(jskat.share.CardVector)
	 */
	public void discloseOuvertCards(CardVector ouvertCards) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#setUpBidding(int)
	 */
	public void setUpBidding(int initialForehandPlayer) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.JSkatPlayer#removeCard(jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Ranks)
	 */
	public Card removeCard(Suits suit, Ranks rank) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#takeSkat(jskat.share.CardVector)
	 */
	public void takeSkat(CardVector skat) {
		// TODO Auto-generated method stub
		
	}
}