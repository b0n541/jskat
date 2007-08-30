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

/**
 * The JSkat Player Interface
 * 
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public interface JSkatPlayer {

	/** Informs the player about the start of a new game */
	public void newGame();
	
	/** Notifies the player of the start of bidding for a new game */
	public void setUpBidding(int initialForehandPlayer);

	/** Asks the player whether it wants to bid higher or not */
	public boolean bidMore(int currBidValue);

	/** Take a card from someone */
	public void takeCard(Card newCard);

	/** Check if you want to look in the skat */
	public boolean lookIntoSkat(boolean isRamsch);

	/**
	 * Take the skat as a single player.<br>
	 * When the method is done, the skat CardVector must still contain exactly
	 * two cards. Otherwise a SkatHandlingException might be thrown.
	 */
	public void takeSkat(CardVector skat);

	/** Take the skat in a ramsch round */
	public void takeRamschSkat(CardVector skat, boolean jacksAllowed);

	/** Asks the player for the game he wants to play */
	public GameAnnouncement announceGame();

	/** Start the game: inform player of game type, trumpf and special options */
	public void startGame(int singlePlayer, int forehandPlayer, int gameType,
			int trump, boolean handGame, boolean ouvertGame);

	/**
	 * Get next Card to play
	 *
	 * @deprecated
	 */
	public int playNextCard(CardVector trick);

	/**
	 * Get next Card to play
	 * 
	 */
	public Card playCard(CardVector trick);
	
	/**
	 * Informs the player about a card that was played
	 * 
	 * @param card The Card the was played
	 */
	public void cardPlayed(Card card);
	
	/** Makes the current trick known to the players when it is complete */
	public void showTrick(CardVector trick, int trickWinner);

	/** Get the selected card from the player's hand */
	public Card removeCard(int suit, int value);

	/**
	 * Get the selected card from the player's hand
	 *
	 * @deprecated
	 */
	public Card removeCard(Card card);

	/**
	 * Get the selected card from the player's hand
	 *
	 * @deprecated
	 */
	public Card removeCard(int index);

	/** Returns a reference to the CardVector of the SkatPlayer */
	//public CardVector getCardVector();

	/** Returns the name of the SkatPlayer */
	public String getPlayerName();

	/** Sets the name of the SkatPlayer */
	public void setPlayerName(String newPlayerName);

	/** Returns the ID of the SkatPlayer */
	public int getPlayerID();

	/** Returns the ID of the SkatPlayer */
	public void setPlayerID(int newPlayerID);

	/**
	 * @return true, if the player is a human player
	 */
	public boolean isHumanPlayer();

	/**
	 * @return true, if the player is an AI player
	 */
	public boolean isAIPlayer();
}