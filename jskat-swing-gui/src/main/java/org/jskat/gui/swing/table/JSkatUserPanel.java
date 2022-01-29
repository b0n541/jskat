package org.jskat.gui.swing.table;

import org.jskat.data.SkatGameData.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Panel for showing informations about the user
 */
public class JSkatUserPanel extends AbstractHandPanel {

    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(JSkatUserPanel.class);

    private ClickableCardPanel lastClickedCardPanel;
    private GameState gameState;

    /**
     * @param actions        Actions
     * @param maxCards       Maximum number of cards
     * @param showIssWidgets TRUE, if ISS widgets should be shown
     * @see AbstractHandPanel#AbstractHandPanel(ActionMap, int, boolean)
     */
    public JSkatUserPanel(final ActionMap actions, final int maxCards,
                          final boolean showIssWidgets) {

        super(actions, maxCards, showIssWidgets);
        this.showCards();
    }

    ClickableCardPanel getLastClickedCardPanel() {

        return this.lastClickedCardPanel;
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
