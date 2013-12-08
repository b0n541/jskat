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

	private JSkatResourceBundle strings;
	private JSkatOptions options;

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
			options.setShowTipsAtStartUp(showTipsAtStartUp.isSelected());
			options.setCheckForNewVersionAtStartUp(checkForNewVersion
					.isSelected());
			options.setLanguage((SupportedLanguage) language.getSelectedItem());
			options.setCardSet(getSelectedCardSet());
			options.setSavePath(savePath.getText());
			options.setIssAddress(issAddress.getText());
			options.setIssPort(Integer.valueOf(issPort.getText()));

			if (ruleSetISPA.isSelected()) {
				options.setRules(RuleSet.ISPA);
			} else {
				options.setRules(RuleSet.PUB);
			}

			options.setRamschEventNoBid(ramschEventNoBid.isSelected());
			options.setBockEventContraReCalled(bockEventContraReAnnounced
					.isSelected());
			options.setBockEventLostGrand(bockEventLostGrand.isSelected());
			options.setBockEventLostAfterContra(bockEventLostAfterContra
					.isSelected());
			options.setBockEventLostWith60(bockEventLostWith60.isSelected());
			options.setPlayContra(playContra.isSelected());
			options.setContraAfterBid18(contraAfterBid18.isSelected());
			options.setPlayRamsch(playRamsch.isSelected());
			options.setPlayBock(playBock.isSelected());
			options.setPlayRevolution(playRevolution.isSelected());
			options.setSchieberRamsch(schiebeRamsch.isSelected());
			options.setSchieberRamschJacksInSkat(schiebeRamschJacksInSkat
					.isSelected());
			options.setRamschSkatOwner(ramschSkatLastTrick.isSelected() ? RamschSkatOwner.LAST_TRICK
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

	final ChangeListener ruleButtonChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(final ChangeEvent e) {
			if (ruleSetISPA.isSelected()) {
				activatePubRules(false);
			}
			if (ruleSetPub.isSelected()) {
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
	 */
	public JSkatOptionsDialog(final JFrame mainFrame) {

		strings = JSkatResourceBundle.instance();
		options = JSkatOptions.instance();
		parent = mainFrame;

		initGUI();
	}

	private void initGUI() {

		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		setTitle(strings.getString("preferences")); //$NON-NLS-1$

		final JPanel root = new JPanel(LayoutFactory.getMigLayout());

		final JTabbedPane prefTabs = new JTabbedPane();

		final JPanel commonTab = getCommonPanel();
		log.debug("Common tab: " + commonTab.getPreferredSize().toString());
		prefTabs.addTab(strings.getString("common_options"), commonTab); //$NON-NLS-1$

		final JPanel cardSetTab = getCardSetSelectionPanel();
		log.debug("Card set tab: " + cardSetTab.getPreferredSize().toString());
		prefTabs.addTab(strings.getString("cardset_options"), cardSetTab); //$NON-NLS-1$

		final JPanel skatRulesTab = getSkatRulesPanel();
		log.debug("Skat rules tab: "
				+ skatRulesTab.getPreferredSize().toString());
		prefTabs.addTab(strings.getString("skat_rules"), skatRulesTab); //$NON-NLS-1$

		final JPanel issTab = getIssPanel();
		log.debug("ISS tab: " + issTab.getPreferredSize().toString());
		prefTabs.addTab(strings.getString("iss"), issTab); //$NON-NLS-1$

		root.add(prefTabs, "wrap"); //$NON-NLS-1$

		final JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout());
		final JButton start = new JButton();
		start.setAction(okAction);
		start.setText("OK"); //$NON-NLS-1$
		buttonPanel.add(start);
		final JButton cancel = new JButton();
		cancel.setAction(cancelAction);
		cancel.setText(strings.getString("cancel")); //$NON-NLS-1$
		buttonPanel.add(cancel);

		root.add(buttonPanel, "center"); //$NON-NLS-1$
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
		JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "",
				"[shrink][grow]"));

		panel.add(new JLabel(strings.getString("card_face"))); //$NON-NLS-1$
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

		issPanel.add(new JLabel(strings.getString("iss_address")), "shrinky"); //$NON-NLS-1$
		issPanel.add(getIssAddressPanel(), "shrinky, wrap"); //$NON-NLS-1$
		issPanel.add(new JLabel(strings.getString("iss_port")), "shrinky"); //$NON-NLS-1$
		issPanel.add(getIssPortPanel(), "shrinky, wrap"); //$NON-NLS-1$

		issPanel.validate();

		return issPanel;
	}

	private JPanel getSavePathPanel() {
		savePath = new JTextField(20);
		savePath.setEditable(false);
		final JButton savePathButton = new JButton(strings.getString("search")); //$NON-NLS-1$
		savePathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setFileHidingEnabled(false);
				fileChooser.setCurrentDirectory(new File(savePath.getText()));
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
		savePathPanel.add(savePath);
		savePathPanel.add(savePathButton);
		return savePathPanel;
	}

	private JPanel getIssAddressPanel() {

		issAddress = new JTextField(20);
		final JPanel issAddressPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		issAddressPanel.add(issAddress);

		return issAddressPanel;
	}

	private JPanel getIssPortPanel() {

		issPort = new JTextField(20);
		final JPanel issPortPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		issPortPanel.add(issPort);
		return issPortPanel;
	}

	private JPanel getCardSetPanel() {

		cardSet = new JComboBox(CardSet.values());
		cardSet.setSelectedIndex(0);
		cardSet.setRenderer(new CardSetComboBoxRenderer());

		cardSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				options.setCardSet((CardSet) cardSet.getSelectedItem());
				refreshCardSet();
			}
		});

		final JPanel cardSetPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cardSetPanel.add(cardSet);
		return cardSetPanel;
	}

	private JPanel getShowTipsPanel() {
		showTipsAtStartUp = new JCheckBox(
				strings.getString("show_tips_at_startup")); //$NON-NLS-1$
		final JPanel showTipsPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		showTipsPanel.add(showTipsAtStartUp);
		return showTipsPanel;
	}

	private JPanel getCheckVersionPanel() {
		checkForNewVersion = new JCheckBox(
				strings.getString("check_for_new_version_at_startup")); //$NON-NLS-1$
		final JPanel checkVersionPanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		checkVersionPanel.add(checkForNewVersion);
		return checkVersionPanel;
	}

	private JPanel getLanguagePanel() {
		language = new JComboBox(SupportedLanguage.values());
		language.setRenderer(new LanguageComboBoxRenderer());
		final JPanel languagePanel = new JPanel(LayoutFactory.getMigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		languagePanel.add(language);
		return languagePanel;
	}

	private JPanel getCommonPanel() {

		final JPanel commonPanel = new JPanel(LayoutFactory.getMigLayout());

		commonPanel.add(new JLabel(strings.getString("show_tips"))); //$NON-NLS-1$
		commonPanel.add(getShowTipsPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("check_for_new_version"))); //$NON-NLS-1$
		commonPanel.add(getCheckVersionPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("language"))); //$NON-NLS-1$
		commonPanel.add(getLanguagePanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("save_path"))); //$NON-NLS-1$
		commonPanel.add(getSavePathPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.validate();

		return commonPanel;
	}

	private JPanel getSkatRulesPanel() {

		log.debug("Loaded rules: " + options.getRules());

		final JPanel rulesPanel = new JPanel(LayoutFactory.getMigLayout());

		final ButtonGroup ruleSetGroup = new ButtonGroup();
		ruleSetISPA = new JRadioButton(strings.getString("ispa_rules")); //$NON-NLS-1$
		ruleSetISPA.addChangeListener(ruleButtonChangeListener);
		ruleSetGroup.add(ruleSetISPA);
		ruleSetPub = new JRadioButton(strings.getString("pub_rules")); //$NON-NLS-1$
		ruleSetPub.addChangeListener(ruleButtonChangeListener);
		ruleSetGroup.add(ruleSetPub);

		rulesPanel.add(ruleSetISPA, "wrap"); //$NON-NLS-1$
		rulesPanel.add(ruleSetPub, "wrap"); //$NON-NLS-1$

		final JPanel pubRulesPanel = getPubRulesPanel();
		rulesPanel.add(pubRulesPanel, "gapleft 20px"); //$NON-NLS-1$

		rulesPanel.validate();

		return rulesPanel;
	}

	private JPanel getPubRulesPanel() {
		final JPanel pubRulesPanel = new JPanel(LayoutFactory.getMigLayout());

		resetPubRulesButton = new JButton(
				strings.getString("reset_to_defaults")); //$NON-NLS-1$
		pubRulesPanel.add(resetPubRulesButton, "wrap"); //$NON-NLS-1$

		final JPanel contraPanel = new JPanel(LayoutFactory.getMigLayout());

		playContra = new JCheckBox(strings.getString("play_contra_re")); //$NON-NLS-1$
		contraPanel.add(playContra, "wrap"); //$NON-NLS-1$

		contraAfterBid18 = new JCheckBox(
				strings.getString("contra_after_bid_18")); //$NON-NLS-1$
		contraPanel.add(contraAfterBid18, "gapleft 20px"); //$NON-NLS-1$
		pubRulesPanel.add(contraPanel, "wrap"); //$NON-NLS-1$

		final JPanel bockPanel = getBockPanel();
		//		pubRulesPanel.add(bockPanel, "wrap"); //$NON-NLS-1$

		final JPanel ramschPanel = new JPanel(LayoutFactory.getMigLayout());

		playRamsch = new JCheckBox(strings.getString("play_ramsch")); //$NON-NLS-1$
		ramschPanel.add(playRamsch, "wrap"); //$NON-NLS-1$

		final JPanel schiebeRamschPanel = getSchiebeRamschPanel();
		ramschPanel.add(schiebeRamschPanel, "wrap"); //$NON-NLS-1$

		final JPanel ramschEventPanel = getRamschEventPanel();
		ramschPanel.add(ramschEventPanel, "gapleft 20px, wrap"); //$NON-NLS-1$

		final JPanel ramschSkatOwnerPanel = getRamschSkatOwnerPanel();
		ramschPanel.add(ramschSkatOwnerPanel, "gapleft 20px"); //$NON-NLS-1$

		pubRulesPanel.add(ramschPanel, "wrap"); //$NON-NLS-1$

		playRevolution = new JCheckBox(strings.getString("play_revolution")); //$NON-NLS-1$
		// pubRulesPanel.add(playRevolution);

		return pubRulesPanel;
	}

	private JPanel getBockPanel() {
		final JPanel bockPanel = new JPanel(LayoutFactory.getMigLayout());

		playBock = new JCheckBox(strings.getString("play_bock")); //$NON-NLS-1$
		bockPanel.add(playBock, "wrap"); //$NON-NLS-1$

		final JPanel bockDetailsPanel = getBockDetailsPanel();
		bockPanel.add(bockDetailsPanel, "gapleft 20px"); //$NON-NLS-1$
		return bockPanel;
	}

	private JPanel getBockDetailsPanel() {
		final JPanel bockDetailsPanel = new JPanel(LayoutFactory.getMigLayout());

		bockEventLabel = new JLabel(strings.getString("bock_events")); //$NON-NLS-1$
		bockDetailsPanel.add(bockEventLabel, "span 2, wrap"); //$NON-NLS-1$
		bockEventAllPlayersPassed = new JCheckBox(
				strings.getString("bock_event_all_players_passed")); //$NON-NLS-1$
		bockDetailsPanel.add(bockEventAllPlayersPassed, "wrap");
		bockEventLostAfterContra = new JCheckBox(
				strings.getString("bock_event_lost_contra")); //$NON-NLS-1$
		bockDetailsPanel.add(bockEventLostAfterContra, "wrap");
		bockEventLostWith60 = new JCheckBox(
				strings.getString("bock_event_lost_game_with_60")); //$NON-NLS-1$
		bockEventLostWith60.setSelected(options.isBockEventLostWith60(false)
				.booleanValue());
		bockDetailsPanel.add(bockEventLostWith60, "wrap"); //$NON-NLS-1$
		bockEventContraReAnnounced = new JCheckBox(
				strings.getString("bock_event_contra_re")); //$NON-NLS-1$
		bockDetailsPanel.add(bockEventContraReAnnounced);
		bockEventPlayerHasX00Points = new JCheckBox(
				strings.getString("bock_event_player_x00_points")); //$NON-NLS-1$
		bockDetailsPanel.add(bockEventPlayerHasX00Points, "wrap"); //$NON-NLS-1$
		bockEventLostGrand = new JCheckBox(
				strings.getString("bock_event_lost_grand")); //$NON-NLS-1$
		bockDetailsPanel.add(bockEventLostGrand);
		return bockDetailsPanel;
	}

	private JPanel getRamschSkatOwnerPanel() {
		final JPanel ramschSkatOwnerPanel = new JPanel(
				LayoutFactory.getMigLayout());
		ramschSkatLabel = new JLabel(strings.getString("ramsch_skat_owner")); //$NON-NLS-1$
		ramschSkatOwnerPanel.add(ramschSkatLabel, "wrap"); //$NON-NLS-1$
		ramschSkatLastTrick = new JRadioButton(
				strings.getString("ramsch_skat_last_trick")); //$NON-NLS-1$
		ramschSkatOwnerPanel.add(ramschSkatLastTrick, "wrap"); //$NON-NLS-1$
		ramschSkatLoser = new JRadioButton(
				strings.getString("ramsch_skat_loser")); //$NON-NLS-1$
		ramschSkatOwnerPanel.add(ramschSkatLoser);

		ramschSkatOwner = new ButtonGroup();
		ramschSkatOwner.add(ramschSkatLastTrick);
		ramschSkatOwner.add(ramschSkatLoser);

		return ramschSkatOwnerPanel;
	}

	private JPanel getSchiebeRamschPanel() {
		final JPanel schiebeRamschPanel = new JPanel(
				LayoutFactory.getMigLayout());

		schiebeRamsch = new JCheckBox(strings.getString("schieberamsch")); //$NON-NLS-1$
		schiebeRamschPanel.add(schiebeRamsch, "gapleft 20px, wrap"); //$NON-NLS-1$

		schiebeRamschJacksInSkat = new JCheckBox(
				strings.getString("schieberamsch_jacks_in_skat")); //$NON-NLS-1$
		schiebeRamschPanel.add(schiebeRamschJacksInSkat, "gapleft 40px, wrap"); //$NON-NLS-1$
		return schiebeRamschPanel;
	}

	private JPanel getRamschEventPanel() {
		final JPanel ramschEventPanel = new JPanel(LayoutFactory.getMigLayout());

		ramschEventLabel = new JLabel(strings.getString("ramsch_events")); //$NON-NLS-1$
		ramschEventPanel.add(ramschEventLabel, "span 2, wrap"); //$NON-NLS-1$
		ramschEventNoBid = new JCheckBox(
				strings.getString("ramsch_event_no_bid")); //$NON-NLS-1$
		ramschEventPanel.add(ramschEventNoBid);
		ramschEventBockRamsch = new JCheckBox(
				strings.getString("ramsch_event_bock_ramsch")); //$NON-NLS-1$
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

		super.setVisible(isVisible);
	}

	private void setOptionValues() {
		// common options
		showTipsAtStartUp.setSelected(options
				.getBoolean(Option.SHOW_TIPS_AT_START_UP));
		checkForNewVersion.setSelected(options
				.getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP));
		language.setSelectedItem(options.getLanguage());

		cardSet.setSelectedItem(options.getCardSet());

		savePath.setText(options.getSavePath());

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
		contraAfterBid18.setSelected(options.isContraAfterBid18(false)
				.booleanValue());
		playBock.setSelected(options.isPlayBock(false).booleanValue());
		bockEventLostAfterContra.setSelected(options
				.isBockEventLostAfterContra(false).booleanValue());
		bockEventContraReAnnounced.setSelected(options
				.isBockEventContraReCalled(false).booleanValue());
		bockEventPlayerHasX00Points.setSelected(options
				.isBockEventMultipleOfHundredScore(false).booleanValue());
		bockEventLostGrand.setSelected(options.isBockEventLostGrand(false)
				.booleanValue());
		playRamsch.setSelected(options.isPlayRamsch(false).booleanValue());
		schiebeRamsch
				.setSelected(options.isSchieberamsch(false).booleanValue());
		schiebeRamschJacksInSkat.setSelected(options
				.isSchieberamschJacksInSkat(false).booleanValue());
		ramschSkatLastTrick.setSelected(RamschSkatOwner.LAST_TRICK
				.equals(options.getRamschSkatOwner()));
		ramschSkatLoser.setSelected(RamschSkatOwner.LOSER.equals(options
				.getRamschSkatOwner()));
		ramschEventNoBid.setSelected(options.isRamschEventNoBid(false)
				.booleanValue());
		ramschEventBockRamsch.setSelected(options.isRamschEventRamschAfterBock(
				false).booleanValue());
		playRevolution.setSelected(options.isPlayRevolution(false)
				.booleanValue());

		// ISS options
		issAddress.setText(options.getString(Option.ISS_ADDRESS));
		issPort.setText(options.getInteger(Option.ISS_PORT).toString());
	}

	CardSet getSelectedCardSet() {
		return (CardSet) cardSet.getSelectedItem();
	}

	void refreshCardSet() {
		this.repaint();
		parent.repaint();
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
					result = strings.getString("english"); //$NON-NLS-1$
					break;
				case GERMAN:
					result = strings.getString("german"); //$NON-NLS-1$
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
				result = strings
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
