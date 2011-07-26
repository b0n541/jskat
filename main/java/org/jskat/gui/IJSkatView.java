/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui;

import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jskat.control.SkatTable;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Interface for JSkat views
 */
public interface IJSkatView {

	/**
	 * Gets a new table name from the view
	 * 
	 * @param localTablesCreated
	 *            Local tables created so far
	 * 
	 * @return New table name
	 */
	public String getNewTableName(int localTablesCreated);

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
	 * @param tableName
	 *            Table name
	 * @param loginName
	 *            Login name on ISS
	 */
	public void createISSTable(String tableName, String loginName);

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
	 * @return List of player names
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
	 *            Message type @see {@link JOptionPane}
	 * @param title
	 *            Message title
	 * @param message
	 *            Message text
	 */
	public void showMessage(int messageType, String title, String message);

	/**
	 * Shows a message, that a card is not allowed
	 * 
	 * @param card
	 *            Card
	 */
	public void showCardNotAllowedMessage(Card card);

	/**
	 * Shows preferences dialog
	 */
	public void showPreferences();

	/**
	 * Shows training overview
	 */
	public void showTrainingOverview();

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
	 * @param madeBid
	 *            TRUE, if the player made the bid, FALSE if the player hold the
	 *            bid
	 */
	public void setBid(String tableName, Player player, int bidValue, boolean madeBid);

	/**
	 * Sets the pass bid for a player
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Player
	 */
	public void setPass(String tableName, Player player);

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
	public void setPositions(String tableName, Player leftPosition, Player rightPosition, Player playerPosition);

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
	 * @param rearHandCard
	 *            Card from hind hand
	 */
	public void setLastTrick(String tableName, Player trickForeHand, Card foreHandCard, Card middleHandCard,
			Card rearHandCard);

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
	 * @param declarer
	 *            Declarer
	 * @param ann
	 *            Game announcement
	 */
	public void setGameAnnouncement(String tableName, Player declarer, GameAnnouncement ann);

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
	 * Set a new series state
	 * 
	 * @param tableName
	 *            Table name
	 * @param state
	 *            New series state
	 */
	public void setSeriesState(String tableName, SeriesState state);

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
	 * Sets the bid value to make
	 * 
	 * @param tableName
	 *            Table name
	 * @param bidValue
	 *            Bid value
	 */
	public void setBidValueToMake(String tableName, int bidValue);

	/**
	 * Sets the bid value to hold
	 * 
	 * @param tableName
	 *            Table name
	 * @param bidValue
	 *            Bid value
	 */
	public void setBidValueToHold(String tableName, int bidValue);

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
	public void updateISSLobbyPlayerList(String playerName, String language, long gamesPlayed, double strength);

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
	public void updateISSLobbyTableList(String tableName, int maxPlayers, long gamesPlayed, String player1,
			String player2, String player3);

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
	public void appendISSChatMessage(ChatMessageType messageType, ChatMessage message);

	/**
	 * Updates an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param status
	 *            New table state
	 */
	public void updateISSTable(String tableName, TablePanelStatus status);

	/**
	 * Updates an ISS table
	 * 
	 * @param tableName
	 *            Table name
	 * @param loginName
	 *            Login name on ISS
	 * @param status
	 *            New game state
	 */
	public void updateISSTable(String tableName, String loginName, GameStartInformation status);

	/**
	 * Updates move information
	 * 
	 * @param tableName
	 *            Table name
	 * @param gameData
	 *            Game data
	 * @param moveInformation
	 *            Move information
	 */
	public void updateISSMove(String tableName, SkatGameData gameData, MoveInformation moveInformation);

	/**
	 * Sets the active player
	 * 
	 * @param tableName
	 *            Table name
	 * @param player
	 *            Active player
	 */
	public void setActivePlayer(String tableName, Player player);

	/**
	 * Sets the skat
	 * 
	 * @param tableName
	 *            Table name
	 * @param skat
	 *            Skat
	 */
	public void setSkat(String tableName, CardList skat);

	/**
	 * Sets the trick number
	 * 
	 * @param tableName
	 *            Table name
	 * @param trickNumber
	 *            Trick number
	 */
	public void setTrickNumber(String tableName, int trickNumber);

	/**
	 * Shows an invitation message for ISS
	 * 
	 * @param invitor
	 *            Invitor
	 * @param tableName
	 *            Table name
	 * @return TRUE, if the user accepted the invitation
	 */
	public boolean showISSTableInvitation(String invitor, String tableName);

	/**
	 * Sets the game result in the view
	 * 
	 * @param tableName
	 *            Table name
	 * @param gameData
	 *            Game data
	 */
	public void setGameResult(String tableName, SkatGameData gameData);

	/**
	 * Sets the game result in the view
	 * 
	 * @param tableName
	 *            Table name
	 * @param gameData
	 *            Game data
	 */
	public void setGameResultWithoutSkatList(String tableName,
			SkatGameData gameData);

	/**
	 * Adds training results
	 * 
	 * @param gameType
	 *            Game type of net
	 * @param episodes
	 *            Number of episodes
	 * @param totalWonGames
	 *            Total number of won games
	 * @param episodeWonGames
	 *            Number of games won in last episode
	 * @param totalDeclarerNetError
	 *            Total error in declarer net
	 * @param totalOpponentNetError
	 *            Total error in opponent net
	 */
	public void addTrainingResult(GameType gameType, long episodes, long totalWonGames, long episodeWonGames,
			double totalDeclarerNetError, double totalOpponentNetError);

	/**
	 * Sets the game number of the current game
	 * 
	 * @param tableName
	 *            Table name
	 * @param gameNumber
	 *            Game number
	 */
	public void setGameNumber(String tableName, int gameNumber);

	/**
	 * Sets the player names of a table
	 * 
	 * @param tableName
	 *            Table name
	 * @param playerName
	 *            Name of left upper player
	 * @param playerName2
	 *            Name of right upper player
	 * @param playerName3
	 *            Name of lower player
	 */
	public void setPlayerNames(String tableName, String upperLeftPlayerName, String upperRightPlayerName,
			String lowerPlayerName);

	/**
	 * Sets the declarer player of the table
	 * 
	 * @param tableName
	 *            Table name
	 * @param declarer
	 *            Declarer player
	 */
	public void setDeclarer(String tableName, Player declarer);

	/**
	 * Closes alle ISS related panels
	 */
	public void closeISSPanels();

	/**
	 * Shows a message, that a table with a given name already exists
	 */
	public void showDuplicateTableNameMessage(String duplicateTableName);
}