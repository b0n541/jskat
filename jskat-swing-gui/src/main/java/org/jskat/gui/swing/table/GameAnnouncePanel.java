package org.jskat.gui.swing.table;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.skatgame.InvalidNumberOfCardsInDiscardedSkatEvent;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Holds widgets for announcing a game
 */
class GameAnnouncePanel extends JPanel {


    private static final Logger log = LoggerFactory.getLogger(GameAnnouncePanel.class);

    JSkatResourceBundle strings;
    JSkatOptions options;

    JRadioButton grandRadioButton;
    JRadioButton clubsRadioButton;
    JRadioButton spadesRadioButton;
    JRadioButton heartsRadioButton;
    JRadioButton diamondsRadioButton;
    JRadioButton nullRadioButton;
    ButtonGroup gameTypeButtonGroup;
    JCheckBox handBox = null;
    JCheckBox ouvertBox = null;
    JCheckBox schneiderBox = null;
    JCheckBox schwarzBox = null;

    DiscardPanel discardPanel;
    JSkatUserPanel userPanel;

    boolean userPickedUpSkat = false;

    /**
     * Constructor
     *
     * @param actions      Action map
     * @param userPanel    User panel
     * @param discardPanel Discard panel
     */
    GameAnnouncePanel(final ActionMap actions, final JSkatUserPanel userPanel,
                      final DiscardPanel discardPanel) {

        strings = JSkatResourceBundle.INSTANCE;
        this.userPanel = userPanel;
        this.discardPanel = discardPanel;

        initPanel(actions);
    }

    private void initPanel(final ActionMap actions) {

        setLayout(LayoutFactory.getMigLayout("fill"));

        final JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill"));

        handBox = new JCheckBox(strings.getString("hand"));
        handBox.setEnabled(false);
        ouvertBox = createOuvertBox();
        schneiderBox = new JCheckBox(strings.getString("schneider"));
        schwarzBox = createSchwarzBox();

        final ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // FIXME (jan 28.11.2010) send sorting game type to JSkatMaster
                // --> more view components can benefit from this
                final GameType gameType = getSelectedGameType();

                if (gameType != null) {
                    userPanel.setSortGameType(gameType);

                    if (userPickedUpSkat) {
                        ouvertBox.setEnabled(gameType == GameType.NULL);
                    } else {
                        ouvertBox.setEnabled(true);
                        if (gameType != GameType.NULL) {
                            schneiderBox.setEnabled(true);
                            schwarzBox.setEnabled(true);
                            if (ouvertBox.isSelected()) {
                                schneiderBox.setSelected(true);
                                schwarzBox.setSelected(true);
                            }
                        } else {
                            schneiderBox.setEnabled(false);
                            schneiderBox.setSelected(false);
                            schwarzBox.setEnabled(false);
                            schwarzBox.setSelected(false);
                        }
                    }
                }
            }
        };

        gameTypeButtonGroup = new ButtonGroup();
        grandRadioButton = createRadioButton(GameType.GRAND, actionListener, gameTypeButtonGroup);
        clubsRadioButton = createRadioButton(GameType.CLUBS, actionListener, gameTypeButtonGroup);
        spadesRadioButton = createRadioButton(GameType.SPADES, actionListener, gameTypeButtonGroup);
        heartsRadioButton = createRadioButton(GameType.HEARTS, actionListener, gameTypeButtonGroup);
        diamondsRadioButton = createRadioButton(GameType.DIAMONDS, actionListener, gameTypeButtonGroup);
        nullRadioButton = createRadioButton(GameType.NULL, actionListener, gameTypeButtonGroup);

        panel.add(new JLabel(strings.getString("game")), "span 2, wrap");

        panel.add(clubsRadioButton);
        panel.add(spadesRadioButton, "wrap");
        panel.add(heartsRadioButton);
        panel.add(diamondsRadioButton, "wrap");
        panel.add(grandRadioButton);
        panel.add(nullRadioButton, "wrap");

        panel.add(new JLabel(strings.getString("win_levels")), "span 2, wrap");

        panel.add(handBox);
        panel.add(ouvertBox, "wrap");
        panel.add(schneiderBox);
        panel.add(schwarzBox, "wrap");

        final JButton announceButton = new JButton(actions.get(JSkatAction.ANNOUNCE_GAME));
        announceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {

                if (getSelectedGameType() != null) {

                    try {
                        final GameAnnouncement announcement = getGameAnnouncement();
                        if (announcement == null) {
                            return;
                        }
                        e.setSource(announcement);
                    } catch (final IllegalArgumentException except) {
                        log.error(except.getMessage());
                    }
                } else {
                    e.setSource(null);
                }

                // fire event again
                announceButton.dispatchEvent(e);
            }

            private GameAnnouncement getGameAnnouncement() {

                final var gameType = getSelectedGameType();
                var contract = new GameContract(gameType);

                if (discardPanel.isUserLookedIntoSkat()) {

                    final CardList discardedCards = discardPanel.getDiscardedCards();

                    if (discardedCards.size() != 2) {

                        JSkatEventBus.INSTANCE.post(new InvalidNumberOfCardsInDiscardedSkatEvent());
                        return null;
                    }
                    if (GameType.NULL == gameType && ouvertBox.isSelected()) {
                        contract = contract.withOuvert(userPanel.cardPanel.getCards());
                    }

                    return new GameAnnouncement(contract, discardedCards);

                } else {

                    if (handBox.isSelected()) {
                        contract = contract.withHand();
                    }
                    if (schneiderBox.isSelected()) {
                        contract = contract.withSchneider();
                    }
                    if (schwarzBox.isSelected()) {
                        contract = contract.withSchwarz();
                    }
                    if (ouvertBox.isSelected()) {
                        contract = contract.withOuvert(userPanel.cardPanel.getCards());
                    }

                    return new GameAnnouncement(contract, CardList.empty());
                }
            }

        });
        panel.add(announceButton, "center, span 2");

        add(panel, "center");

        setOpaque(false);

        resetPanel();
    }

    private JRadioButton createRadioButton(final GameType gameType, final ActionListener actionListener, final ButtonGroup buttonGroup) {
        final JRadioButton radioButton = new JRadioButton(strings.getGameType(gameType));
        radioButton.setActionCommand(gameType.toString());
        radioButton.addActionListener(actionListener);
        buttonGroup.add(radioButton);
        return radioButton;
    }

    GameType getSelectedGameType() {
        for (final JRadioButton radioButton : new JRadioButton[]{grandRadioButton,
                spadesRadioButton,
                clubsRadioButton,
                diamondsRadioButton,
                heartsRadioButton,
                nullRadioButton}) {
            if (radioButton != null && radioButton.isSelected()) {
                return GameType.valueOf(radioButton.getActionCommand());
            }
        }
        return null;
    }

    private JCheckBox createOuvertBox() {
        final JCheckBox result = new JCheckBox(strings.getString("ouvert"));

        result.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent arg0) {
                if (result.isSelected() && handBox.isSelected()
                        && getSelectedGameType() != null) {
                    // hand ouvert
                    if (GameType.NULL != getSelectedGameType()) {
                        schneiderBox.setSelected(true);
                        schwarzBox.setSelected(true);
                    }
                }
            }
        });

        return result;
    }

    private JCheckBox createSchwarzBox() {
        final JCheckBox result = new JCheckBox(strings.getString("schwarz"));

        result.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (result.isSelected()) {
                    schneiderBox.setSelected(true);
                }
            }
        });

        return result;
    }

    void resetPanel() {
        gameTypeButtonGroup.clearSelection();
        handBox.setSelected(true);
        ouvertBox.setSelected(false);
        schneiderBox.setSelected(false);
        schwarzBox.setSelected(false);
    }

    void setUserPickedUpSkat(final boolean isUserPickedUpSkat) {

        userPickedUpSkat = isUserPickedUpSkat;

        if (isUserPickedUpSkat) {
            handBox.setSelected(false);
            ouvertBox.setEnabled(GameType.NULL.equals(getSelectedGameType()));
            schneiderBox.setEnabled(false);
            schwarzBox.setEnabled(false);
        } else {
            handBox.setSelected(true);
            ouvertBox.setEnabled(true);
            schneiderBox.setEnabled(true);
            schwarzBox.setEnabled(true);
        }
    }
}
