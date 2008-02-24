/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.player;

import jskat.data.GameAnnouncement;
import jskat.share.CardVector;
import jskat.share.Card;
import jskat.share.SkatConstants;

/**
 * Player interface for AI players
 */
public interface JSkatPlayer {

	/** 
	 * Informs the player about the start of a new game 
	 */
	public void newGame();
	
	/** 
	 * Notifies the player of the start of bidding for a new game 
	 * 
	 * @param initialForehandPlayer Player ID of the initial fore hand player 
	 */
	public void setUpBidding(int initialForehandPlayer);

	/** 
	 * Asks the player whether it wants to bid higher or not 
	 * 
	 * @param currBidValue Current bid value
	 * @return TRUE if the player wants to make the bid
	 */
	public boolean bidMore(int currBidValue);

	/** 
	 * Takes a card from the dealer
	 *  
	 * @param newCard New Card 
	 */
	public void takeCard(Card newCard);

	/** 
	 * Checks whether the player wants to look into the skat
	 *  
	 * @param isRamsch TRUE if the current game is a ramsch game
	 * @return TRUE if the player wants to look into the skat 
	 */
	public boolean lookIntoSkat(boolean isRamsch);

	/**
	 * Take the skat as a single player.<br>
	 * When the method is done, the skat CardVector must still contain exactly
	 * two cards. Otherwise a SkatHandlingException might be thrown.
	 * 
	 * @param skat The skat Cards 
	 */
	public void takeSkat(CardVector skat);

	/** 
	 * Take the skat in a ramsch round 
	 * 
	 * @param skat The skat Cards
	 * @param jacksAllowed TRUE if jacks are allowed to be discarded
	 */
	public void takeRamschSkat(CardVector skat, boolean jacksAllowed);

	/** 
	 * Asks the player for the game he wants to play
	 *  
	 * @return Game announcement 
	 */
	public GameAnnouncement announceGame();

	/** 
	 * Start the game: inform player of game type, trumpf and special options 
	 * 
	 * @param singlePlayer Player ID of the single player 
	 * @param forehandPlayer Player ID of the fore hand player
	 * @param gameType Game type
	 * @param trump Trump suit
	 * @param handGame TRUE if the game is a hand game
	 * @param ouvertGame TRUE if the game is an ouvert game
	 */
	public void startGame(int singlePlayer, int forehandPlayer, SkatConstants.GameTypes gameType,
			SkatConstants.Suits trump, boolean handGame, boolean ouvertGame);

	/** 
	 * Shows the cards of the single player to the opponents in ouvert games
	 *  
	 * @param ouvertCards Cards of the single player in an ouver game 
	 */
	public void discloseOuvertCards(CardVector ouvertCards);
	
	/**
	 * Get next Card to play
	 * 
	 * @param trick Cards of the trick
	 * @return Card to be played
	 */
	public Card playCard(CardVector trick);
	
	/**
	 * Informs the player about a card that was played
	 * 
	 * @param card The Card the was played
	 */
	public void cardPlayed(Card card);
	
	/** 
	 * Makes the current trick known to the players when it is complete
	 *  
	 * @param trick Cards of the trick
	 * @param trickWinner Player ID of the trick winner
	 */
	public void showTrick(CardVector trick, int trickWinner);

	/** 
	 * Get the selected card from the player's hand
	 *  
	 * @param suit Suit of the card to be removed 
	 * @param rank Rank of the card to be removed
	 * @return Card that was removed 
	 */
	public Card removeCard(SkatConstants.Suits suit, SkatConstants.Ranks rank);

	/** Gets the name of the skat player 
	 *
	 * @return Player name
	 */
	public String getPlayerName();

	/** 
	 * Sets the name of the skat player 
	 *
	 * @param newPlayerName Player name to be set
	 */
	public void setPlayerName(String newPlayerName);

	/** Gets the ID of the skat player
	 *
	 * @return Player ID
	 */
	public int getPlayerID();

	/** 
	 * Sets the ID of the skat player
	 * 
	 * @param newPlayerID Player ID to be set
	 */
	public void setPlayerID(int newPlayerID);

	/**
	 * Checks whether the player is a human player
	 * 
	 * @return TRUE if the player is a human player
	 */
	public boolean isHumanPlayer();

	/**
	 * Checks whether the player is an AI player
	 * 
	 * @return TRUE if the player is an AI player
	 */
	public boolean isAIPlayer();

	/**
	 * Holds all player states
	 */
	public enum PlayerStates {
		/**
		 * Player waits
		 */
		WAITING, 
		/**
		 * Player receives cards during dealing
		 */
		DEALING, 
		/**
		 * Player takes part in bidding
		 */
		BIDDING, 
		/**
		 * Player plays the tricks
		 */
		PLAYING
	}
}