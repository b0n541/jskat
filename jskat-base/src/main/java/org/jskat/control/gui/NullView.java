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
 * Doesn't do anything<br>
 * is needed for simulating games without gui
 */
public class NullView implements JSkatView {
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
    public void showAIPlayedSchwarzMessageDiscarding(final String playerName,
                                                     final CardList discardedCards) {
        // empty method by intent
    }

    @Override
    public void showAIPlayedSchwarzMessageCardPlay(final String playerName, final Card card) {
        // empty method by intent
    }

    @Override
    public void setSkat(String tableName, CardList skat) {
        // empty method by intent
    }
}
