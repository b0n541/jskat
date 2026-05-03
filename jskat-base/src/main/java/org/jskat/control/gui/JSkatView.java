package org.jskat.control.gui;

import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.data.SkatGameData;
import org.jskat.data.iss.MoveInformation;
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

//    /**
//     * Starts a new game
//     *
//     * @param tableName Table name
//     */
//    void startGame(String tableName);

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

    /**
     * Sets the skat cards
     *
     * @param tableName Table name
     * @param skat      Skat cards
     */
    void setSkat(String tableName, CardList skat);
}
