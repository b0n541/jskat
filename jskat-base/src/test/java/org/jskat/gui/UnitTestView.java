package org.jskat.gui;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.control.gui.JSkatView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Helper class that represent a GUI view during unit tests
 */
public class UnitTestView implements JSkatView {

    public List<String> tables;

    /**
     * Constructor
     */
    public UnitTestView() {
        this.tables = new ArrayList<String>();
        JSkatEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void handle(final TableCreatedEvent event) {
        this.tables.add(event.tableName);
    }

    /**
     * Resets the view
     */
    public void reset() {
        this.tables.clear();
    }

    @Override
    public String getNewTableName(final int localTablesCreated) {

        return "UnitTestTable " + (localTablesCreated + 1);
    }

    @Override
    public void startGame(final String tableName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showISSLogin() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getPlayerForInvitation(final Set<String> playerNames) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void showMessage(final String title, final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showErrorMessage(final String title, final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showCardNotAllowedMessage(final Card card) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBidValueToMake(final String tableName, final int bidValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBidValueToHold(final String tableName, final int bidValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateISSLobbyPlayerList(final String playerName,
                                         final String language, final long gamesPlayed, final double strength) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeFromISSLobbyPlayerList(final String playerName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateISSLobbyTableList(final String tableName,
                                        final int maxPlayers, final long gamesPlayed, final String player1,
                                        final String player2, final String player3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeFromISSLobbyTableList(final String tableName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void appendISSChatMessage(final ChatMessageType messageType,
                                     final ChatMessage message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateISSTable(final String tableName,
                               final TablePanelStatus status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateISSTable(final String tableName, final String loginName,
                               final GameStartInformation status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateISSMove(final String tableName,
                              final SkatGameData gameData, final MoveInformation moveInformation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setResign(final String tableName, final Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean showISSTableInvitation(final String invitor,
                                          final String tableName) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setGeschoben(final String tableName, final Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDiscardedSkat(final String tableName,
                                 final Player activePlayer, final CardList skatBefore,
                                 final CardList discardedSkat) {
        // TODO Auto-generated method stub
    }

    @Override
    public void openWebPage(final String link) {
        // TODO Auto-generated method stub
    }

    @Override
    public AbstractHumanJSkatPlayer getHumanPlayerForGUI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setActiveView(final String name) {
        // TODO Auto-generated method stub
    }

    @Override
    public void showAIPlayedSchwarzMessageDiscarding(final String playerName,
                                                     final CardList discardedCard) {
        // TODO Auto-generated method stub
    }

    @Override
    public void showAIPlayedSchwarzMessageCardPlay(final String playerName, final Card card) {
        // TODO Auto-generated method stub
    }
}
