package org.jskat.gui.swing.table;

import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;

import javax.swing.*;

/**
 * Context panel for discarding
 */
class DeclaringContextPanel extends JPanel {
    private final DiscardPanel discardPanel;
    private final GameAnnouncePanel announcePanel;

    DeclaringContextPanel(final ActionMap actions, final JSkatUserPanel newUserPanel) {

        setLayout(LayoutFactory.getMigLayout("fill", "[shrink][grow][shrink]", "fill"));

        final JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        add(blankPanel, "width 25%");

        discardPanel = new DiscardPanel(actions, 4);
        add(discardPanel, "grow");

        announcePanel = new GameAnnouncePanel(actions, newUserPanel, discardPanel);
        add(announcePanel, "width 25%");
        discardPanel.setAnnouncePanel(announcePanel);

        setOpaque(false);

        resetPanel();
    }

    public void resetPanel() {
        discardPanel.resetPanel();
        announcePanel.resetPanel();
    }

    public void removeCard(final Card card) {
        discardPanel.removeCard(card);
    }

    public boolean isHandFull() {
        return discardPanel.isHandFull();
    }

    public void addCard(final Card card) {
        discardPanel.addCard(card);
    }

    public void setSkat(final CardList skat) {
        discardPanel.setSkat(skat);
    }
}