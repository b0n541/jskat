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
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Options dialog for JSkat
 */
public class JSkatOptionsDialog extends JDialog {

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
            options.setShowTipsAtStartUp(showTipsAtStartUp.isSelected());
            options.setCheckForNewVersionAtStartUp(checkForNewVersion.isSelected());
            options.setHideToolbar(hideToolbar.isSelected());
            options.setLanguage((SupportedLanguage) language.getSelectedItem());
            options.setCardSet(getSelectedCardSet());

            if (savePathUserHome.isSelected()) {
                options.setSavePath(SavePath.USER_HOME);
            } else if (savePathWorkingDirectory.isSelected()) {
                options.setSavePath(SavePath.WORKING_DIRECTORY);
            }

            options.setWaitTimeAfterTrick(waitTimeAfterTrick.getValue());
            options.setIssAddress(issAddress.getText());
            options.setIssPort(Integer.valueOf(issPort.getText()));

            if (ruleSetISPA.isSelected()) {
                options.setRules(RuleSet.ISPA);
            } else if (ruleSetPub.isSelected()) {
                options.setRules(RuleSet.PUB);
            }

            options.setRamschEventNoBid(ramschEventNoBid.isSelected());
            options.setBockEventContraReCalled(bockEventContraReAnnounced.isSelected());
            options.setBockEventLostGrand(bockEventLostGrand.isSelected());
            options.setBockEventLostAfterContra(bockEventLostAfterContra.isSelected());
            options.setBockEventLostWith60(bockEventLostWith60.isSelected());
            options.setPlayContra(playContra.isSelected());
            options.setContraAfterBid18(contraAfterBid18.isSelected());
            options.setPlayRamsch(playRamsch.isSelected());
            options.setPlayBock(playBock.isSelected());
            options.setPlayRevolution(playRevolution.isSelected());
            options.setSchieberRamsch(schiebeRamsch.isSelected());
            options.setSchieberRamschJacksInSkat(schiebeRamschJacksInSkat.isSelected());
            options.setRamschSkatOwner(
                    ramschSkatLastTrick.isSelected() ? RamschSkatOwner.LAST_TRICK
                            : RamschSkatOwner.LOSER);

            options.saveJSkatProperties();
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

    final ChangeListener ruleButtonChangeListener = e -> {
        if (ruleSetISPA.isSelected()) {
            activatePubRules(false);
        }
        if (ruleSetPub.isSelected()) {
            activatePubRules(true);
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
        options = JSkatOptions.instance();

        this.parent = parent;

        initGUI();
    }

    public JSkatOptionsDialog() {
        this(null);
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
        start.setAction(okAction);
        start.setText("OK");
        buttonPanel.add(start);
        final JButton cancel = new JButton();
        cancel.setAction(cancelAction);
        cancel.setText(strings.getString("cancel"));
        buttonPanel.add(cancel);

        root.add(buttonPanel, "center");
        root.validate();

        final InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap am = root.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
        am.put("OK", okAction);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
        am.put("CANCEL", cancelAction);

        setContentPane(root);
        validate();
        pack();
    }

    private JPanel getCardSetSelectionPanel() {
        final JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "", "[shrink][grow]"));

        panel.add(new JLabel(strings.getString("card_face")));
        panel.add(getCardSetPanel(), "growx, wrap");

        panel.add(getCardPanel(), "span 2, grow");

        panel.validate();

        return panel;
    }

    private CardPanel getCardPanel() {
        final CardPanel cardPanel = new CardPanel(1.0, false);

        for (final Card card : CardDeck.getAllCards()) {
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
        savePathUserHome = new JRadioButton(strings.getString("user_home"));
        savePathGroup.add(savePathUserHome);
        savePathWorkingDirectory = new JRadioButton(strings.getString("working_directory"));
        savePathGroup.add(savePathWorkingDirectory);

        savePathPanel.add(savePathUserHome); // $NON-NLS-1$
        savePathPanel.add(savePathWorkingDirectory, "wrap");

        return savePathPanel;
    }

    private JPanel getIssAddressPanel() {

        issAddress = new JTextField(20);
        final JPanel issAddressPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "shrink"));
        issAddressPanel.add(issAddress);

        return issAddressPanel;
    }

    private JPanel getIssPortPanel() {

        issPort = new JTextField(20);
        final JPanel issPortPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "shrink"));
        issPortPanel.add(issPort);
        return issPortPanel;
    }

    private JPanel getCardSetPanel() {

        cardSet = new JComboBox(CardSet.values());
        cardSet.setSelectedIndex(0);
        cardSet.setRenderer(new CardSetComboBoxRenderer());

        cardSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                options.setCardSet((CardSet) cardSet.getSelectedItem());
                refreshCardSet();
            }
        });

        final JPanel cardSetPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        cardSetPanel.add(cardSet);
        return cardSetPanel;
    }

    private JPanel getShowTipsPanel() {
        showTipsAtStartUp = new JCheckBox(strings.getString("show_tips_at_startup"));
        final JPanel showTipsPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        showTipsPanel.add(showTipsAtStartUp);
        return showTipsPanel;
    }

    private JPanel getCheckVersionPanel() {
        checkForNewVersion = new JCheckBox(strings.getString("check_for_new_version_at_startup"));
        final JPanel checkVersionPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        checkVersionPanel.add(checkForNewVersion);
        return checkVersionPanel;
    }

    private JPanel getHideToolbarPanel() {
        hideToolbar = new JCheckBox(strings.getString("hide_toolbar"));
        hideToolbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
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
        language = new JComboBox(SupportedLanguage.values());
        language.setRenderer(new LanguageComboBoxRenderer());
        final JPanel languagePanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill"));
        languagePanel.add(language);
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

        log.debug("Loaded rules: " + options.getRules());

        final JPanel rulesPanel = new JPanel(LayoutFactory.getMigLayout());

        final ButtonGroup ruleSetGroup = new ButtonGroup();
        ruleSetISPA = new JRadioButton(strings.getString("ispa_rules"));
        ruleSetISPA.addChangeListener(ruleButtonChangeListener);
        ruleSetGroup.add(ruleSetISPA);
        ruleSetPub = new JRadioButton(strings.getString("pub_rules"));
        ruleSetPub.addChangeListener(ruleButtonChangeListener);
        ruleSetGroup.add(ruleSetPub);

        rulesPanel.add(ruleSetISPA, "wrap");
        rulesPanel.add(ruleSetPub, "wrap");

        final JPanel pubRulesPanel = getPubRulesPanel();
        rulesPanel.add(pubRulesPanel, "gapleft 20px");

        rulesPanel.validate();

        return rulesPanel;
    }

    private JPanel getPubRulesPanel() {
        final JPanel pubRulesPanel = new JPanel(LayoutFactory.getMigLayout());

        resetPubRulesButton = new JButton(strings.getString("reset_to_defaults"));
        pubRulesPanel.add(resetPubRulesButton, "wrap");

        final JPanel contraPanel = new JPanel(LayoutFactory.getMigLayout());

        playContra = new JCheckBox(strings.getString("play_contra_re"));
        contraPanel.add(playContra, "wrap");

        contraAfterBid18 = new JCheckBox(strings.getString("contra_after_bid_18"));
        contraPanel.add(contraAfterBid18, "gapleft 20px");
        pubRulesPanel.add(contraPanel, "wrap");

        final JPanel bockPanel = getBockPanel();
        // pubRulesPanel.add(bockPanel, "wrap"); 

        final JPanel ramschPanel = new JPanel(LayoutFactory.getMigLayout());

        playRamsch = new JCheckBox(strings.getString("play_ramsch"));
        ramschPanel.add(playRamsch, "wrap");

        final JPanel schiebeRamschPanel = getSchiebeRamschPanel();
        ramschPanel.add(schiebeRamschPanel, "wrap");

        final JPanel ramschEventPanel = getRamschEventPanel();
        ramschPanel.add(ramschEventPanel, "gapleft 20px, wrap");

        final JPanel ramschSkatOwnerPanel = getRamschSkatOwnerPanel();
        ramschPanel.add(ramschSkatOwnerPanel, "gapleft 20px");

        pubRulesPanel.add(ramschPanel, "wrap");

        playRevolution = new JCheckBox(strings.getString("play_revolution"));
        // pubRulesPanel.add(playRevolution);

        return pubRulesPanel;
    }

    private JPanel getBockPanel() {
        final JPanel bockPanel = new JPanel(LayoutFactory.getMigLayout());

        playBock = new JCheckBox(strings.getString("play_bock"));
        bockPanel.add(playBock, "wrap");

        final JPanel bockDetailsPanel = getBockDetailsPanel();
        bockPanel.add(bockDetailsPanel, "gapleft 20px");
        return bockPanel;
    }

    private JPanel getBockDetailsPanel() {
        final JPanel bockDetailsPanel = new JPanel(LayoutFactory.getMigLayout());

        bockEventLabel = new JLabel(strings.getString("bock_events"));
        bockDetailsPanel.add(bockEventLabel, "span 2, wrap");
        bockEventAllPlayersPassed = new JCheckBox(strings.getString("bock_event_all_players_passed"));
        bockDetailsPanel.add(bockEventAllPlayersPassed, "wrap");
        bockEventLostAfterContra = new JCheckBox(strings.getString("bock_event_lost_contra"));
        bockDetailsPanel.add(bockEventLostAfterContra, "wrap");
        bockEventLostWith60 = new JCheckBox(strings.getString("bock_event_lost_game_with_60"));
        bockEventLostWith60.setSelected(options.isBockEventLostWith60(false).booleanValue());
        bockDetailsPanel.add(bockEventLostWith60, "wrap");
        bockEventContraReAnnounced = new JCheckBox(strings.getString("bock_event_contra_re"));
        bockDetailsPanel.add(bockEventContraReAnnounced);
        bockEventPlayerHasX00Points = new JCheckBox(strings.getString("bock_event_player_x00_points"));
        bockDetailsPanel.add(bockEventPlayerHasX00Points, "wrap");
        bockEventLostGrand = new JCheckBox(strings.getString("bock_event_lost_grand"));
        bockDetailsPanel.add(bockEventLostGrand);
        return bockDetailsPanel;
    }

    private JPanel getRamschSkatOwnerPanel() {
        final JPanel ramschSkatOwnerPanel = new JPanel(LayoutFactory.getMigLayout());
        ramschSkatLabel = new JLabel(strings.getString("ramsch_skat_owner"));
        ramschSkatOwnerPanel.add(ramschSkatLabel, "wrap");
        ramschSkatLastTrick = new JRadioButton(strings.getString("ramsch_skat_last_trick"));
        ramschSkatOwnerPanel.add(ramschSkatLastTrick, "wrap");
        ramschSkatLoser = new JRadioButton(strings.getString("ramsch_skat_loser"));
        ramschSkatOwnerPanel.add(ramschSkatLoser);

        ramschSkatOwner = new ButtonGroup();
        ramschSkatOwner.add(ramschSkatLastTrick);
        ramschSkatOwner.add(ramschSkatLoser);

        return ramschSkatOwnerPanel;
    }

    private JPanel getSchiebeRamschPanel() {
        final JPanel schiebeRamschPanel = new JPanel(LayoutFactory.getMigLayout());

        schiebeRamsch = new JCheckBox(strings.getString("schieberamsch"));
        schiebeRamschPanel.add(schiebeRamsch, "gapleft 20px, wrap");

        schiebeRamschJacksInSkat = new JCheckBox(strings.getString("schieberamsch_jacks_in_skat"));
        schiebeRamschPanel.add(schiebeRamschJacksInSkat, "gapleft 40px, wrap");
        return schiebeRamschPanel;
    }

    private JPanel getRamschEventPanel() {
        final JPanel ramschEventPanel = new JPanel(LayoutFactory.getMigLayout());

        ramschEventLabel = new JLabel(strings.getString("ramsch_events"));
        ramschEventPanel.add(ramschEventLabel, "span 2, wrap");
        ramschEventNoBid = new JCheckBox(strings.getString("ramsch_event_no_bid"));
        ramschEventPanel.add(ramschEventNoBid);
        ramschEventBockRamsch = new JCheckBox(strings.getString("ramsch_event_bock_ramsch"));
        // ramschEventPanel.add(ramschEventBockRamsch);
        return ramschEventPanel;
    }

    void activatePubRules(final boolean isActivated) {

        resetPubRulesButton.setEnabled(isActivated);

        playContra.setEnabled(isActivated);
        contraAfterBid18.setEnabled(isActivated);

        playBock.setEnabled(isActivated);
        bockEventLabel.setEnabled(isActivated);
        bockEventContraReAnnounced.setEnabled(isActivated);
        bockEventLostAfterContra.setEnabled(isActivated);
        bockEventLostGrand.setEnabled(isActivated);
        bockEventLostWith60.setEnabled(isActivated);
        bockEventPlayerHasX00Points.setEnabled(isActivated);

        playRamsch.setEnabled(isActivated);
        schiebeRamsch.setEnabled(isActivated);
        schiebeRamschJacksInSkat.setEnabled(isActivated);
        ramschSkatLabel.setEnabled(isActivated);
        ramschSkatLastTrick.setEnabled(isActivated);
        ramschSkatLoser.setEnabled(isActivated);
        ramschEventLabel.setEnabled(isActivated);
        ramschEventNoBid.setEnabled(isActivated);
        ramschEventBockRamsch.setEnabled(isActivated);

        playRevolution.setEnabled(isActivated);
    }

    /**
     * @see JDialog#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean isVisible) {

        if (isVisible) {
            setLocationRelativeTo(parent);
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
        return (CardSet) cardSet.getSelectedItem();
    }

    void refreshCardSet() {
        repaint();
        if (parent != null) {
            parent.repaint();
        }
    }

    private class LanguageComboBoxRenderer extends AbstractI18NComboBoxRenderer {


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
                        result = strings.getString("english");
                        break;
                    case GERMAN:
                        result = strings.getString("german");
                        break;
                }
            }

            return result;
        }
    }

    private class CardSetComboBoxRenderer extends AbstractI18NComboBoxRenderer {


        CardSetComboBoxRenderer() {
            super();
        }

        @Override
        public String getValueText(final Object value) {

            String result = " ";

            final CardSet cardSet = (CardSet) value;

            if (cardSet != null) {
                result = strings.getString(
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
