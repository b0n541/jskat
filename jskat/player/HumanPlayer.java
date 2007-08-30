/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.player;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.data.GameAnnouncement;

/** The JSkat Player
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class HumanPlayer extends JSkatPlayerImpl {
    
    /** Creates a new instance of SkatPlayer */
    public HumanPlayer(int playerID) {
        
        super();
        setPlayerID(playerID);
        setPlayerName("Nobody");
    }
    
    /** Creates a new instance of SkatPlayer */
    public HumanPlayer(int playerID, String playerName) {
        
        super();
        setPlayerID(playerID);
        setPlayerName(playerName);
    }
    
    public boolean bidMore(int currBidValue) {
        
        return false;
    }
    
    public GameAnnouncement announceGame() {
        
        return new GameAnnouncement();
    }
    
    /**
     * This method should wait for human player
     * 
     */
    public Card playCard(CardVector trick) {
        
        return new Card(-1, -1);
    }
    
    public void takeRamschSkat(CardVector skat, boolean jacksAllowed) {
        
    }
    
    public boolean lookIntoSkat(boolean isRamsch) {
        return true;
    }
    
    public final boolean isHumanPlayer() {
        return true;
    }
    
    public final boolean isAIPlayer() {
        return false;
    }

	public void showTrick(CardVector trick, int trickWinner) {
		// TODO Auto-generated method stub
		
	}
}