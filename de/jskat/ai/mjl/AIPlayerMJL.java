/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.AbstractJSkatPlayer;
import de.jskat.ai.JSkatPlayer;
import de.jskat.data.GameAnnouncement;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.Rank;
import de.jskat.util.SkatConstants;
import de.jskat.util.Suit;

/** A JSkat AI Player
 * @author Markus J. Luzius <markus@luzius.de>
 */
public class AIPlayerMJL extends AbstractJSkatPlayer implements JSkatPlayer {
    
    /** 
     * Creates a new instance of SkatPlayer (default constructor)
     */
    public AIPlayerMJL() {
        
        super();
        isSinglePlayer = false;
		log.debug("Player "+playerID+": Constructing AIPlayerMJL ("+playerName+")");
    }
    
    /**
     * Creates a new instance of SkatPlayer with a player ID and a name
     * @param playerID
     * @param playerName
     */
    public AIPlayerMJL(int playerID, String playerName) {
        
        super();
        playerID = (int) System.currentTimeMillis();
        setPlayerName(playerName);
        isSinglePlayer = false;
		log.debug("Player "+playerID+": Constructing AIPlayerMJL ("+playerName+")");
    }
    
    /** (non-Javadoc)
     * @see jskat.player.JSkatPlayer#takeRamschSkat(jskat.share.CardList, boolean)
     */
	public void takeRamschSkat(CardList skat, boolean jacksAllowed) {
		RamschSkatProcessor rsp = new RamschSkatProcessor();
    	rsp.processSkat(cards, skat);
    }
    
    /** (non-Javadoc)
     * @see jskat.player.JSkatPlayer#lookIntoSkat(boolean)
     */
    public boolean lookIntoSkat(boolean isRamsch) {
    	// TODO (mjl) implementation for single player (i.e. no Ramsch)
    	RamschSkatProcessor rsp = new RamschSkatProcessor();
    	return rsp.lookAtSkat(cards);
    }

    /** 
     * Asks the player for the game he wants to play
     * @see jskat.player.JSkatPlayer#announceGame()
     */
    public GameAnnouncement announceGame() {
    	
        isSinglePlayer = true;
    	GameAnnouncement newGame = new GameAnnouncement();
        
        switch(selectedTrump.getSuitOrder()) {
	        case 3:
	        	newGame.setGameType(GameType.CLUBS);
	        	break;
	        case 2:
	        	newGame.setGameType(GameType.SPADES);
	        	break;
	        case 1:
	        	newGame.setGameType(GameType.HEARTS);
	        	break;
	        case 0:
	        	newGame.setGameType(GameType.DIAMONDS);
	        	break;
	        default:
	        	newGame.setGameType(GameType.GRAND);
	        	break;
        }
        
        return newGame;
    }
    
	/** 
	 * Notifies the player of the start of bidding for a new game
	 * @see jskat.player.JSkatPlayer#setUpBidding(int)
     * @param initialForehandPlayer
	 */
	public void setUpBidding(int initialForehandPlayer) {
		this.initialForehandPlayer = initialForehandPlayer;
		if (this.initialForehandPlayer == playerID) 
			log.debug(".setUpBidding(): Player "+playerID+": Hey - I'm in forehand position! Cool....");
		else
			log.debug(".setUpBidding(): Player "+playerID+": I'm not in forehand position! What a shame....");
		myBid = null;
	}

    /** Asks the player whether he wants to bid higher or not
     * @see jskat.player.JSkatPlayer#bidMore(int)
     * @param currBidValue
     */
    public int bidMore(int currBidValue) {
		boolean iDoThisBid = true;
		
		// set the game state
		if(myBid==null) {
			this.setState(JSkatPlayer.PlayerStates.BIDDING);
		}
		
	    int maxBid = myBid.getMaxBid();
		if (maxBid < currBidValue)
			iDoThisBid = false;
        return currBidValue;
    }
    
	/** 
	 * Start the game: inform player of game type, trumpf and special options
	 * @see jskat.player.JSkatPlayer#startGame(int, int, SkatConstants.GameType, SkatConstants.Suit, boolean, boolean)
     * @param singlePlayer
     * @param forehandPlayer
     * @param gameType
     * @param trump
     * @param handGame
     * @param ouvertGame
	 */
//    public void startGame(int singlePlayer, int forehandPlayer, GameType gameType,
//			Suit trump, boolean handGame, boolean ouvertGame) {
//		super.startGame(singlePlayer, forehandPlayer, gameType, trump, handGame, ouvertGame);
//		singlePlayerPos = singlePlayer - forehandPlayer;
//		if(singlePlayerPos<0) singlePlayerPos +=3;
//		log.debug("SinglePlayer is in position "+singlePlayerPos+", when single player is "+singlePlayer+" and forehand is "+forehandPlayer+" (and I am player "+playerID+")");
//		if(singlePlayer==playerID) {
//	        isSinglePlayer = true;
//		}
//		else {
//	        isSinglePlayer = false;
//		}
//		
//		myBid = null;
//		myMemory = new CardMemory(gameType, trump);
//		game = new GameInfo(gameType, trump, singlePlayer);		
//		this.setState(JSkatPlayer.PlayerStates.PLAYING);
//	}


    /** Get next Card to play
     * @see jskat.player.JSkatPlayer#playNextCard(jskat.share.CardList)
     */
    public int playNextCard(CardList trick) {
		// set the current game state
		if(playerType==null) {
			this.setState(JSkatPlayer.PlayerStates.PLAYING);
		}
        TrickInfo thisTrick = new TrickInfo();
        thisTrick.setGameInfo(game);
        thisTrick.setTrick(trick);
		thisTrick.setSinglePlayerPos(singlePlayerPos);
//		thisTrick.setForehandPlayer(forehandPlayer);
        return playerType.playNextCard(cards, thisTrick);
    }
    
    /**
     * @see jskat.player.JSkatPlayer#playCard(jskat.share.CardList)
     */
    public Card playCard(CardList trick) {
    	return cards.remove(playNextCard(trick));
    }
	
	/** 
	 * Makes the current trick known to the players when it is complete
	 * @see jskat.player.JSkatPlayer#showTrick(jskat.share.CardList, int)
	 */
//	public void showTrick(CardList trick, int trickWinner) {
//		// remember the cards that have been played by each player
//		log.debug("Player "+playerID+" received the trick ["+trick+"], where the winner is player "+trickWinner);
//
//		if(myMemory != null)
//			myMemory.addTrick(trick, forehandPlayer);
//		else {
//			log.warn("CardMemory should exist, but is null!");
//			myMemory = new CardMemory();
//			myMemory.addTrick(trick, forehandPlayer);
//		}
//		// set the forehand player for the next trick
//		forehandPlayer = trickWinner;
//		singlePlayerPos = singlePlayer - forehandPlayer;
//		if(singlePlayerPos<0) singlePlayerPos +=3;
//		log.debug("SinglePlayer is now in position "+singlePlayerPos+", when single player is "+singlePlayer+" and forehand is "+forehandPlayer);
//		
//	}

	/** 
	 * Sets the state of the current game <br>
	 * If necessary, a new bid is initialized.
	 * @see de.jskat.ai.mjl.Bidding  
	 * @see jskat.player.AbstractJSkatPlayer#setState(JSkatPlayer.PlayerStates)
	 */
//	public final void setState(JSkatPlayer.PlayerStates newState) {
//        
//		log.debug("Player "+playerID+" ("+playerName+"): Setting new state:"+newState);
//		super.setState(newState);
//		if(newState == JSkatPlayer.PlayerStates.BIDDING) {
//			myBid = new Bidding(cards);
//			playerType = null;
//		} 
//		else if(newState == JSkatPlayer.PlayerStates.PLAYING) {
//			myBid = null;
//			if(game!=null && game.getGameType() == GameType.RAMSCH) {
//				log.debug("Player "+playerID+" ("+playerName+"): is a ramsch player");
//			    playerType = new RamschPlayer(playerID, rules);
//			}
//			else if(isSinglePlayer) {
//				log.debug("Player "+playerID+" ("+playerName+"): is the single player");
//				playerType = new SinglePlayer(playerID, rules);
//			}
//			else {
//				log.debug("Player "+playerID+" ("+playerName+"): is an opponent player");
//				playerType = new OpponentPlayer(playerID, rules);
//			}
//		} 
//		else {
//			myBid = null;
//		}
//	}
		

	/**
	 * Gets the id of the first forehand player in the current game
	 * @return id
	 */
	public int getInitialForehandPlayer() {
		return initialForehandPlayer;
	}

	/**
	 *
	 * @return true if this player is the single player
	 */
	public boolean isSinglePlayer() {
		return isSinglePlayer;
	}

	/**
	 * Gets the memory of all the cards that have been played in the current game
	 * @return CardMemory
	 */
	public CardMemory getCardMemory() {
		if(myMemory==null) myMemory = new CardMemory();
		return myMemory;
	}

	/**
	 * Gets the position of the single player
	 * @return position of the single player (forehand, middlehand, hindhand)
	 */
	public int getSinglePlayerPos() {
		return singlePlayerPos;
	}

	/**
	 * Sets the position of the single player
	 * @param i position of the single player (forehand, middlehand, hindhand)
	 */
	public void setSinglePlayerPos(int i) {
		singlePlayerPos = i;
	}

	/**
	 * @see jskat.player.JSkatPlayer#isAIPlayer()
	 */
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#cardPlayed(jskat.share.Card)
	 */
	public void cardPlayed(Card card) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#discloseOuvertCards(jskat.share.CardList)
	 */
	public void discloseOuvertCards(CardList ouvertCards) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.JSkatPlayer#removeCard(Suit.share.SkatConstants.Suits, Rank.share.SkatConstants.Ranks)
	 */
	public Card removeCard(Suit suit, Rank rank) {
		// TODO Auto-generated method stub
		return null;
	}

	/** log */
	private Log log = LogFactory.getLog(AIPlayerMJL.class);
	private int playerID;
	private CardMemory myMemory;
	private CardPlayer playerType;
	private Bidding myBid;
	private boolean isSinglePlayer;
	private int initialForehandPlayer;
	private int singlePlayerPos;
	private GameInfo game;
	private Suit selectedTrump;
	@Override
	public CardList discardSkat() {
		// TODO Auto-generated method stub
    	selectedTrump = SkatProcessor.processSkat(cards, skat);
		return null;
	}

	@Override
	public void finalizeGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean holdBid(int currBidValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean lookIntoSkat() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Card playCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void preparateForNewGame() {
		// TODO Auto-generated method stub
		
	}
}