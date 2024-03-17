package org.jskat.gui.swing.table;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.skatgame.InvalidNumberOfCardsInDiscardedSkatEvent;
import org.jskat.control.event.skatgame.NoJacksAllowedInDiscardedSkatEvent;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.CardList;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Rank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Holds widgets for announcing a game
 */
class SkatSchiebenPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(SkatSchiebenPanel.class);

    JSkatResourceBundle strings;
    JSkatOptions options;

    DiscardPanel discardPanel;

    /**
     * Constructor
     *
     * @param actions      Action map
     * @param discardPanel Discard panel
     */
    SkatSchiebenPanel(final ActionMap actions, final DiscardPanel discardPanel) {

        strings = JSkatResourceBundle.INSTANCE;
        this.discardPanel = discardPanel;

        initPanel(actions);
    }

    private void initPanel(final ActionMap actions) {

        setLayout(LayoutFactory.getMigLayout("fill"));
        setOpaque(false);

        final JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill"));
        panel.setOpaque(false);

        final JButton schiebenButton = new JButton(actions.get(JSkatAction.SCHIEBEN));
        schiebenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                e.setSource(null);
                try {
                    final CardList discardedCards = getDiscardedCards();
                    if (discardedCards == null) {
                        return; // no valid announcement
                    }

                    e.setSource(discardedCards);
                } catch (final IllegalArgumentException except) {
                    log.error(except.getMessage());
                }

                // fire event again
                schiebenButton.dispatchEvent(e);
            }

            private CardList getDiscardedCards() {
                if (discardPanel.isUserLookedIntoSkat()) {
                    final CardList discardedCards = discardPanel.getDiscardedCards();
                    if (discardedCards.size() != 2) {
                        JSkatEventBus.INSTANCE.post(new InvalidNumberOfCardsInDiscardedSkatEvent());
                        return null;
                    }
                    if (!JSkatOptions.instance().isSchieberamschJacksInSkat()
                            && (discardedCards.get(0).getRank() == Rank.JACK || discardedCards.get(1).getRank() == Rank.JACK)) {
                        JSkatEventBus.INSTANCE.post(new NoJacksAllowedInDiscardedSkatEvent());
                        return null;
                    }

                    return discardedCards;
                }
                return new CardList();
            }

        });
        panel.add(schiebenButton, "center");

        add(panel, "center");
    }
}
