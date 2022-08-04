package org.jskat.gui.swing.table;

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


    private static final Logger log = LoggerFactory
            .getLogger(SkatSchiebenPanel.class);

    JSkatResourceBundle strings;
    JSkatOptions options;

    DiscardPanel discardPanel;

    /**
     * Constructor
     *
     * @param actions            Action map
     * @param discardPanel       Discard panel
     * @param showAnnounceButton TRUE, if the announce button should be shown
     */
    SkatSchiebenPanel(final ActionMap actions, final DiscardPanel discardPanel) {

        this.strings = JSkatResourceBundle.INSTANCE;
        this.discardPanel = discardPanel;

        initPanel(actions);
    }

    private void initPanel(final ActionMap actions) {

        this.setLayout(LayoutFactory.getMigLayout("fill"));
        setOpaque(false);

        JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill"));
        panel.setOpaque(false);

        final JButton schiebenButton = new JButton(
                actions.get(JSkatAction.SCHIEBEN));
        schiebenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    CardList discardedCards = getDiscardedCards();
                    if (discardedCards == null) {
                        return; // no valid announcement
                    }

                    e.setSource(discardedCards);
                    // fire event again
                    schiebenButton.dispatchEvent(e);
                } catch (IllegalArgumentException except) {
                    log.error(except.getMessage());
                }
            }

            private CardList getDiscardedCards() {
                if (SkatSchiebenPanel.this.discardPanel.isUserLookedIntoSkat()) {
                    CardList discardedCards = SkatSchiebenPanel.this.discardPanel.getDiscardedCards();
                    if (discardedCards.size() != 2) {
                        JOptionPane
                                .showMessageDialog(
                                        SkatSchiebenPanel.this,
                                        SkatSchiebenPanel.this.strings.getString("invalid_number_of_cards_in_skat_message"),
                                        SkatSchiebenPanel.this.strings.getString("invalid_number_of_cards_in_skat_title"),
                                        JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    if (!JSkatOptions.instance().isSchieberamschJacksInSkat()
                            && (discardedCards.get(0).getRank() == Rank.JACK || discardedCards
                            .get(1).getRank() == Rank.JACK)) {
                        JOptionPane
                                .showMessageDialog(
                                        SkatSchiebenPanel.this,
                                        // FIXME: should not be checked in GUI
                                        // code
                                        SkatSchiebenPanel.this.strings.getString("no_jacks_allowed_in_schieberamsch_skat_message"),
                                        SkatSchiebenPanel.this.strings.getString("no_jacks_allowed_in_schieberamsch_skat_title"),
                                        JOptionPane.ERROR_MESSAGE);
                        return null;
                    }

                    return discardedCards;
                }
                return new CardList();
            }

        });
        panel.add(schiebenButton, "center");

        this.add(panel, "center");
    }
}
