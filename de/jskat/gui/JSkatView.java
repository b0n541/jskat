/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.util.List;
import java.util.Set;

import de.jskat.control.SkatTable;
import de.jskat.control.iss.ChatMessageType;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSGameStartInformation;
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
	public String getNewTableName();

	/**
	 * Shows a table
	 * 
	 * @param table
	 *            Skat table
	 */
	public void showTable(SkatTable table);

	/**
	 * Opens a new series
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void startSeries(String tableName);

	/**
	 * Shows the results of a series
	 */
	public void showSeriesResults();

	/**
	 * Starts a new game
	 * 
	 * @param tableName
	 *            Table name
	 */
	public void startGame(String tableName);

	/**
	 * Starts bidding
	 */
	public void startBidding();

	/**
	 * Starts discarding
	 */
	public void startDiscarding();

	/**
	 * Starts playing
	 */
	public void startPlaying();

	/**
	 * Shows the results of a game
	 */
	public void showGameResults();

	/**
	 * Shows the login for ISS
	 */
	public void showISSLogin();

	/**
	 * Shows the lobby for ISS
	 */
	public void showISSLobby();

	/**
	 * Creates ISS table
	 * 
	 * @param name
	 *            Name of the table
	 */
	public void createISSTable(String name);

	/**
	 * Creates a local table panel
	 * 
	 * @param name
	 *            Name of the table
	 */
	public void createSkatTablePanel(String name);

	/**
	 * Closes a tab panel
	 * 
	 * @param name
	 *            Name of the tab panel
	 */
	public void closeTabPanel(String name);

	/**
	 * Gets the players to invite
	 * 
	 * @param playerNames
	 *            Available players
	 */
	public List<String> getPlayerForInvitation(Set<String> playerNames);

	/**
	 * Shows the about dialog
	 */
	public void showAboutMessage();

	/**
	 * Shows a message dialog
	 * 
	 * @param messageType
	 *            Message type @see {@link JOPtionPane}
	 * @param message
	 *            Message text
	 */
	public void showMessage(int messageType, String message);

	/**
	 * Shows the exit dialog
	 * 
	 * @return User decision
	 */
	public int showExitDialog();

	/**
	 * Shows preferences dialog
	 */
	public void showPreferences();

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
	public void addCard(String tableName, Player player, Card card);

	/**
	 * Adds a list of cards to a players hand
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Player
	 * @param cards
	 *            List of cards
	 */
	public void addCards(String tableName, Player player, CardList cards);

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
	public void removeCard(String tableName, Player player, Card card);

	/**
	 * Removes all cards from a players hand
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Player
	 */
	public void clearHand(String tableName, Player player);

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
	 */
	public void setGameAnnouncement(String tableName, GameAnnouncement ann);

	/**
	 * Set a new game state
	 * 
	 * @param tableName
	 *            Table name
	 * @param state
	 *            New game state
	 */
	public void setGameState(String tableName, GameState state);

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
	public void setTrickForeHand(String tableName, Player trickForeHand);

	/**
	 * Takes a card from the skat
	 * 
	 * @param tableName
	 *            Table name
	 * @param card
	 *            Card
	 */
	public void takeCardFromSkat(String tableName, Card card);

	/**
	 * Puts a card into the skat
	 * 
	 * @param tableName
	 *            Table name
	 * @param card
	 *            Card
	 */
	public void putCardIntoSkat(String tableName, Card card);

	/**
	 * Shows the start dialog for skat series
	 */
	public void showStartSkatSeriesDialog();

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
	public void updateISSLobbyPlayerList(String playerName, String language,
			long gamesPlayed, double strength);

	/**
	 * Removes a client from the list of the ISS lobby
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removeFromISSLobbyPlayerList(String playerName);

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
	public void updateISSLobbyTableList(String tableName, int maxPlayers,
			long gamesPlayed, String player1, String player2, String player3);

	/**
	 * Removes a table from the table list of the ISS lobby
	 * 
	 * @param tableName
	 */
	public void removeFromISSLobbyTableList(String tableName);

	/**
	 * Appends a new chat message to a chat
	 * 
	 * @param messageType
	 *            Type of message
	 * @param message
	 *            Message
	 */
	public void appendISSChatMessage(ChatMessageType messageType,
			ISSChatMessage message);

	/**
	 * Updates an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param status
	 *            New table state
	 */
	public void updateISSTable(String tableName, ISSTablePanelStatus status);

	/**
	 * Updates an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param playerName
	 *            Player name
	 * @param status
	 *            New game state
	 */
	public void updateISSTable(String tableName, String playerName,
			ISSGameStartInformation status);

	/**
	 * Updates move information
	 * 
	 * @param tableName
	 *            Table name
	 * @param moveInformation
	 *            Move information
	 */
	public void updateISSMove(String tableName,
			ISSMoveInformation moveInformation);
}