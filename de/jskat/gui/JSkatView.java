/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import de.jskat.control.SkatGame;
import de.jskat.control.SkatTable;
import de.jskat.control.iss.ChatMessageType;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameStates;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSGameStatus;
import de.jskat.data.iss.ISSMoveInformation;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Interface for JSkat views
 */
public interface JSkatView {

	/**
	 * Gets a new table name from the view
	 * 
	 * @return New table name
	 */
	public abstract String getNewTableName();

	/**
	 * Shows a table
	 * 
	 * @param table
	 *            Skat table
	 */
	public abstract void showTable(SkatTable table);

	/**
	 * Opens a new series
	 * 
	 * @param tableName
	 *            Table name
	 */
	public abstract void startSeries(String tableName);

	/**
	 * Shows the results of a series
	 */
	public abstract void showSeriesResults();

	/**
	 * Starts a new game
	 * 
	 * @param tableName
	 *            Table name
	 */
	public abstract void startGame(String tableName);

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
	 * Shows the login for ISS
	 */
	public abstract void showISSLogin();

	/**
	 * Shows the lobby for ISS
	 */
	public abstract void showISSLobby();

	/**
	 * Creates ISS table
	 * 
	 * @param name
	 *            Name of the table
	 */
	public abstract void createISSTable(String name);

	/**
	 * Creates a local table panel
	 * 
	 * @param name
	 *            Name of the table
	 * @return Reference to the table panel
	 */
	public void createSkatTablePanel(String name);

	/**
	 * Shows the about dialog
	 */
	public abstract void showAboutMessage();

	/**
	 * Shows a message dialog
	 * 
	 * @param messageType
	 *            Message type
	 * @param message
	 *            Message text
	 */
	public abstract void showMessage(int messageType, String message);

	/**
	 * Shows the exit dialog
	 * 
	 * @return User decision
	 */
	public abstract int showExitDialog();

	/**
	 * Shows preferences dialog
	 */
	public abstract void showPreferences();

	/**
	 * Adds a card to a players hand
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public abstract void addCard(String tableName, Player player, Card card);

	/**
	 * Removes a card from a players hand
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public abstract void removeCard(String tableName, Player player, Card card);

	/**
	 * Removes all cards from a players hand
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Player
	 */
	public abstract void clearHand(String tableName, Player player);

	/**
	 * Sets the bid value for a player
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Player
	 * @param bidValue
	 *            Bid value
	 */
	public void setBid(String tableName, Player player, int bidValue);

	/**
	 * Sets the player positions for a table
	 * 
	 * @param tableName
	 *            Table name
	 * @param leftPosition
	 *            Player in upper left position
	 * @param rightPosition
	 *            Player in upper right position
	 * @param playerPosition
	 *            Player in lower position
	 */
	public void setPositions(String tableName, Player leftPosition,
			Player rightPosition, Player playerPosition);

	/**
	 * Adds a card to the trick
	 * 
	 * @param tableName
	 *            Table name
	 * @param position
	 *            Player position
	 * @param card
	 *            Card
	 */
	public void setTrickCard(String tableName, Player position, Card card);

	/**
	 * Sets the cards for the last trick
	 * 
	 * @param tableName
	 *            tableName
	 * @param trickForeHand
	 *            Fore hand player in this trick
	 * @param foreHandCard
	 *            Card from fore hand
	 * @param middleHandCard
	 *            Card from middle hand
	 * @param hindHandCard
	 *            Card from hind hand
	 */
	public void setLastTrick(String tableName, Player trickForeHand,
			Card foreHandCard, Card middleHandCard, Card hindHandCard);

	/**
	 * Clears the cards of the trick
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void clearTrickCards(String tableName);

	/**
	 * Plays a card during card play
	 * 
	 * @param tableName
	 *            Table name
	 * @param position
	 *            Player position
	 * @param card
	 *            Card
	 */
	public void playTrickCard(String tableName, Player position, Card card);

	/**
	 * Sets the game announcement
	 * 
	 * @param tableName
	 *            Table name
	 * @param ann
	 *            Game announcement
	 * @param hand
	 *            TRUE if the game is a hand game
	 */
	public void setGameAnnouncement(String tableName, GameAnnouncement ann,
			boolean hand);

	/**
	 * Set a new game state
	 * 
	 * @param tableName
	 *            Table name
	 * @param state
	 *            New game state
	 */
	public void setGameState(String tableName, GameStates state);

	/**
	 * Adds a game result
	 * 
	 * @param tableName
	 *            Table name
	 * @param data
	 *            Game data
	 */
	public void addGameResult(String tableName, SkatGameData data);

	/**
	 * Shows the help dialog
	 */
	public void showHelpDialog();

	/**
	 * Shows the license dialog
	 */
	public void showLicenseDialog();

	/**
	 * Clears a complete table
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void clearTable(String tableName);

	/**
	 * Sets the next bid value
	 * 
	 * @param tableName
	 *            Table name
	 * @param nextBidValue
	 *            Next bid value
	 */
	public void setNextBidValue(String tableName, int nextBidValue);

	/**
	 * Sets the new trick fore hand
	 * 
	 * @param tableName
	 *            Table name
	 * @param trickForeHand
	 *            Trick fore hand
	 */
	public abstract void setTrickForeHand(String tableName, Player trickForeHand);

	/**
	 * Sets the skat cards
	 * 
	 * @param tableName
	 *            Table name
	 * @param skat
	 *            Skat cards
	 */
	public abstract void setSkat(String tableName, CardList skat);

	/**
	 * Takes a card from the skat
	 * 
	 * @param tableName
	 *            Table name
	 * @param card
	 *            Card
	 */
	public abstract void takeCardFromSkat(String tableName, Card card);

	/**
	 * Puts a card into the skat
	 * 
	 * @param tableName
	 *            Table name
	 * @param card
	 *            Card
	 */
	public abstract void putCardIntoSkat(String tableName, Card card);

	/**
	 * Shows the start dialog for skat series
	 */
	public abstract void showStartSkatSeriesDialog();

	/**
	 * Updates the client list of the ISS lobby
	 * 
	 * @param playerName
	 *            Player name
	 * @param language
	 *            Languages spoken by the human player or '-' for AI player
	 * @param gamesPlayed
	 *            Number of games played so far
	 * @param strength
	 *            Playing strength after ISS evaluation
	 */
	public abstract void updateISSLobbyPlayerList(String playerName,
			String language, long gamesPlayed, double strength);

	/**
	 * Removes a client from the list of the ISS lobby
	 * 
	 * @param playerName
	 *            Player name
	 */
	public abstract void removeFromISSLobbyPlayerList(String playerName);

	/**
	 * Updates the table list of the ISS lobby
	 * 
	 * @param tableName
	 *            Table name
	 * @param maxPlayers
	 *            Maximum players allowed on the table
	 * @param gamesPlayed
	 *            Games played so far
	 * @param player1
	 *            Player 1 (? for free seat)
	 * @param player2
	 *            Player 2 (? for free seat)
	 * @param player3
	 *            Player 3 (? for free seat)
	 */
	public abstract void updateISSLobbyTableList(String tableName,
			int maxPlayers, long gamesPlayed, String player1, String player2,
			String player3);

	/**
	 * Removes a table from the table list of the ISS lobby
	 * 
	 * @param tableName
	 */
	public abstract void removeFromISSLobbyTableList(String tableName);

	/**
	 * Appends a new chat message to a chat
	 * 
	 * @param messageType
	 *            Type of message
	 * @param message
	 *            Message
	 */
	public abstract void appendISSChatMessage(ChatMessageType messageType,
			ISSChatMessage message);

	/**
	 * Updates an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param status
	 *            New table state
	 */
	public abstract void updateISSTable(String tableName,
			ISSTablePanelStatus status);

	/**
	 * Updates an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param status
	 *            New game state
	 */
	public abstract void updateISSTable(String tableName, ISSGameStatus status);

	/**
	 * Updates move information
	 * 
	 * @param tableName
	 *            Table name
	 * @param moveInformation
	 *            Move information
	 */
	public abstract void updateISSMove(String tableName,
			ISSMoveInformation moveInformation);
}