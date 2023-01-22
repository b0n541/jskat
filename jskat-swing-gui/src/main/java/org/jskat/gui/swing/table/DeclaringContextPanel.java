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

    DeclaringContextPanel(ActionMap actions, JSkatUserPanel newUserPanel) {

        setLayout(LayoutFactory.getMigLayout("fill", "[shrink][grow][shrink]", "fill"));

        JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        add(blankPanel, "width 25%");

        this.discardPanel = new DiscardPanel(actions, 4);
        add(this.discardPanel, "grow");

        this.announcePanel = new GameAnnouncePanel(actions, newUserPanel,
                this.discardPanel);
        add(this.announcePanel, "width 25%");
        this.discardPanel.setAnnouncePanel(this.announcePanel);

        setOpaque(false);

        resetPanel();
    }

    public void resetPanel() {

        this.discardPanel.resetPanel();
        this.announcePanel.resetPanel();
    }

    public void removeCard(Card card) {
        this.discardPanel.removeCard(card);
    }

    public boolean isHandFull() {
        return this.discardPanel.isHandFull();
    }

    public void addCard(Card card) {
        this.discardPanel.addCard(card);
    }

    public void setSkat(CardList skat) {
        this.discardPanel.setSkat(skat);
    }
}
