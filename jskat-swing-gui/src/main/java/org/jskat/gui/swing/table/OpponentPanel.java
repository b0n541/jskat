package org.jskat.gui.swing.table;

import javax.swing.*;

/**
 * Panel for showing informations about opponents.
 */
public class OpponentPanel extends AbstractHandPanel {

    private static final long serialVersionUID = 1L;

    /**
     * @param actions        Actions
     * @param maxCards       Maximum number of cards
     * @param showIssWidgets TRUE, if ISS widgets should be shown
     * @see AbstractHandPanel#AbstractHandPanel(ActionMap, int, boolean)
     */
    public OpponentPanel(ActionMap actions, int maxCards, boolean showIssWidgets) {

        super(actions, maxCards, showIssWidgets);
    }
}
