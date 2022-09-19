package org.jskat.control.gui;

import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatSeriesData.SeriesState;
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
 * Doesn't do anything<br>
 * is needed for simulating games without gui
 */
public class NullView implements JSkatView {

    /**
     * {@inheritDoc}
     */
    @Override
    public void showISSLogin() {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startGame(@SuppressWarnings("unused") final String tableName) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showMessage(@SuppressWarnings("unused") final String title,
                            @SuppressWarnings("unused") final String message) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showErrorMessage(
            @SuppressWarnings("unused") final String title,
            @SuppressWarnings("unused") final String message) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSLobbyPlayerList(
            @SuppressWarnings("unused") final String playerName,
            @SuppressWarnings("unused") final String playerLanguage1,
            @SuppressWarnings("unused") final long gamesPlayed,
            @SuppressWarnings("unused") final double strength) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromISSLobbyPlayerList(
            @SuppressWarnings("unused") final String playerName) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromISSLobbyTableList(
            @SuppressWarnings("unused") final String tableName) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSLobbyTableList(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final int maxPlayers,
            @SuppressWarnings("unused") final long gamesPlayed,
            @SuppressWarnings("unused") final String player1,
            @SuppressWarnings("unused") final String player2,
            @SuppressWarnings("unused") final String player3) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendISSChatMessage(
            @SuppressWarnings("unused") final ChatMessageType messageType,
            @SuppressWarnings("unused") final ChatMessage message) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSTable(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final TablePanelStatus status) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSTable(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final String playerName,
            @SuppressWarnings("unused") final GameStartInformation status) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNewTableName(
            @SuppressWarnings("unused") final int localTablesCreated) {
        // empty method by intent
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSMove(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final SkatGameData gameData,
            @SuppressWarnings("unused") final MoveInformation moveInformation) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getPlayerForInvitation(
            @SuppressWarnings("unused") final Set<String> playerNames) {
        // empty method by intent
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeriesState(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final SeriesState state) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBidValueToMake(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final int bidValue) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBidValueToHold(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final int bidValue) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showISSTableInvitation(
            @SuppressWarnings("unused") final String invitor,
            @SuppressWarnings("unused") final String tableName) {
        // empty method by intent
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showCardNotAllowedMessage(
            @SuppressWarnings("unused") final Card card) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResign(@SuppressWarnings("unused") final String tableName,
                          @SuppressWarnings("unused") final Player player) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGeschoben(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final Player player) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDiscardedSkat(
            @SuppressWarnings("unused") final String tableName,
            @SuppressWarnings("unused") final Player activePlayer,
            @SuppressWarnings("unused") final CardList skatBefore,
            @SuppressWarnings("unused") final CardList discardedSkat) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openWebPage(@SuppressWarnings("unused") final String link) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractHumanJSkatPlayer getHumanPlayerForGUI() {
        // empty method by intent
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveView(final String name) {
        // empty method by intent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showAIPlayedSchwarzMessageDiscarding(final String playerName,
                                                     final CardList discardedCards) {
        // empty method by intent
    }

    @Override
    public void showAIPlayedSchwarzMessageCardPlay(final String playerName, final Card card) {
        // empty method by intent
    }
}
