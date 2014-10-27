/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.Option;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.SkatTableOptions.RamschSkatOwner;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.gui.img.CardSet;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preferences dialog for JSkat
 */
public class JSkatOptionsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory
			.getLogger(JSkatOptionsDialog.class);

	private final JSkatResourceBundle strings;
	private final JSkatOptions options;

	private final JFrame parent;

	// general options
	private JCheckBox showTipsAtStartUp;
	private JCheckBox checkForNewVersion;
	private JComboBox language;
	private JComboBox cardSet;
	private JTextField savePath;

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
			JSkatOptionsDialog.this.options.setShowTipsAtStartUp(JSkatOptionsDialog.this.showTipsAtStartUp.isSelected());
			JSkatOptionsDialog.this.options.setCheckForNewVersionAtStartUp(JSkatOptionsDialog.this.checkForNewVersion
					.isSelected());
			JSkatOptionsDialog.this.options.setLanguage((SupportedLanguage) JSkatOptionsDialog.this.language.getSelectedItem());
			JSkatOptionsDialog.this.options.setCardSet(getSelectedCardSet());
			JSkatOptionsDialog.this.options.setSavePath(JSkatOptionsDialog.this.savePath.getText());
			JSkatOptionsDialog.this.options.setIssAddress(JSkatOptionsDialog.this.issAddress.getText());
			JSkatOptionsDialog.this.options.setIssPort(Integer.valueOf(JSkatOptionsDialog.this.issPort.getText()));

			if (JSkatOptionsDialog.this.ruleSetISPA.isSelected()) {
				JSkatOptionsDialog.this.options.setRules(RuleSet.ISPA);
			} else {
				JSkatOptionsDialog.this.options.setRules(RuleSet.PUB);
			}

			JSkatOptionsDialog.this.options.setRamschEventNoBid(JSkatOptionsDialog.this.ramschEventNoBid.isSelected());
			JSkatOptionsDialog.this.options.setBockEventContraReCalled(JSkatOptionsDialog.this.bockEventContraReAnnounced
					.isSelected());
			JSkatOptionsDialog.this.options.setBockEventLostGrand(JSkatOptionsDialog.this.bockEventLostGrand.isSelected());
			JSkatOptionsDialog.this.options.setBockEventLostAfterContra(JSkatOptionsDialog.this.bockEventLostAfterContra
					.isSelected());
			JSkatOptionsDialog.this.options.setBockEventLostWith60(JSkatOptionsDialog.this.bockEventLostWith60.isSelected());
			JSkatOptionsDialog.this.options.setPlayContra(JSkatOptionsDialog.this.playContra.isSelected());
			JSkatOptionsDialog.this.options.setContraAfterBid18(JSkatOptionsDialog.this.contraAfterBid18.isSelected());
			JSkatOptionsDialog.this.options.setPlayRamsch(JSkatOptionsDialog.this.playRamsch.isSelected());
			JSkatOptionsDialog.this.options.setPlayBock(JSkatOptionsDialog.this.playBock.isSelected());
			JSkatOptionsDialog.this.options.setPlayRevolution(JSkatOptionsDialog.this.playRevolution.isSelected());
			JSkatOptionsDialog.this.options.setSchieberRamsch(JSkatOptionsDialog.this.schiebeRamsch.isSelected());
			JSkatOptionsDialog.this.options.setSchieberRamschJacksInSkat(JSkatOptionsDialog.this.schiebeRamschJacksInSkat
					.isSelected());
			JSkatOptionsDialog.this.options.setRamschSkatOwner(JSkatOptionsDialog.this.ramschSkatLastTrick.isSelected() ? RamschSkatOwner.LAST_TRICK
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
	 * @param mainFrame
	 *            Main window
	 */
	public JSkatOptionsDialog(final JFrame mainFrame) {

		this.strings = JSkatResourceBundle.INSTANCE;
		this.options = JSkatOptions.instance();
		this.parent = mainFrame;

		initGUI();
	}

	private void initGUI() {

		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		setTitle(this.strings.getString("preferences")); //$NON-NLS-1$

		final JPanel root = new JPanel(LayoutFactory.getMigLayout());

		final JTabbedPane prefTabs = new JTabbedPane();

		final JPanel commonTab = getCommonPanel();
		log.debug("Common tab: " + commonTab.getPreferredSize().toString());
		prefTabs.addTab(this.strings.getString("common_options"), commonTab); //$NON-NLS-1$

		final JPanel cardSetTab = getCardSetSelectionPanel();
		log.debug("Card set tab: " + cardSetTab.getPreferredSize().toString());
		prefTabs.addTab(this.strings.getString("cardset_options"), cardSetTab); //$NON-NLS-1$

		final JPanel skatRulesTab = getSkatRulesPanel();
		log.debug("Skat rules tab: "
				+ skatRulesTab.getPreferredSize().toString());
		prefTabs.addTab(this.strings.getString("skat_rules"), skatRulesTab); //$NON-NLS-1$

		final JPanel issTab = getIssPanel();
		log.debug("ISS tab: " + issTab.getPreferredSize().toString());
		prefTabs.addTab(this.strings.getString("iss"), issTab); //$NON-NLS-1$

		root.add(prefTabs, "wrap"); //$NON-NLS-1$

		final JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout());
		final JButton start = new JButton();
		start.setAction(this.okAction);
		start.setText("OK"); //$NON-NLS-1$
		buttonPanel.add(start);
		final JButton cancel = new JButton();
		cancel.setAction(this.cancelAction);
		cancel.setText(this.strings.getString("cancel")); //$NON-NLS-1$
		buttonPanel.add(cancel);

		root.add(buttonPanel, "center"); //$NON-NLS-1$
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
		JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "",
				"[shrink][grow]"));

		panel.add(new JLabel(this.strings.getString("card_face"))); //$NON-NLS-1$
		panel.add(getCardSetPanel(), "growx, wrap"); //$NON-NLS-1$

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

		issPanel.add(new JLabel(this.strings.getString("iss_address")), "shrinky"); //$NON-NLS-1$
		issPanel.add(getIssAddressPanel(), "shrinky, wrap"); //$NON-NLS-1$
		issPanel.add(new JLabel(this.strings.getString("iss_port")), "shrinky"); //$NON-NLS-1$
		issPanel.add(getIssPortPanel(), "shrinky, wrap"); //$NON-NLS-1$

		issPanel.validate();

		return issPanel;
	}

	private JPanel getSavePathPanel() {
		this.savePath = new JTextField(20);
		this.savePath.setEditable(false);
		final JButton savePathButton = new JButton(this.strings.getString("search")); //$NON-NLS-1$
		savePathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setFileHidingEnabled(false);
				fileChooser.setCurrentDirectory(new File(JSkatOptionsDialog.this.savePath.getText()));
				final int result = fileChooser
						.showOpenDialog(JSkatOptionsDialog.this.parent);
				if (result == JFileChooser.APPROVE_OPTION) {
					JSkatOptionsDialog.this.savePath.setText(fileChooser
							.getSelectedFile().getAbsolutePath());
				}
			}
		});
		final JPanel savePathPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		savePathPanel.add(this.savePath);
		savePathPanel.add(savePathButton);
		return savePathPanel;
	}

	private JPanel getIssAddressPanel() {

		this.issAddress = new JTextField(20);
		final JPanel issAddressPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		issAddressPanel.add(this.issAddress);

		return issAddressPanel;
	}

	private JPanel getIssPortPanel() {

		this.issPort = new JTextField(20);
		final JPanel issPortPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

		final JPanel cardSetPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cardSetPanel.add(this.cardSet);
		return cardSetPanel;
	}

	private JPanel getShowTipsPanel() {
		this.showTipsAtStartUp = new JCheckBox(
				this.strings.getString("show_tips_at_startup")); //$NON-NLS-1$
		final JPanel showTipsPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		showTipsPanel.add(this.showTipsAtStartUp);
		return showTipsPanel;
	}

	private JPanel getCheckVersionPanel() {
		this.checkForNewVersion = new JCheckBox(
				this.strings.getString("check_for_new_version_at_startup")); //$NON-NLS-1$
		final JPanel checkVersionPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		checkVersionPanel.add(this.checkForNewVersion);
		return checkVersionPanel;
	}

	private JPanel getLanguagePanel() {
		this.language = new JComboBox(SupportedLanguage.values());
		this.language.setRenderer(new LanguageComboBoxRenderer());
		final JPanel languagePanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		languagePanel.add(this.language);
		return languagePanel;
	}

	private JPanel getCommonPanel() {

		final JPanel commonPanel = new JPanel(LayoutFactory.getMigLayout());

		commonPanel.add(new JLabel(this.strings.getString("show_tips"))); //$NON-NLS-1$
		commonPanel.add(getShowTipsPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(this.strings.getString("check_for_new_version"))); //$NON-NLS-1$
		commonPanel.add(getCheckVersionPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(this.strings.getString("language"))); //$NON-NLS-1$
		commonPanel.add(getLanguagePanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(this.strings.getString("save_path"))); //$NON-NLS-1$
		commonPanel.add(getSavePathPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.validate();

		return commonPanel;
	}

	private JPanel getSkatRulesPanel() {

		log.debug("Loaded rules: " + this.options.getRules());

		final JPanel rulesPanel = new JPanel(LayoutFactory.getMigLayout());

		final ButtonGroup ruleSetGroup = new ButtonGroup();
		this.ruleSetISPA = new JRadioButton(this.strings.getString("ispa_rules")); //$NON-NLS-1$
		this.ruleSetISPA.addChangeListener(this.ruleButtonChangeListener);
		ruleSetGroup.add(this.ruleSetISPA);
		this.ruleSetPub = new JRadioButton(this.strings.getString("pub_rules")); //$NON-NLS-1$
		this.ruleSetPub.addChangeListener(this.ruleButtonChangeListener);
		ruleSetGroup.add(this.ruleSetPub);

		rulesPanel.add(this.ruleSetISPA, "wrap"); //$NON-NLS-1$
		rulesPanel.add(this.ruleSetPub, "wrap"); //$NON-NLS-1$

		final JPanel pubRulesPanel = getPubRulesPanel();
		rulesPanel.add(pubRulesPanel, "gapleft 20px"); //$NON-NLS-1$

		rulesPanel.validate();

		return rulesPanel;
	}

	private JPanel getPubRulesPanel() {
		final JPanel pubRulesPanel = new JPanel(LayoutFactory.getMigLayout());

		this.resetPubRulesButton = new JButton(
				this.strings.getString("reset_to_defaults")); //$NON-NLS-1$
		pubRulesPanel.add(this.resetPubRulesButton, "wrap"); //$NON-NLS-1$

		final JPanel contraPanel = new JPanel(LayoutFactory.getMigLayout());

		this.playContra = new JCheckBox(this.strings.getString("play_contra_re")); //$NON-NLS-1$
		contraPanel.add(this.playContra, "wrap"); //$NON-NLS-1$

		this.contraAfterBid18 = new JCheckBox(
				this.strings.getString("contra_after_bid_18")); //$NON-NLS-1$
		contraPanel.add(this.contraAfterBid18, "gapleft 20px"); //$NON-NLS-1$
		pubRulesPanel.add(contraPanel, "wrap"); //$NON-NLS-1$

		final JPanel bockPanel = getBockPanel();
		//		pubRulesPanel.add(bockPanel, "wrap"); //$NON-NLS-1$

		final JPanel ramschPanel = new JPanel(LayoutFactory.getMigLayout());

		this.playRamsch = new JCheckBox(this.strings.getString("play_ramsch")); //$NON-NLS-1$
		ramschPanel.add(this.playRamsch, "wrap"); //$NON-NLS-1$

		final JPanel schiebeRamschPanel = getSchiebeRamschPanel();
		ramschPanel.add(schiebeRamschPanel, "wrap"); //$NON-NLS-1$

		final JPanel ramschEventPanel = getRamschEventPanel();
		ramschPanel.add(ramschEventPanel, "gapleft 20px, wrap"); //$NON-NLS-1$

		final JPanel ramschSkatOwnerPanel = getRamschSkatOwnerPanel();
		ramschPanel.add(ramschSkatOwnerPanel, "gapleft 20px"); //$NON-NLS-1$

		pubRulesPanel.add(ramschPanel, "wrap"); //$NON-NLS-1$

		this.playRevolution = new JCheckBox(this.strings.getString("play_revolution")); //$NON-NLS-1$
		// pubRulesPanel.add(playRevolution);

		return pubRulesPanel;
	}

	private JPanel getBockPanel() {
		final JPanel bockPanel = new JPanel(LayoutFactory.getMigLayout());

		this.playBock = new JCheckBox(this.strings.getString("play_bock")); //$NON-NLS-1$
		bockPanel.add(this.playBock, "wrap"); //$NON-NLS-1$

		final JPanel bockDetailsPanel = getBockDetailsPanel();
		bockPanel.add(bockDetailsPanel, "gapleft 20px"); //$NON-NLS-1$
		return bockPanel;
	}

	private JPanel getBockDetailsPanel() {
		final JPanel bockDetailsPanel = new JPanel(LayoutFactory.getMigLayout());

		this.bockEventLabel = new JLabel(this.strings.getString("bock_events")); //$NON-NLS-1$
		bockDetailsPanel.add(this.bockEventLabel, "span 2, wrap"); //$NON-NLS-1$
		this.bockEventAllPlayersPassed = new JCheckBox(
				this.strings.getString("bock_event_all_players_passed")); //$NON-NLS-1$
		bockDetailsPanel.add(this.bockEventAllPlayersPassed, "wrap");
		this.bockEventLostAfterContra = new JCheckBox(
				this.strings.getString("bock_event_lost_contra")); //$NON-NLS-1$
		bockDetailsPanel.add(this.bockEventLostAfterContra, "wrap");
		this.bockEventLostWith60 = new JCheckBox(
				this.strings.getString("bock_event_lost_game_with_60")); //$NON-NLS-1$
		this.bockEventLostWith60.setSelected(this.options.isBockEventLostWith60(false)
				.booleanValue());
		bockDetailsPanel.add(this.bockEventLostWith60, "wrap"); //$NON-NLS-1$
		this.bockEventContraReAnnounced = new JCheckBox(
				this.strings.getString("bock_event_contra_re")); //$NON-NLS-1$
		bockDetailsPanel.add(this.bockEventContraReAnnounced);
		this.bockEventPlayerHasX00Points = new JCheckBox(
				this.strings.getString("bock_event_player_x00_points")); //$NON-NLS-1$
		bockDetailsPanel.add(this.bockEventPlayerHasX00Points, "wrap"); //$NON-NLS-1$
		this.bockEventLostGrand = new JCheckBox(
				this.strings.getString("bock_event_lost_grand")); //$NON-NLS-1$
		bockDetailsPanel.add(this.bockEventLostGrand);
		return bockDetailsPanel;
	}

	private JPanel getRamschSkatOwnerPanel() {
		final JPanel ramschSkatOwnerPanel = new JPanel(
				LayoutFactory.getMigLayout());
		this.ramschSkatLabel = new JLabel(this.strings.getString("ramsch_skat_owner")); //$NON-NLS-1$
		ramschSkatOwnerPanel.add(this.ramschSkatLabel, "wrap"); //$NON-NLS-1$
		this.ramschSkatLastTrick = new JRadioButton(
				this.strings.getString("ramsch_skat_last_trick")); //$NON-NLS-1$
		ramschSkatOwnerPanel.add(this.ramschSkatLastTrick, "wrap"); //$NON-NLS-1$
		this.ramschSkatLoser = new JRadioButton(
				this.strings.getString("ramsch_skat_loser")); //$NON-NLS-1$
		ramschSkatOwnerPanel.add(this.ramschSkatLoser);

		this.ramschSkatOwner = new ButtonGroup();
		this.ramschSkatOwner.add(this.ramschSkatLastTrick);
		this.ramschSkatOwner.add(this.ramschSkatLoser);

		return ramschSkatOwnerPanel;
	}

	private JPanel getSchiebeRamschPanel() {
		final JPanel schiebeRamschPanel = new JPanel(
				LayoutFactory.getMigLayout());

		this.schiebeRamsch = new JCheckBox(this.strings.getString("schieberamsch")); //$NON-NLS-1$
		schiebeRamschPanel.add(this.schiebeRamsch, "gapleft 20px, wrap"); //$NON-NLS-1$

		this.schiebeRamschJacksInSkat = new JCheckBox(
				this.strings.getString("schieberamsch_jacks_in_skat")); //$NON-NLS-1$
		schiebeRamschPanel.add(this.schiebeRamschJacksInSkat, "gapleft 40px, wrap"); //$NON-NLS-1$
		return schiebeRamschPanel;
	}

	private JPanel getRamschEventPanel() {
		final JPanel ramschEventPanel = new JPanel(LayoutFactory.getMigLayout());

		this.ramschEventLabel = new JLabel(this.strings.getString("ramsch_events")); //$NON-NLS-1$
		ramschEventPanel.add(this.ramschEventLabel, "span 2, wrap"); //$NON-NLS-1$
		this.ramschEventNoBid = new JCheckBox(
				this.strings.getString("ramsch_event_no_bid")); //$NON-NLS-1$
		ramschEventPanel.add(this.ramschEventNoBid);
		this.ramschEventBockRamsch = new JCheckBox(
				this.strings.getString("ramsch_event_bock_ramsch")); //$NON-NLS-1$
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

		super.setVisible(isVisible);
	}

	private void setOptionValues() {
		// common options
		this.showTipsAtStartUp.setSelected(this.options
				.getBoolean(Option.SHOW_TIPS_AT_START_UP));
		this.checkForNewVersion.setSelected(this.options
				.getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP));
		this.language.setSelectedItem(this.options.getLanguage());

		this.cardSet.setSelectedItem(this.options.getCardSet());

		this.savePath.setText(this.options.getSavePath());

		// skat rule options
		switch (this.options.getRules()) {
		case ISPA:
			this.ruleSetISPA.setSelected(true);
			break;
		case PUB:
			this.ruleSetPub.setSelected(true);
			break;
		}
		this.playContra.setSelected(this.options.isPlayContra(false).booleanValue());
		this.contraAfterBid18.setSelected(this.options.isContraAfterBid18(false)
				.booleanValue());
		this.playBock.setSelected(this.options.isPlayBock(false).booleanValue());
		this.bockEventLostAfterContra.setSelected(this.options
				.isBockEventLostAfterContra(false).booleanValue());
		this.bockEventContraReAnnounced.setSelected(this.options
				.isBockEventContraReCalled(false).booleanValue());
		this.bockEventPlayerHasX00Points.setSelected(this.options
				.isBockEventMultipleOfHundredScore(false).booleanValue());
		this.bockEventLostGrand.setSelected(this.options.isBockEventLostGrand(false)
				.booleanValue());
		this.playRamsch.setSelected(this.options.isPlayRamsch(false).booleanValue());
		this.schiebeRamsch
				.setSelected(this.options.isSchieberamsch(false).booleanValue());
		this.schiebeRamschJacksInSkat.setSelected(this.options
				.isSchieberamschJacksInSkat(false).booleanValue());
		this.ramschSkatLastTrick.setSelected(RamschSkatOwner.LAST_TRICK
				.equals(this.options.getRamschSkatOwner()));
		this.ramschSkatLoser.setSelected(RamschSkatOwner.LOSER.equals(this.options
				.getRamschSkatOwner()));
		this.ramschEventNoBid.setSelected(this.options.isRamschEventNoBid(false)
				.booleanValue());
		this.ramschEventBockRamsch.setSelected(this.options.isRamschEventRamschAfterBock(
				false).booleanValue());
		this.playRevolution.setSelected(this.options.isPlayRevolution(false)
				.booleanValue());

		// ISS options
		this.issAddress.setText(this.options.getString(Option.ISS_ADDRESS));
		this.issPort.setText(this.options.getInteger(Option.ISS_PORT).toString());
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

			String result = " "; //$NON-NLS-1$

			final SupportedLanguage language = (SupportedLanguage) value;

			if (language != null) {
				switch (language) {
				case ENGLISH:
					result = JSkatOptionsDialog.this.strings.getString("english"); //$NON-NLS-1$
					break;
				case GERMAN:
					result = JSkatOptionsDialog.this.strings.getString("german"); //$NON-NLS-1$
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

			String result = " "; //$NON-NLS-1$

			final CardSet cardSet = (CardSet) value;

			if (cardSet != null) {
				result = JSkatOptionsDialog.this.strings
						.getString("cardset_" + getCardSetNameInLowerCase(cardSet) + "_" + getCardFaceInLowerCase(cardSet)); //$NON-NLS-1$
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
