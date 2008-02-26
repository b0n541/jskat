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
import jskat.share.SkatConstants.Ranks;
import jskat.share.SkatConstants.Suits;
import jskat.data.GameAnnouncement;
import jskat.player.JSkatPlayer;
import jskat.player.AbstractJSkatPlayer;

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
     * Creates a new instance of SkatPlayer with a player ID
     * @param playerID
     */
    public AIPlayerMJL(int playerID) {
        
        super();
        setPlayerID(playerID);
        setPlayerName("---undefined---");
        isSinglePlayer = false;
    }
    
    /**
     * Creates a new instance of SkatPlayer with a player ID and a name
     * @param playerID
     * @param playerName
     */
    public AIPlayerMJL(int playerID, String playerName) {
        
        super();
        setPlayerID(playerID);
        setPlayerName(playerName);
        isSinglePlayer = false;
		log.debug("Player "+playerID+": Constructing AIPlayerMJL ("+playerName+")");
    }
    
    /** (non-Javadoc)
     * @see jskat.player.JSkatPlayer#takeRamschSkat(jskat.share.CardVector)
     */
	public void takeRamschSkat(CardVector skat, boolean jacksAllowed) {
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

    /** (non-Javadoc)
     * @see jskat.player.JSkatPlayer#takeSkat(jskat.share.CardVector)
     */
    public void takeSkat(CardVector skat) {
    	selectedTrump = SkatProcessor.processSkat(cards, skat);
    }

    /** 
     * Asks the player for the game he wants to play
     * @see jskat.player.JSkatPlayer#announceGame()
     */
    public GameAnnouncement announceGame() {
    	
        isSinglePlayer = true;
    	GameAnnouncement newGame = new GameAnnouncement();
        
        newGame.setGameType(SkatConstants.GameTypes.SUIT);
        newGame.setTrump(selectedTrump);
        
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
    public boolean bidMore(int currBidValue) {
		boolean iDoThisBid = true;
		
		// set the game state
		if(myBid==null) {
			this.setState(JSkatPlayer.PlayerStates.BIDDING);
		}
		
	    int maxBid = myBid.getMaxBid();
		if (maxBid < currBidValue)
			iDoThisBid = false;
        return iDoThisBid;
    }
    
	/** 
	 * Start the game: inform player of game type, trumpf and special options
	 * @see jskat.player.JSkatPlayer#startGame(int, int, int, int, boolean, boolean)
     * @param singlePlayer
     * @param forehandPlayer
     * @param gameType
     * @param trump
     * @param handGame
     * @param ouvertGame
	 */
	public void startGame(int singlePlayer, int forehandPlayer, SkatConstants.GameTypes gameType,
			SkatConstants.Suits trump, boolean handGame, boolean ouvertGame) {
		super.startGame(singlePlayer, forehandPlayer, gameType, trump, handGame, ouvertGame);
		singlePlayerPos = singlePlayer - forehandPlayer;
		if(singlePlayerPos<0) singlePlayerPos +=3;
		log.debug("SinglePlayer is in position "+singlePlayerPos+", when single player is "+singlePlayer+" and forehand is "+forehandPlayer+" (and I am player "+playerID+")");
		if(singlePlayer==playerID) {
	        isSinglePlayer = true;
		}
		else {
	        isSinglePlayer = false;
		}
		
		myBid = null;
		myMemory = new CardMemory(gameType, trump);
		game = new GameInfo(gameType, trump, singlePlayer);		
		this.setState(JSkatPlayer.PlayerStates.PLAYING);
	}


    /** Get next Card to play
     * @see jskat.player.JSkatPlayer#playNextCard(jskat.share.CardVector)
     */
    public int playNextCard(CardVector trick) {
		// set the current game state
		if(playerType==null) {
			this.setState(JSkatPlayer.PlayerStates.PLAYING);
		}
        TrickInfo thisTrick = new TrickInfo();
        thisTrick.setGameInfo(game);
        thisTrick.setTrick(trick);
		thisTrick.setSinglePlayerPos(singlePlayerPos);
		thisTrick.setForehandPlayer(forehandPlayer);
        return playerType.playNextCard(cards, thisTrick);
    }
    
    /**
     * @see jskat.player.JSkatPlayer#playCard(jskat.share.CardVector)
     */
    public Card playCard(CardVector trick) {
    	return cards.remove(playNextCard(trick));
    }
	
	/** 
	 * Makes the current trick known to the players when it is complete
	 * @see jskat.player.JSkatPlayer#showTrick(jskat.share.CardVector, int)
	 */
	public void showTrick(CardVector trick, int trickWinner) {
		// remember the cards that have been played by each player
		log.debug("Player "+playerID+" received the trick ["+trick+"], where the winner is player "+trickWinner);

		if(myMemory != null)
			myMemory.addTrick(trick, forehandPlayer);
		else {
			log.warn("CardMemory should exist, but is null!");
			myMemory = new CardMemory();
			myMemory.addTrick(trick, forehandPlayer);
		}
		// set the forehand player for the next trick
		forehandPlayer = trickWinner;
		singlePlayerPos = singlePlayer - forehandPlayer;
		if(singlePlayerPos<0) singlePlayerPos +=3;
		log.debug("SinglePlayer is now in position "+singlePlayerPos+", when single player is "+singlePlayer+" and forehand is "+forehandPlayer);
		
	}

	/** 
	 * Sets the state of the current game <br>
	 * If necessary, a new bid is initialized.
	 * @see jskat.player.AIPlayerMJL.Bidding  
	 * @see jskat.player.AbstractJSkatPlayer#setState(JSkatPlayer.PlayerStates)
	 */
	public final void setState(JSkatPlayer.PlayerStates newState) {
        
		log.debug("Player "+playerID+" ("+playerName+"): Setting new state:"+newState);
		super.setState(newState);
		if(newState == JSkatPlayer.PlayerStates.BIDDING) {
			myBid = new Bidding(cards);
			playerType = null;
		} 
		else if(newState == JSkatPlayer.PlayerStates.PLAYING) {
			myBid = null;
			if(game!=null && game.getGameType() == SkatConstants.GameTypes.RAMSCH) {
				log.debug("Player "+playerID+" ("+playerName+"): is a ramsch player");
			    playerType = new RamschPlayer(playerID, rules);
			}
			else if(isSinglePlayer) {
				log.debug("Player "+playerID+" ("+playerName+"): is the single player");
				playerType = new SinglePlayer(playerID, rules);
			}
			else {
				log.debug("Player "+playerID+" ("+playerName+"): is an opponent player");
				playerType = new OpponentPlayer(playerID, rules);
			}
		} 
		else {
			myBid = null;
		}
	}
		

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
	 * @see jskat.player.JSkatPlayer#isHumanPlayer()
	 */
	public boolean isHumanPlayer() {

		return false;
	}
	
	/**
	 * @see jskat.player.AbstractJSkatPlayer#cardPlayed(jskat.share.Card)
	 */
	@Override
	public void cardPlayed(Card card) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#discloseOuvertCards(jskat.share.CardVector)
	 */
	@Override
	public void discloseOuvertCards(CardVector ouvertCards) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.JSkatPlayer#removeCard(jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Ranks)
	 */
	public Card removeCard(Suits suit, Ranks rank) {
		// TODO Auto-generated method stub
		return null;
	}

	/** log */
	private static final Logger log = Logger.getLogger(AIPlayerMJL.class);
	private CardMemory myMemory;
	private CardPlayer playerType;
	private Bidding myBid;
	private boolean isSinglePlayer;
	private int initialForehandPlayer;
	private int singlePlayerPos;
	private GameInfo game;
	private SkatConstants.Suits selectedTrump;
}