/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import de.jskat.control.SkatGame;
import de.jskat.control.SkatTable;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameStates;
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.Player;

/**
 * Interface for JSkat views
 */
public interface JSkatView {

	/**
	 * Shows a table
	 * 
	 * @param table Skat table
	 */
	public abstract void showTable(SkatTable table);
	
	/**
	 * Opens a new series
	 */
	public abstract void startSeries();

	/**
	 * Shows the results of a series
	 */
	public abstract void showSeriesResults();

	/**
	 * Starts a new game
	 * 
	 * @param game Skat game
	 */
	public abstract void startGame(SkatGame game);

	/**
	 * Starts bidding
	 */
	public abstract void startBidding();

	/**
	 * Starts discarding
	 */
	public abstract void startDiscarding();

	/**
	 * Starts playing
	 */
	public abstract void startPlaying();

	/**
	 * Shows the results of a game
	 */
	public abstract void showGameResults();
	
	/**
	 * Shows the login panel for ISS
	 */
	public abstract void showISSLoginPanel();

	/**
	 * Creates ISS table panel
	 * 
	 * @param name Name of the table
	 */
	public abstract void createISSTablePanel(String name);

	/**
	 * Creates a local table panel
	 * 
	 * @param name Name of the table
	 * @return Reference to the table panel
	 */
	public SkatTablePanel createSkatTablePanel(String name);
	
	/**
	 * Shows the about dialog
	 */
	public abstract void showAboutMessage();

	/**
	 * Shows a message dialog
	 * 
	 * @param messageType Message type 
	 * @param message Message text
	 */
	public abstract void showMessage(int messageType, String message);

	/**
	 * Shows the exit dialog
	 * 
	 * @return User decision
	 */
	public abstract int showExitDialog();

	/**
	 * Adds a card to a players hand
	 * 
	 * @param tableName Table name
	 * @param player Player
	 * @param card Card
	 */
	public abstract void addCard(String tableName, Player player, Card card);

	/**
	 * Removes a card from a players hand
	 * 
	 * @param tableName Table name
	 * @param player Player
	 * @param card Card
	 */
	public abstract void removeCard(String tableName, Player player, Card card);

	/**
	 * Removes all cards from a players hand
	 * 
	 * @param tableName Table name
	 * @param player Player
	 */
	public abstract void clearHand(String tableName, Player player);

	/**
	 * Sets the bid value for a player
	 * 
	 * @param tableName Table name
	 * @param player Player
	 * @param bidValue Bid value
	 */
	public void setBid(String tableName, Player player, int bidValue);
	
	/**
	 * Sets the player positions for a table
	 * 
	 * @param tableName Table name
	 * @param leftPosition Player in upper left position
	 * @param rightPosition Player in upper right position
	 * @param playerPosition Player in lower position
	 */
	public void setPositions(String tableName, Player leftPosition, Player rightPosition,
			Player playerPosition);

	/**
	 * Adds a card to the trick
	 * 
	 * @param tableName Table name
	 * @param position Player position
	 * @param card Card
	 */
	public void setTrickCard(String tableName, Player position, Card card);
	
	/**
	 * Clears the cards of the trick
	 * 
	 * @param tableName Table name
	 */
	public void clearTrickCards(String tableName);

	/**
	 * Sets the game announcement
	 * 
	 * @param tableName Table name
	 * @param ann Game announcement
	 * @param hand TRUE if the game is a hand game
	 */
	public void setGameAnnouncement(String tableName, GameAnnouncement ann, boolean hand);
	
	/**
	 * Set a new game state
	 * 
	 * @param tableName Table name
	 * @param state New game state
	 */
	public void setGameState(String tableName, GameStates state);
	
	/**
	 * Adds a game result
	 * 
	 * @param tableName Table name
	 * @param data Game data
	 */
	public void addGameResult(String tableName, SkatGameData data);

	/**
	 * Shows the help dialog
	 */
	public void showHelpDialog();
	
	/**
	 * Clears a complete table
	 * 
	 * @param tableName Table name
	 */
	public void clearTable(String tableName);
	
	/**
	 * Sets the next bid value
	 * 
	 * @param tableName Table name
	 * @param nextBidValue Next bid value
	 */
	public void setNextBidValue(String tableName, int nextBidValue);

	/**
	 * Sets the new trick fore hand
	 * 
	 * @param tableName Table name
	 * @param trickForeHand Trick fore hand
	 */
	public abstract void setTrickForeHand(String tableName, Player trickForeHand);
	
	/**
	 * Sets the skat cards
	 * 
	 * @param tableName Table name
	 * @param skat Skat cards
	 */
	public abstract void setSkat(String tableName, CardList skat);

	/**
	 * Takes a card from the skat
	 * 
	 * @param tableName Table name
	 * @param card Card
	 */
	public abstract void takeCardFromSkat(String tableName, Card card);

	/**
	 * Puts a card into the skat
	 * 
	 * @param tableName Table name
	 * @param card Card
	 */
	public abstract void putCardIntoSkat(String tableName, Card card);

	/**
	 * Shows the start dialog for skat series
	 */
	public abstract void showStartSkatSeriesDialog();
}