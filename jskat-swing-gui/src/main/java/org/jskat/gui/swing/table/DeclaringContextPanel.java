package org.jskat.gui.swing.table;

import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;

import javax.swing.*;

/**
 * Context panel for discarding
 */
class DeclaringContextPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DiscardPanel discardPanel;
    private final GameAnnouncePanel announcePanel;

    DeclaringContextPanel(ActionMap actions, JSkatUserPanel newUserPanel) {

        setLayout(LayoutFactory.getMigLayout(
                "fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

        JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        add(blankPanel, "width 25%"); //$NON-NLS-1$

        this.discardPanel = new DiscardPanel(actions, 4);
        add(this.discardPanel, "grow"); //$NON-NLS-1$

        this.announcePanel = new GameAnnouncePanel(actions, newUserPanel,
                this.discardPanel);
        add(this.announcePanel, "width 25%"); //$NON-NLS-1$
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
