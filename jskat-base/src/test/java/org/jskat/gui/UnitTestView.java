package org.jskat.gui;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.control.gui.JSkatView;
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.SkatGameData;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.MoveInformation;
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
        tables = new ArrayList<>();
        JSkatEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void handle(final TableCreatedEvent event) {
        tables.add(event.tableName());
    }

    /**
     * Resets the view
     */
    public void reset() {
        tables.clear();
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
    public void appendISSChatMessage(final ChatMessageType messageType,
                                     final ChatMessage message) {
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
