package org.jskat.gui.swing.table;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import org.jskat.gui.javafx.table.DiscardPanel;
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

        discardPanel = new DiscardPanel(actions, 4);

        announcePanel = new GameAnnouncePanel(actions, newUserPanel, discardPanel);
        add(announcePanel, "width 25%");
        discardPanel.setAnnouncePanel(announcePanel);

        final JFXPanel discardPanelContainer = new JFXPanel();
        discardPanelContainer.setOpaque(false);
        Platform.runLater(() -> discardPanelContainer.setScene(new Scene(discardPanel, Color.TRANSPARENT)));
        add(discardPanelContainer, "grow");


        final JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        add(blankPanel, "width 25%");

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
