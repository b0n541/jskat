package org.jskat.gui.swing.table;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.GameContract;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Context panel for Schieberamsch
 */
class SchieberamschContextPanel extends JPanel {

    JSkatGraphicRepository bitmaps = JSkatGraphicRepository.INSTANCE;
    JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

    private static final String GRAND_HAND = "GRAND_HAND";
    private static final String DISCARD = "DISCARD";

    private static final Logger log = LoggerFactory.getLogger(SchieberamschContextPanel.class);

    private final DiscardPanel discardPanel;
    private final JPanel centerPanel;

    SchieberamschContextPanel(final ActionMap actions, final JSkatUserPanel newUserPanel, final int maxCards) {

        setLayout(LayoutFactory.getMigLayout("fill", "[shrink][grow][shrink]", "fill"));

        final JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        add(blankPanel, "width 25%");

        this.centerPanel = new JPanel(new CardLayout());
        final JPanel grandHandPanel = getGrandHandSchiebeRamschPanel(actions);
        this.centerPanel.add(grandHandPanel, GRAND_HAND);

        discardPanel = new DiscardPanel(actions, 4);
        this.centerPanel.add(discardPanel, DISCARD);

        this.centerPanel.setOpaque(false);
        add(this.centerPanel, "grow");

        add(new SkatSchiebenPanel(actions, discardPanel), "width 25%");

        setOpaque(false);

        resetPanel();
    }

    public JPanel getGrandHandSchiebeRamschPanel(final ActionMap actions) {
        final JPanel result = new JPanel(LayoutFactory.getMigLayout("fill"));

        final JPanel question = new JPanel();
        final JLabel questionIconLabel = new JLabel(new ImageIcon(this.bitmaps.getUserBidBubble()));
        question.add(questionIconLabel);
        final JLabel questionLabel = new JLabel(this.strings.getString("want_play_grand_hand"));
        questionLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        question.add(questionLabel);
        result.add(question, "center, growx, span 2, wrap");

        final JButton grandHandButton = new JButton(actions.get(JSkatAction.PLAY_GRAND_HAND));
        grandHandButton.setIcon(new ImageIcon(this.bitmaps.getIconImage(Icon.OK, IconSize.BIG)));
        grandHandButton.setText(this.strings.getString("yes"));
        grandHandButton.addActionListener(event -> {
            try {
                final GameContract contract = new GameContract(GameType.GRAND).withHand();

                event.setSource(contract);
                // fire event again
                grandHandButton.dispatchEvent(event);
            } catch (final IllegalArgumentException except) {
                log.error(except.getMessage());
            }
        });

        final JButton schieberamschButton = new JButton(actions.get(JSkatAction.PLAY_SCHIEBERAMSCH));
        schieberamschButton.setIcon(new ImageIcon(this.bitmaps.getIconImage(Icon.STOP, IconSize.BIG)));
        schieberamschButton.setText(this.strings.getString("no"));
        schieberamschButton.addActionListener(event -> {
            try {
                showPanel(DISCARD);
            } catch (final IllegalArgumentException except) {
                log.error(except.getMessage());
            }
        });

        final JPanel grandHandPanel = new JPanel();
        grandHandPanel.add(grandHandButton);
        grandHandPanel.setOpaque(false);
        result.add(grandHandPanel, "width 50%");

        final JPanel schieberamschPanel = new JPanel();
        schieberamschPanel.add(schieberamschButton);
        schieberamschPanel.setOpaque(false);
        result.add(schieberamschPanel, "width 50%");

        result.setOpaque(false);

        return result;
    }

    public void resetPanel() {
        discardPanel.resetPanel();
        showPanel(GRAND_HAND);
    }

    void showPanel(final String panelName) {
        ((CardLayout) this.centerPanel.getLayout()).show(this.centerPanel, panelName);
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
