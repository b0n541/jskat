package org.jskat.control.gui;

import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.SkatGameData;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.List;
import java.util.Set;

/**
 * Interface for JSkat views
 */
public interface JSkatView {

    /**
     * Gets a new table name from the view
     *
     * @param localTablesCreated Local tables created so far
     * @return New table name
     */
    String getNewTableName(int localTablesCreated);

    /**
     * Starts a new game
     *
     * @param tableName Table name
     */
    void startGame(String tableName);

    /**
     * Shows the login for ISS
     */
    void showISSLogin();

    /**
     * Gets the players to invite
     *
     * @param playerNames Available players
     * @return List of player names
     */
    List<String> getPlayerForInvitation(Set<String> playerNames);

    /**
     * Shows a message dialog
     *
     * @param title   Message title
     * @param message Message text
     */
    void showMessage(String title, String message);

    /**
     * Shows an error message dialog
     *
     * @param title   Message title
     * @param message Message text
     */
    void showErrorMessage(String title, String message);

    /**
     * Shows a message, that a card is not allowed
     *
     * @param card Card
     */
    void showCardNotAllowedMessage(Card card);

    /**
     * Sets the bid value to make
     *
     * @param tableName Table name
     * @param bidValue  Bid value
     */
    void setBidValueToMake(String tableName, int bidValue);

    /**
     * Sets the bid value to hold
     *
     * @param tableName Table name
     * @param bidValue  Bid value
     */
    void setBidValueToHold(String tableName, int bidValue);

    /**
     * Updates the client list of the ISS lobby
     *
     * @param playerName  Player name
     * @param language    Languages spoken by the human player or '-' for AI player
     * @param gamesPlayed Number of games played so far
     * @param strength    Playing strength after ISS evaluation
     */
    void updateISSLobbyPlayerList(String playerName, String language,
                                  long gamesPlayed, double strength);

    /**
     * Removes a client from the list of the ISS lobby
     *
     * @param playerName Player name
     */
    void removeFromISSLobbyPlayerList(String playerName);

    /**
     * Updates the table list of the ISS lobby
     *
     * @param tableName   Table name
     * @param maxPlayers  Maximum players allowed on the table
     * @param gamesPlayed Games played so far
     * @param player1     Player 1 (? for free seat)
     * @param player2     Player 2 (? for free seat)
     * @param player3     Player 3 (? for free seat)
     */
    void updateISSLobbyTableList(String tableName, int maxPlayers,
                                 long gamesPlayed, String player1, String player2, String player3);

    /**
     * Removes a table from the table list of the ISS lobby
     *
     * @param tableName Table name
     */
    void removeFromISSLobbyTableList(String tableName);

    /**
     * Appends a new chat message to a chat
     *
     * @param messageType Type of message
     * @param message     Message
     */
    void appendISSChatMessage(ChatMessageType messageType,
                              ChatMessage message);

    /**
     * Updates an ISS table
     *
     * @param tableName Table name
     * @param status    New table state
     */
    void updateISSTable(String tableName, TablePanelStatus status);

    /**
     * Updates an ISS table
     *
     * @param tableName Table name
     * @param loginName Login name on ISS
     * @param status    New game state
     */
    void updateISSTable(String tableName, String loginName,
                        GameStartInformation status);

    /**
     * Updates move information
     *
     * @param tableName       Table name
     * @param gameData        Game data
     * @param moveInformation Move information
     */
    void updateISSMove(String tableName, SkatGameData gameData,
                       MoveInformation moveInformation);

    /**
     * Sets the resigning flag of a player
     *
     * @param tableName Table name
     * @param player    Resigning player
     */
    void setResign(String tableName, Player player);

    /**
     * Shows an invitation message for ISS
     *
     * @param invitor   Invitor
     * @param tableName Table name
     * @return TRUE, if the user accepted the invitation
     */
    boolean showISSTableInvitation(String invitor, String tableName);

    /**
     * Sets the schieben of a player
     *
     * @param tableName Table name
     * @param player    Player position
     */
    void setGeschoben(String tableName, Player player);

    /**
     * Sets the discards skat
     *
     * @param tableName     Table name
     * @param activePlayer  Discarding player
     * @param skatBefore    Skat before discarding
     * @param discardedSkat Skat after discarding
     */
    void setDiscardedSkat(String tableName, Player activePlayer,
                          CardList skatBefore, CardList discardedSkat);

    /**
     * Opens a web page in the browser
     *
     * @param link URL of the web page
     */
    void openWebPage(String link);

    /**
     * Creates a human player object for the GUI
     *
     * @return Human player object that extends {@link AbstractHumanJSkatPlayer}
     */
    AbstractHumanJSkatPlayer getHumanPlayerForGUI();

    /**
     * Sets the active view part for JSkat
     *
     * @param name Name of the view
     */
    void setActiveView(String name);

    /**
     * Shows a messages if an AI player played schwarz during discarding
     *
     * @param playerName     Player name
     * @param discardedCards Discarded cards
     */
    void showAIPlayedSchwarzMessageDiscarding(String playerName,
                                              CardList discardedCards);

    /**
     * Shows a messages if an AI player played schwarz during card play
     *
     * @param playerName Player name
     * @param card       Card
     */
    void showAIPlayedSchwarzMessageCardPlay(String playerName, Card card);
}