package org.jskat.gui.swing;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.general.HideToolbarCommand;
import org.jskat.control.command.general.ShowToolbarCommand;
import org.jskat.control.gui.img.CardSet;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.Option;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.SkatTableOptions.RamschSkatOwner;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.data.SkatTableOptions.SavePath;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Options dialog for JSkat
 */
public class JSkatOptionsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(JSkatOptionsDialog.class);

    private final JSkatResourceBundle strings;
    private final JSkatOptions options;

    private final Component parent;

    // general options
    private JCheckBox showTipsAtStartUp;
    private JCheckBox checkForNewVersion;
    private JCheckBox hideToolbar;
    private JComboBox language;
    private JComboBox cardSet;
    private JRadioButton savePathUserHome;
    private JRadioButton savePathWorkingDirectory;
    private JSlider waitTimeAfterTrick;

    // rule options
    private JRadioButton ruleSetISPA;
    private JRadioButton ruleSetPub;
    private JButton resetPubRulesButton;
    private JCheckBox playContra;
    private JCheckBox contraAfterBid18;
    private JCheckBox playBock;
    private JCheckBox playRamsch;
    private JCheckBox playRevolution;
    private JCheckBox bockEventAllPlayersPassed;
    private JCheckBox bockEventLostGrand;
    private JCheckBox bockEventLostWith60;
    private JCheckBox bockEventLostAfterContra;
    private JCheckBox bockEventContraReAnnounced;
    private JCheckBox bockEventPlayerHasX00Points;
    private JCheckBox schiebeRamsch;
    private JCheckBox schiebeRamschJacksInSkat;
    private JCheckBox ramschEventNoBid;
    private JCheckBox ramschEventBockRamsch;
    private ButtonGroup ramschSkatOwner;
    private JRadioButton ramschSkatLastTrick;
    private JRadioButton ramschSkatLoser;

    private JTextField issAddress;
    private JTextField issPort;

    private final Action okAction = new AbstractAction("OK") {
        @Override
        public void actionPerformed(final ActionEvent e) {
            JSkatOptionsDialog.this.options
                    .setShowTipsAtStartUp(JSkatOptionsDialog.this.showTipsAtStartUp.isSelected());
            JSkatOptionsDialog.this.options
                    .setCheckForNewVersionAtStartUp(JSkatOptionsDialog.this.checkForNewVersion.isSelected());
            JSkatOptionsDialog.this.options.setHideToolbar(JSkatOptionsDialog.this.hideToolbar.isSelected());
            JSkatOptionsDialog.this.options
                    .setLanguage((SupportedLanguage) JSkatOptionsDialog.this.language.getSelectedItem());
            JSkatOptionsDialog.this.options.setCardSet(getSelectedCardSet());

            if (JSkatOptionsDialog.this.savePathUserHome.isSelected()) {
                JSkatOptionsDialog.this.options.setSavePath(SavePath.USER_HOME);
            } else if (JSkatOptionsDialog.this.savePathWorkingDirectory.isSelected()) {
                JSkatOptionsDialog.this.options.setSavePath(SavePath.WORKING_DIRECTORY);
            }

            JSkatOptionsDialog.this.options
                    .setWaitTimeAfterTrick(JSkatOptionsDialog.this.waitTimeAfterTrick.getValue());
            JSkatOptionsDialog.this.options.setIssAddress(JSkatOptionsDialog.this.issAddress.getText());
            JSkatOptionsDialog.this.options.setIssPort(Integer.valueOf(JSkatOptionsDialog.this.issPort.getText()));

            if (JSkatOptionsDialog.this.ruleSetISPA.isSelected()) {
                JSkatOptionsDialog.this.options.setRules(RuleSet.ISPA);
            } else if (JSkatOptionsDialog.this.ruleSetPub.isSelected()) {
                JSkatOptionsDialog.this.options.setRules(RuleSet.PUB);
            }

            JSkatOptionsDialog.this.options.setRamschEventNoBid(JSkatOptionsDialog.this.ramschEventNoBid.isSelected());
            JSkatOptionsDialog.this.options
                    .setBockEventContraReCalled(JSkatOptionsDialog.this.bockEventContraReAnnounced.isSelected());
            JSkatOptionsDialog.this.options
                    .setBockEventLostGrand(JSkatOptionsDialog.this.bockEventLostGrand.isSelected());
            JSkatOptionsDialog.this.options
                    .setBockEventLostAfterContra(JSkatOptionsDialog.this.bockEventLostAfterContra.isSelected());
            JSkatOptionsDialog.this.options
                    .setBockEventLostWith60(JSkatOptionsDialog.this.bockEventLostWith60.isSelected());
            JSkatOptionsDialog.this.options.setPlayContra(JSkatOptionsDialog.this.playContra.isSelected());
            JSkatOptionsDialog.this.options.setContraAfterBid18(JSkatOptionsDialog.this.contraAfterBid18.isSelected());
            JSkatOptionsDialog.this.options.setPlayRamsch(JSkatOptionsDialog.this.playRamsch.isSelected());
            JSkatOptionsDialog.this.options.setPlayBock(JSkatOptionsDialog.this.playBock.isSelected());
            JSkatOptionsDialog.this.options.setPlayRevolution(JSkatOptionsDialog.this.playRevolution.isSelected());
            JSkatOptionsDialog.this.options.setSchieberRamsch(JSkatOptionsDialog.this.schiebeRamsch.isSelected());
            JSkatOptionsDialog.this.options
                    .setSchieberRamschJacksInSkat(JSkatOptionsDialog.this.schiebeRamschJacksInSkat.isSelected());
            JSkatOptionsDialog.this.options.setRamschSkatOwner(
                    JSkatOptionsDialog.this.ramschSkatLastTrick.isSelected() ? RamschSkatOwner.LAST_TRICK
                            : RamschSkatOwner.LOSER);

            JSkatOptionsDialog.this.options.saveJSkatProperties();
            refreshCardSet();

            setVisible(false);
        }
    };

    private final Action cancelAction = new AbstractAction("CANCEL") {
        @Override
        public void actionPerformed(final ActionEvent e) {
            setVisible(false);
        }
    };

    final ChangeListener ruleButtonChangeListener = new ChangeListener() {

        @Override
        public void stateChanged(final ChangeEvent e) {
            if (JSkatOptionsDialog.this.ruleSetISPA.isSelected()) {
                activatePubRules(false);
            }
            if (JSkatOptionsDialog.this.ruleSetPub.isSelected()) {
                activatePubRules(true);
            }
        }
    };
    private JLabel bockEventLabel;
    private JLabel ramschEventLabel;
    private JLabel ramschSkatLabel;

    /**
     * Constructor
     *
     * @param parent Parent component of the options dialog
     */
    public JSkatOptionsDialog(final Component parent) {

        strings = JSkatResourceBundle.INSTANCE;
        this.options = JSkatOptions.instance();
        this.parent = parent;

        initGUI();
    }

    private void initGUI() {

        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        setTitle(strings.getString("preferences"));

        final JPanel root = new JPanel(LayoutFactory.getMigLayout());

        final JTabbedPane prefTabs = new JTabbedPane();

        final JPanel commonTab = getCommonPanel();
        log.debug("Common tab: " + commonTab.getPreferredSize().toString());
        prefTabs.addTab(strings.getString("common_options"), commonTab);

        final JPanel cardSetTab = getCardSetSelectionPanel();
        log.debug("Card set tab: " + cardSetTab.getPreferredSize().toString());
        prefTabs.addTab(strings.getString("cardset_options"), cardSetTab);

        final JPanel skatRulesTab = getSkatRulesPanel();
        log.debug("Skat rules tab: " + skatRulesTab.getPreferredSize().toString());
        prefTabs.addTab(strings.getString("skat_rules"), skatRulesTab);

        final JPanel issTab = getIssPanel();
        log.debug("ISS tab: " + issTab.getPreferredSize().toString());
        prefTabs.addTab(strings.getString("iss"), issTab);

        root.add(prefTabs, "wrap");

        final JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout());
        final JButton start = new JButton();
        start.setAction(this.okAction);
        start.setText("OK");
        buttonPanel.add(start);
        final JButton cancel = new JButton();
        cancel.setAction(this.cancelAction);
        cancel.setText(strings.getString("cancel"));
        buttonPanel.add(cancel);

        root.add(buttonPanel, "center");
        root.validate();

        final InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap am = root.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
        am.put("OK", this.okAction);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
        am.put("CANCEL", this.cancelAction);

        setContentPane(root);
        validate();
        pack();
    }

    private JPanel getCardSetSelectionPanel() {
        JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "", "[shrink][grow]"));

        panel.add(new JLabel(strings.getString("card_face")));
        panel.add(getCardSetPanel(), "growx, wrap");

        panel.add(getCardPanel(), "span 2, grow");

        panel.validate();

        return panel;
    }

    private CardPanel getCardPanel() {
        CardPanel cardPanel = new CardPanel(1.0, false);

        for (Card card : CardDeck.getAllCards()) {
            cardPanel.addCard(card);
        }
        cardPanel.setSortType(GameType.GRAND);
        cardPanel.setPreferredSize(new Dimension(600, 100));
        return cardPanel;
    }

    private JPanel getIssPanel() {
        final JPanel issPanel = new JPanel(LayoutFactory.getMigLayout());

        issPanel.add(new JLabel(strings.getString("iss_address")), "shrinky");
        issPanel.add(getIssAddressPanel(), "shrinky, wrap");
        issPanel.add(new JLabel(strings.getString("iss_port")), "shrinky");
        issPanel.add(getIssPortPanel(), "shrinky, wrap");

        issPanel.validate();

        return issPanel;
    }

    private JPanel getSavePathPanel() {

        final JPanel savePathPanel = new JPanel(LayoutFactory.getMigLayout());

        final ButtonGroup savePathGroup = new ButtonGroup();
        this.savePathUserHome = new JRadioButton(strings.getString("user_home"));
        savePathGroup.add(this.savePathUserHome);
        this.savePathWorkingDirectory = new JRadioButton(strings.getString("working_directory"));
        savePathGroup.add(this.savePathWorkingDirectory);

        savePathPanel.add(this.savePathUserHome); // $NON-NLS-1$
        savePathPanel.add(this.savePathWorkingDirectory, "wrap");

        return savePathPanel;
    }

    private JPanel getIssAddressPanel() {

        this.issAddress = new JTextField(20);
        final JPanel issAddressPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "shrink"));
        issAddressPanel.add(this.issAddress);

        return issAddressPanel;
    }

    private JPanel getIssPortPanel() {

        this.issPort = new JTextField(20);
        final JPanel issPortPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "shrink"));
        issPortPanel.add(this.issPort);
        return issPortPanel;
    }

    private JPanel getCardSetPanel() {

        this.cardSet = new JComboBox(CardSet.values());
        this.cardSet.setSelectedIndex(0);
        this.cardSet.setRenderer(new CardSetComboBoxRenderer());

        this.cardSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSkatOptionsDialog.this.options.setCardSet((CardSet) JSkatOptionsDialog.this.cardSet.getSelectedItem());
                refreshCardSet();
            }
        });

        final JPanel cardSetPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        cardSetPanel.add(this.cardSet);
        return cardSetPanel;
    }

    private JPanel getShowTipsPanel() {
        this.showTipsAtStartUp = new JCheckBox(strings.getString("show_tips_at_startup"));
        final JPanel showTipsPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        showTipsPanel.add(this.showTipsAtStartUp);
        return showTipsPanel;
    }

    private JPanel getCheckVersionPanel() {
        this.checkForNewVersion = new JCheckBox(strings.getString("check_for_new_version_at_startup"));
        final JPanel checkVersionPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        checkVersionPanel.add(this.checkForNewVersion);
        return checkVersionPanel;
    }

    private JPanel getHideToolbarPanel() {
        hideToolbar = new JCheckBox(strings.getString("hide_toolbar"));
        hideToolbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hideToolbar.isSelected()) {
                    JSkatEventBus.INSTANCE.post(new HideToolbarCommand());
                } else {
                    JSkatEventBus.INSTANCE.post(new ShowToolbarCommand());
                }
            }
        });

        final JPanel hideToolbarPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        hideToolbarPanel.add(hideToolbar);
        return hideToolbarPanel;
    }

    private JPanel getLanguagePanel() {
        this.language = new JComboBox(SupportedLanguage.values());
        this.language.setRenderer(new LanguageComboBoxRenderer());
        final JPanel languagePanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        languagePanel.add(this.language);
        return languagePanel;
    }

    private JPanel getCommonPanel() {

        final JPanel commonPanel = new JPanel(LayoutFactory.getMigLayout());

        commonPanel.add(new JLabel(strings.getString("show_tips")));
        commonPanel.add(getShowTipsPanel(), "wrap");

        commonPanel.add(new JLabel(strings.getString("check_for_new_version")));
        commonPanel.add(getCheckVersionPanel(), "wrap");

        commonPanel.add(new JLabel(strings.getString("toolbar")));
        commonPanel.add(getHideToolbarPanel(), "wrap");

        commonPanel.add(new JLabel(strings.getString("language")));
        commonPanel.add(getLanguagePanel(), "wrap");

        commonPanel.add(new JLabel(strings.getString("save_path")));
        commonPanel.add(getSavePathPanel(), "wrap");

        commonPanel.add(new JLabel(strings.getString("wait_time_after_trick")));
        commonPanel.add(getWaitTimeAfterTrickPanel(), "wrap");

        commonPanel.validate();

        return commonPanel;
    }

    private JPanel getWaitTimeAfterTrickPanel() {
        waitTimeAfterTrick = new JSlider(0, 10);
        waitTimeAfterTrick.setMajorTickSpacing(5);
        waitTimeAfterTrick.setMinorTickSpacing(1);
        waitTimeAfterTrick.setPaintTicks(true);
        waitTimeAfterTrick.setPaintLabels(true);
        final JPanel waitTimeAfterTrickPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        waitTimeAfterTrickPanel.add(waitTimeAfterTrick);
        waitTimeAfterTrickPanel.add(new JLabel(strings.getString("seconds")));
        return waitTimeAfterTrickPanel;
    }

    private JPanel getSkatRulesPanel() {

        log.debug("Loaded rules: " + this.options.getRules());

        final JPanel rulesPanel = new JPanel(LayoutFactory.getMigLayout());

        final ButtonGroup ruleSetGroup = new ButtonGroup();
        this.ruleSetISPA = new JRadioButton(strings.getString("ispa_rules"));
        this.ruleSetISPA.addChangeListener(this.ruleButtonChangeListener);
        ruleSetGroup.add(this.ruleSetISPA);
        this.ruleSetPub = new JRadioButton(strings.getString("pub_rules"));
        this.ruleSetPub.addChangeListener(this.ruleButtonChangeListener);
        ruleSetGroup.add(this.ruleSetPub);

        rulesPanel.add(this.ruleSetISPA, "wrap");
        rulesPanel.add(this.ruleSetPub, "wrap");

        final JPanel pubRulesPanel = getPubRulesPanel();
        rulesPanel.add(pubRulesPanel, "gapleft 20px");

        rulesPanel.validate();

        return rulesPanel;
    }

    private JPanel getPubRulesPanel() {
        final JPanel pubRulesPanel = new JPanel(LayoutFactory.getMigLayout());

        this.resetPubRulesButton = new JButton(strings.getString("reset_to_defaults"));
        pubRulesPanel.add(this.resetPubRulesButton, "wrap");

        final JPanel contraPanel = new JPanel(LayoutFactory.getMigLayout());

        this.playContra = new JCheckBox(strings.getString("play_contra_re"));
        contraPanel.add(this.playContra, "wrap");

        this.contraAfterBid18 = new JCheckBox(strings.getString("contra_after_bid_18"));
        contraPanel.add(this.contraAfterBid18, "gapleft 20px");
        pubRulesPanel.add(contraPanel, "wrap");

        final JPanel bockPanel = getBockPanel();
        // pubRulesPanel.add(bockPanel, "wrap"); 

        final JPanel ramschPanel = new JPanel(LayoutFactory.getMigLayout());

        this.playRamsch = new JCheckBox(strings.getString("play_ramsch"));
        ramschPanel.add(this.playRamsch, "wrap");

        final JPanel schiebeRamschPanel = getSchiebeRamschPanel();
        ramschPanel.add(schiebeRamschPanel, "wrap");

        final JPanel ramschEventPanel = getRamschEventPanel();
        ramschPanel.add(ramschEventPanel, "gapleft 20px, wrap");

        final JPanel ramschSkatOwnerPanel = getRamschSkatOwnerPanel();
        ramschPanel.add(ramschSkatOwnerPanel, "gapleft 20px");

        pubRulesPanel.add(ramschPanel, "wrap");

        this.playRevolution = new JCheckBox(strings.getString("play_revolution"));
        // pubRulesPanel.add(playRevolution);

        return pubRulesPanel;
    }

    private JPanel getBockPanel() {
        final JPanel bockPanel = new JPanel(LayoutFactory.getMigLayout());

        this.playBock = new JCheckBox(strings.getString("play_bock"));
        bockPanel.add(this.playBock, "wrap");

        final JPanel bockDetailsPanel = getBockDetailsPanel();
        bockPanel.add(bockDetailsPanel, "gapleft 20px");
        return bockPanel;
    }

    private JPanel getBockDetailsPanel() {
        final JPanel bockDetailsPanel = new JPanel(LayoutFactory.getMigLayout());

        this.bockEventLabel = new JLabel(strings.getString("bock_events"));
        bockDetailsPanel.add(this.bockEventLabel, "span 2, wrap");
        this.bockEventAllPlayersPassed = new JCheckBox(strings.getString("bock_event_all_players_passed"));
        bockDetailsPanel.add(this.bockEventAllPlayersPassed, "wrap");
        this.bockEventLostAfterContra = new JCheckBox(strings.getString("bock_event_lost_contra"));
        bockDetailsPanel.add(this.bockEventLostAfterContra, "wrap");
        this.bockEventLostWith60 = new JCheckBox(strings.getString("bock_event_lost_game_with_60"));
        this.bockEventLostWith60.setSelected(this.options.isBockEventLostWith60(false).booleanValue());
        bockDetailsPanel.add(this.bockEventLostWith60, "wrap");
        this.bockEventContraReAnnounced = new JCheckBox(strings.getString("bock_event_contra_re"));
        bockDetailsPanel.add(this.bockEventContraReAnnounced);
        this.bockEventPlayerHasX00Points = new JCheckBox(strings.getString("bock_event_player_x00_points"));
        bockDetailsPanel.add(this.bockEventPlayerHasX00Points, "wrap");
        this.bockEventLostGrand = new JCheckBox(strings.getString("bock_event_lost_grand"));
        bockDetailsPanel.add(this.bockEventLostGrand);
        return bockDetailsPanel;
    }

    private JPanel getRamschSkatOwnerPanel() {
        final JPanel ramschSkatOwnerPanel = new JPanel(LayoutFactory.getMigLayout());
        this.ramschSkatLabel = new JLabel(strings.getString("ramsch_skat_owner"));
        ramschSkatOwnerPanel.add(this.ramschSkatLabel, "wrap");
        this.ramschSkatLastTrick = new JRadioButton(strings.getString("ramsch_skat_last_trick"));
        ramschSkatOwnerPanel.add(this.ramschSkatLastTrick, "wrap");
        this.ramschSkatLoser = new JRadioButton(strings.getString("ramsch_skat_loser"));
        ramschSkatOwnerPanel.add(this.ramschSkatLoser);

        this.ramschSkatOwner = new ButtonGroup();
        this.ramschSkatOwner.add(this.ramschSkatLastTrick);
        this.ramschSkatOwner.add(this.ramschSkatLoser);

        return ramschSkatOwnerPanel;
    }

    private JPanel getSchiebeRamschPanel() {
        final JPanel schiebeRamschPanel = new JPanel(LayoutFactory.getMigLayout());

        this.schiebeRamsch = new JCheckBox(strings.getString("schieberamsch"));
        schiebeRamschPanel.add(this.schiebeRamsch, "gapleft 20px, wrap");

        this.schiebeRamschJacksInSkat = new JCheckBox(strings.getString("schieberamsch_jacks_in_skat"));
        schiebeRamschPanel.add(this.schiebeRamschJacksInSkat, "gapleft 40px, wrap");
        return schiebeRamschPanel;
    }

    private JPanel getRamschEventPanel() {
        final JPanel ramschEventPanel = new JPanel(LayoutFactory.getMigLayout());

        this.ramschEventLabel = new JLabel(strings.getString("ramsch_events"));
        ramschEventPanel.add(this.ramschEventLabel, "span 2, wrap");
        this.ramschEventNoBid = new JCheckBox(strings.getString("ramsch_event_no_bid"));
        ramschEventPanel.add(this.ramschEventNoBid);
        this.ramschEventBockRamsch = new JCheckBox(strings.getString("ramsch_event_bock_ramsch"));
        // ramschEventPanel.add(ramschEventBockRamsch);
        return ramschEventPanel;
    }

    void activatePubRules(final boolean isActivated) {

        this.resetPubRulesButton.setEnabled(isActivated);

        this.playContra.setEnabled(isActivated);
        this.contraAfterBid18.setEnabled(isActivated);

        this.playBock.setEnabled(isActivated);
        this.bockEventLabel.setEnabled(isActivated);
        this.bockEventContraReAnnounced.setEnabled(isActivated);
        this.bockEventLostAfterContra.setEnabled(isActivated);
        this.bockEventLostGrand.setEnabled(isActivated);
        this.bockEventLostWith60.setEnabled(isActivated);
        this.bockEventPlayerHasX00Points.setEnabled(isActivated);

        this.playRamsch.setEnabled(isActivated);
        this.schiebeRamsch.setEnabled(isActivated);
        this.schiebeRamschJacksInSkat.setEnabled(isActivated);
        this.ramschSkatLabel.setEnabled(isActivated);
        this.ramschSkatLastTrick.setEnabled(isActivated);
        this.ramschSkatLoser.setEnabled(isActivated);
        this.ramschEventLabel.setEnabled(isActivated);
        this.ramschEventNoBid.setEnabled(isActivated);
        this.ramschEventBockRamsch.setEnabled(isActivated);

        this.playRevolution.setEnabled(isActivated);
    }

    /**
     * @see JDialog#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean isVisible) {

        if (isVisible) {
            setLocationRelativeTo(this.parent);
        }

        setOptionValues();

        SwingUtilities.invokeLater(() -> JSkatOptionsDialog.super.setVisible(isVisible));
    }

    private void setOptionValues() {
        // common options
        showTipsAtStartUp.setSelected(options.getBoolean(Option.SHOW_TIPS_AT_START_UP));
        checkForNewVersion.setSelected(options.getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP));
        hideToolbar.setSelected(options.getBoolean(Option.HIDE_TOOLBAR));
        language.setSelectedItem(options.getLanguage());

        cardSet.setSelectedItem(options.getCardSet());

        switch (options.getSavePathInternal()) {
            case USER_HOME:
                savePathUserHome.setSelected(true);
                break;
            case WORKING_DIRECTORY:
                savePathWorkingDirectory.setSelected(true);
                break;
        }

        waitTimeAfterTrick.setValue(options.getWaitTimeAfterTrick());

        // skat rule options
        switch (options.getRules()) {
            case ISPA:
                ruleSetISPA.setSelected(true);
                break;
            case PUB:
                ruleSetPub.setSelected(true);
                break;
        }
        playContra.setSelected(options.isPlayContra(false).booleanValue());
        contraAfterBid18.setSelected(options.isContraAfterBid18(false).booleanValue());
        playBock.setSelected(options.isPlayBock(false).booleanValue());
        bockEventLostAfterContra.setSelected(options.isBockEventLostAfterContra(false).booleanValue());
        bockEventContraReAnnounced.setSelected(options.isBockEventContraReCalled(false).booleanValue());
        bockEventPlayerHasX00Points.setSelected(options.isBockEventMultipleOfHundredScore(false).booleanValue());
        bockEventLostGrand.setSelected(options.isBockEventLostGrand(false).booleanValue());
        playRamsch.setSelected(options.isPlayRamsch(false).booleanValue());
        schiebeRamsch.setSelected(options.isSchieberamsch(false).booleanValue());
        schiebeRamschJacksInSkat.setSelected(options.isSchieberamschJacksInSkat(false).booleanValue());
        ramschSkatLastTrick.setSelected(RamschSkatOwner.LAST_TRICK.equals(options.getRamschSkatOwner()));
        ramschSkatLoser.setSelected(RamschSkatOwner.LOSER.equals(options.getRamschSkatOwner()));
        ramschEventNoBid.setSelected(options.isRamschEventNoBid(false).booleanValue());
        ramschEventBockRamsch.setSelected(options.isRamschEventRamschAfterBock(false).booleanValue());
        playRevolution.setSelected(options.isPlayRevolution(false).booleanValue());

        // ISS options
        issAddress.setText(options.getString(Option.ISS_ADDRESS));
        issPort.setText(options.getInteger(Option.ISS_PORT).toString());
    }

    CardSet getSelectedCardSet() {
        return (CardSet) this.cardSet.getSelectedItem();
    }

    void refreshCardSet() {
        this.repaint();
        this.parent.repaint();
    }

    private class LanguageComboBoxRenderer extends AbstractI18NComboBoxRenderer {

        private static final long serialVersionUID = 1L;

        LanguageComboBoxRenderer() {
            super();
        }

        @Override
        public String getValueText(final Object value) {

            String result = " ";

            final SupportedLanguage language = (SupportedLanguage) value;

            if (language != null) {
                switch (language) {
                    case ENGLISH:
                        result = JSkatOptionsDialog.this.strings.getString("english");
                        break;
                    case GERMAN:
                        result = JSkatOptionsDialog.this.strings.getString("german");
                        break;
                }
            }

            return result;
        }
    }

    private class CardSetComboBoxRenderer extends AbstractI18NComboBoxRenderer {

        private static final long serialVersionUID = 1L;

        CardSetComboBoxRenderer() {
            super();
        }

        @Override
        public String getValueText(final Object value) {

            String result = " ";

            final CardSet cardSet = (CardSet) value;

            if (cardSet != null) {
                result = JSkatOptionsDialog.this.strings.getString(
                        "cardset_" + getCardSetNameInLowerCase(cardSet) + "_" + getCardFaceInLowerCase(cardSet));
            }

            return result;
        }

        private String getCardFaceInLowerCase(final CardSet cardSet) {
            return cardSet.getCardFace().toString().toLowerCase();
        }

        private String getCardSetNameInLowerCase(final CardSet cardSet) {
            return cardSet.getName().toLowerCase().replace(" ", "");
        }
    }
}
