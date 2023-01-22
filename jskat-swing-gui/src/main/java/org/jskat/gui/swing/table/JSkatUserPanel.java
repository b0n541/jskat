package org.jskat.gui.swing.table;

import org.jskat.data.SkatGameData.GameState;

import javax.swing.*;

/**
 * Panel for showing information about the user
 */
public class JSkatUserPanel extends AbstractHandPanel {

    private GameState gameState;

    /**
     * @param actions        Actions
     * @param maxCards       Maximum number of cards
     * @param showIssWidgets TRUE, if ISS widgets should be shown
     * @see AbstractHandPanel#AbstractHandPanel(ActionMap, int, boolean)
     */
    public JSkatUserPanel(final ActionMap actions,
                          final int maxCards,
                          final boolean showIssWidgets) {

        super(actions, maxCards, showIssWidgets);
        this.showCards();
    }

    void setGameState(final GameState newGameState) {
        this.gameState = newGameState;
    }

    GameState getGameState() {
        return this.gameState;
    }

    @Override
    void hideCards() {
        // ignore hiding of cards for user panel
    }
}
