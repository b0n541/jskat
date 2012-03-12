/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
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
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.SkatTableOptions.RamschSkatOwner;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.gui.img.CardFace;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.JSkatResourceBundle;

/**
 * Preferences dialog for JSkat
 */
public class JSkatOptionsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(JSkatOptionsDialog.class);

	JSkatResourceBundle strings;
	JSkatOptions options;

	private JFrame parent;

	// general options
	private JCheckBox showTipsAtStartUp;
	private JCheckBox checkForNewVersion;
	private JComboBox language;
	private ButtonGroup cardFace;
	private JRadioButton cardFaceFrench;
	private JRadioButton cardFaceGerman;
	private JRadioButton cardFaceTournament;
	JTextField savePath;
	JSlider waitTime;
	JCheckBox trickRemoveAfterClick;
	private ButtonGroup gameShortCut;
	private JRadioButton gameShortCutYes;
	private JRadioButton gameShortCutNo;

	// rule options
	JRadioButton ruleSetISPA;
	JRadioButton ruleSetPub;
	private JCheckBox playContra;
	private JCheckBox contraAfterBid18;
	private JCheckBox playBock;
	private JCheckBox playRamsch;
	private JCheckBox playRevolution;
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
		public void actionPerformed(ActionEvent e) {
			options.setShowTipsAtStartUp(showTipsAtStartUp.isSelected());
			options.setCheckForNewVersionAtStartUp(checkForNewVersion.isSelected());
			options.setLanguage((SupportedLanguage) language.getSelectedItem());
			options.setCardFace(getSelectedCardFace());
			options.setSavePath(savePath.getText());
			options.setTrickRemoveDelayTime(waitTime.getValue() * 1000);
			options.setTrickRemoveAfterClick(trickRemoveAfterClick.isSelected());
			options.setGameShortCut(gameShortCutYes.isSelected());
			options.setIssAddress(issAddress.getText());
			options.setIssPort(Integer.valueOf(issPort.getText()));

			if (ruleSetISPA.isSelected()) {
				options.setRules(RuleSet.ISPA);
			} else {
				options.setRules(RuleSet.PUB);
			}

			options.setRamschEventNoBid(ramschEventNoBid.isSelected());
			options.setBockEventContraReAnnounced(bockEventContraReAnnounced.isSelected());
			options.setBockEventLostGrand(bockEventLostGrand.isSelected());
			options.setBockEventLostAfterContra(bockEventLostAfterContra.isSelected());
			options.setBockEventLostWith60(bockEventLostWith60.isSelected());
			options.setPlayContra(playContra.isSelected());
			options.setPlayRamsch(playRamsch.isSelected());
			options.setPlayBock(playBock.isSelected());
			options.setPlayRevolution(playRevolution.isSelected());
			options.setSchieberRamsch(schiebeRamsch.isSelected());
			options.setSchieberRamschJacksInSkat(schiebeRamschJacksInSkat.isSelected());
			options.setRamschSkatOwner(ramschSkatLastTrick.isSelected() ? RamschSkatOwner.LAST_TRICK
					: RamschSkatOwner.LOSER);

			options.saveJSkatProperties();
			refreshCardFaces();

			setVisible(false);
		}
	};
	
	private final Action cancelAction = new AbstractAction("CANCEL") {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	};

	final ChangeListener ruleButtonChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
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
	public JSkatOptionsDialog(JFrame mainFrame) {

		strings = JSkatResourceBundle.instance();
		options = JSkatOptions.instance();
		parent = mainFrame;

		initGUI();
	}

	private void initGUI() {

		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(true);

		setTitle(strings.getString("preferences")); //$NON-NLS-1$

		JPanel root = new JPanel(LayoutFactory.getMigLayout());

		JTabbedPane prefTabs = new JTabbedPane();

		JPanel commonTab = getCommonPanel();
		log.debug(commonTab.getPreferredSize());
		prefTabs.addTab(strings.getString("common_options"), commonTab); //$NON-NLS-1$

		JPanel skatRulesTab = getSkatRulesPanel();
		log.debug(skatRulesTab.getPreferredSize());
		prefTabs.addTab(strings.getString("skat_rules"), skatRulesTab); //$NON-NLS-1$

		JPanel issTab = getIssPanel();
		log.debug(issTab.getPreferredSize());
		prefTabs.addTab(strings.getString("iss"), issTab); //$NON-NLS-1$

		root.add(prefTabs, "wrap"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout());
		JButton start = new JButton(); 
		start.setAction(okAction);
		start.setText("OK"); //$NON-NLS-1$
		buttonPanel.add(start);
		JButton cancel = new JButton(); 
		cancel.setAction(cancelAction);
		cancel.setText(strings.getString("cancel")); //$NON-NLS-1$
		buttonPanel.add(cancel);

		root.add(buttonPanel, "center"); //$NON-NLS-1$
		
		InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = root.getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
		am.put("OK", okAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
		am.put("CANCEL", cancelAction);
		
		setContentPane(root);

		pack();
	}

	private JPanel getIssPanel() {
		JPanel issPanel = new JPanel(LayoutFactory.getMigLayout()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		issPanel.add(new JLabel(strings.getString("iss_address")), "shrinky"); //$NON-NLS-1$
		issPanel.add(getIssAddressPanel(), "shrinky, wrap"); //$NON-NLS-1$
		issPanel.add(new JLabel(strings.getString("iss_port")), "shrinky"); //$NON-NLS-1$
		issPanel.add(getIssPortPanel(), "shrinky, wrap"); //$NON-NLS-1$

		return issPanel;
	}

	private JPanel getSavePathPanel() {
		savePath = new JTextField(20);
		savePath.setEditable(false);
		JButton savePathButton = new JButton(strings.getString("search")); //$NON-NLS-1$
		savePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setFileHidingEnabled(false);
				fileChooser.setCurrentDirectory(new File(savePath.getText()));
				int result = fileChooser.showOpenDialog(JSkatOptionsDialog.this.parent);
				if (result == JFileChooser.APPROVE_OPTION) {
					JSkatOptionsDialog.this.savePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		JPanel savePathPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		savePathPanel.add(savePath);
		savePathPanel.add(savePathButton);
		return savePathPanel;
	}

	private JPanel getIssAddressPanel() {

		issAddress = new JTextField(20);
		JPanel issAddressPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		issAddressPanel.add(issAddress);

		return issAddressPanel;
	}

	private JPanel getIssPortPanel() {

		issPort = new JTextField(20);
		JPanel issPortPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		issPortPanel.add(issPort);
		return issPortPanel;
	}

	private JPanel getCardFacePanel() {

		cardFace = new ButtonGroup();
		cardFaceFrench = new JRadioButton(strings.getString("card_face_french")); //$NON-NLS-1$
		cardFaceFrench.getModel().setActionCommand(CardFace.FRENCH.name());
		cardFace.add(cardFaceFrench);
		cardFaceGerman = new JRadioButton(strings.getString("card_face_german")); //$NON-NLS-1$
		cardFaceGerman.getModel().setActionCommand(CardFace.GERMAN.name());
		cardFace.add(cardFaceGerman);
		cardFaceTournament = new JRadioButton(strings.getString("card_face_tournament")); //$NON-NLS-1$
		cardFaceTournament.getModel().setActionCommand(CardFace.TOURNAMENT.name());
		cardFace.add(cardFaceTournament);

		JPanel cardFacePanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cardFacePanel.add(cardFaceFrench);
		cardFacePanel.add(cardFaceGerman);
		cardFacePanel.add(cardFaceTournament);

		return cardFacePanel;
	}

	private JPanel getWaitingTimePanel(JSkatResourceBundle strings, JSkatOptions options) {

		waitTime = new JSlider();
		waitTime.setSnapToTicks(true);
		waitTime.setMinimum(0);
		waitTime.setMaximum(20);
		waitTime.setMajorTickSpacing(5);
		waitTime.setMinorTickSpacing(1);
		waitTime.setPaintTicks(true);
		waitTime.setPaintLabels(true);

		trickRemoveAfterClick = new JCheckBox(strings.getString("remove_trick_after_click")); //$NON-NLS-1$
		trickRemoveAfterClick.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {

				if (JSkatOptionsDialog.this.trickRemoveAfterClick.isSelected()) {

					JSkatOptionsDialog.this.waitTime.setEnabled(false);

				} else {

					JSkatOptionsDialog.this.waitTime.setEnabled(true);
				}
			}
		});

		JPanel waitTimePanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		waitTimePanel.add(waitTime);
		waitTimePanel.add(trickRemoveAfterClick);
		return waitTimePanel;
	}

	private JPanel getGameShortCutPanel() {

		gameShortCut = new ButtonGroup();
		gameShortCutYes = new JRadioButton(strings.getString("yes")); //$NON-NLS-1$
		gameShortCut.add(gameShortCutYes);
		gameShortCutNo = new JRadioButton(strings.getString("no")); //$NON-NLS-1$
		gameShortCut.add(gameShortCutNo);

		JPanel gameShortCutPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		gameShortCutPanel.add(gameShortCutYes);
		gameShortCutPanel.add(gameShortCutNo);
		return gameShortCutPanel;
	}

	private JPanel getShowTipsPanel() {
		showTipsAtStartUp = new JCheckBox(strings.getString("show_tips_at_startup")); //$NON-NLS-1$
		JPanel showTipsPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		showTipsPanel.add(showTipsAtStartUp);
		return showTipsPanel;
	}

	private JPanel getCheckVersionPanel() {
		checkForNewVersion = new JCheckBox(strings.getString("check_for_new_version_at_startup")); //$NON-NLS-1$
		JPanel checkVersionPanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		checkVersionPanel.add(checkForNewVersion);
		return checkVersionPanel;
	}

	private JPanel getLanguagePanel() {
		language = new JComboBox(SupportedLanguage.values());
		language.setRenderer(new LanguageComboBoxRenderer());
		JPanel languagePanel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		languagePanel.add(language);
		return languagePanel;
	}

	private JPanel getCommonPanel() {

		JPanel commonPanel = new JPanel(LayoutFactory.getMigLayout());

		commonPanel.add(new JLabel(strings.getString("show_tips"))); //$NON-NLS-1$
		commonPanel.add(getShowTipsPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("check_for_new_version"))); //$NON-NLS-1$
		commonPanel.add(getCheckVersionPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("language"))); //$NON-NLS-1$
		commonPanel.add(getLanguagePanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("card_face"))); //$NON-NLS-1$
		commonPanel.add(getCardFacePanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("save_path"))); //$NON-NLS-1$
		commonPanel.add(getSavePathPanel(), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("wait_time_after_trick"))); //$NON-NLS-1$
		commonPanel.add(getWaitingTimePanel(strings, options), "wrap"); //$NON-NLS-1$

		commonPanel.add(new JLabel(strings.getString("game_short_cut"))); //$NON-NLS-1$
		commonPanel.add(getGameShortCutPanel(), "wrap"); //$NON-NLS-1$

		return commonPanel;
	}

	private JPanel getSkatRulesPanel() {

		log.debug("Loaded rules: " + options.getRules());

		JPanel rulesPanel = new JPanel(LayoutFactory.getMigLayout());

		ButtonGroup ruleSetGroup = new ButtonGroup();
		ruleSetISPA = new JRadioButton(strings.getString("ispa_rules")); //$NON-NLS-1$
		ruleSetISPA.addChangeListener(ruleButtonChangeListener);
		ruleSetGroup.add(ruleSetISPA);
		ruleSetPub = new JRadioButton(strings.getString("pub_rules")); //$NON-NLS-1$
		ruleSetPub.addChangeListener(ruleButtonChangeListener);
		ruleSetGroup.add(ruleSetPub);

		rulesPanel.add(ruleSetISPA, "wrap"); //$NON-NLS-1$
		rulesPanel.add(ruleSetPub, "wrap"); //$NON-NLS-1$

		JPanel pubRulesPanel = new JPanel(LayoutFactory.getMigLayout());

		JButton resetButton = new JButton(
				strings.getString("reset_to_defaults")); //$NON-NLS-1$
		//		pubRulesPanel.add(resetButton, "wrap"); //$NON-NLS-1$

		JPanel contraPanel = new JPanel(LayoutFactory.getMigLayout());

		playContra = new JCheckBox(strings.getString("play_contra_re")); //$NON-NLS-1$
		//		contraPanel.add(playContra, "wrap"); //$NON-NLS-1$

		contraAfterBid18 = new JCheckBox(strings.getString("contra_after_bid_18")); //$NON-NLS-1$
		//		contraPanel.add(contraAfterBid18, "gapleft 20px"); //$NON-NLS-1$

		//		pubRulesPanel.add(contraPanel, "wrap"); //$NON-NLS-1$

		JPanel bockPanel = new JPanel(LayoutFactory.getMigLayout()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		playBock = new JCheckBox(strings.getString("play_bock")); //$NON-NLS-1$
		//		bockPanel.add(playBock, "wrap"); //$NON-NLS-1$

		JPanel bockDetailsPanel = new JPanel(LayoutFactory.getMigLayout());

		bockEventLabel = new JLabel(strings.getString("bock_events")); //$NON-NLS-1$
		//		bockDetailsPanel.add(bockEventLabel, "span 2, wrap"); //$NON-NLS-1$
		bockEventLostAfterContra = new JCheckBox(strings.getString("bock_event_lost_contra")); //$NON-NLS-1$
		// bockDetailsPanel.add(bockEventLostAfterContra);
		bockEventLostWith60 = new JCheckBox(strings.getString("bock_event_lost_game_with_60")); //$NON-NLS-1$
		bockEventLostWith60.setSelected(options.isBockEventLostWith60(false).booleanValue());
		//		bockDetailsPanel.add(bockEventLostWith60, "wrap"); //$NON-NLS-1$
		bockEventContraReAnnounced = new JCheckBox(strings.getString("bock_event_contra_re")); //$NON-NLS-1$
		// bockDetailsPanel.add(bockEventContraReAnnounced);
		bockEventPlayerHasX00Points = new JCheckBox(strings.getString("bock_event_player_x00_points")); //$NON-NLS-1$
		//		bockDetailsPanel.add(bockEventPlayerHasX00Points, "wrap"); //$NON-NLS-1$
		bockEventLostGrand = new JCheckBox(strings.getString("bock_event_lost_grand")); //$NON-NLS-1$
		// bockDetailsPanel.add(bockEventLostGrand);
		//		bockPanel.add(bockDetailsPanel, "gapleft 20px"); //$NON-NLS-1$

		//		pubRulesPanel.add(bockPanel, "wrap"); //$NON-NLS-1$

		JPanel ramschPanel = new JPanel(LayoutFactory.getMigLayout());

		playRamsch = new JCheckBox(strings.getString("play_ramsch")); //$NON-NLS-1$
		ramschPanel.add(playRamsch, "wrap"); //$NON-NLS-1$

		JPanel schiebeRamschPanel = new JPanel(LayoutFactory.getMigLayout());

		schiebeRamsch = new JCheckBox(strings.getString("schieberamsch")); //$NON-NLS-1$
		schiebeRamschPanel.add(schiebeRamsch, "gapleft 20px, wrap"); //$NON-NLS-1$

		schiebeRamschJacksInSkat = new JCheckBox(strings.getString("schieberamsch_jacks_in_skat")); //$NON-NLS-1$
		//		schiebeRamschPanel.add(schiebeRamschJacksInSkat, "gapleft 40px, wrap"); //$NON-NLS-1$

		ramschPanel.add(schiebeRamschPanel, "wrap"); //$NON-NLS-1$

		JPanel ramschEventPanel = new JPanel(LayoutFactory.getMigLayout());

		ramschEventLabel = new JLabel(strings.getString("ramsch_events")); //$NON-NLS-1$
		ramschEventPanel.add(ramschEventLabel, "span 2, wrap"); //$NON-NLS-1$
		ramschEventNoBid = new JCheckBox(strings.getString("ramsch_event_no_bid")); //$NON-NLS-1$
		ramschEventPanel.add(ramschEventNoBid);
		ramschEventBockRamsch = new JCheckBox(strings.getString("ramsch_event_bock_ramsch")); //$NON-NLS-1$
		// ramschEventPanel.add(ramschEventBockRamsch);

		ramschPanel.add(ramschEventPanel, "gapleft 20px, wrap"); //$NON-NLS-1$

		JPanel ramschSkatPanel = new JPanel(LayoutFactory.getMigLayout());
		ramschSkatLabel = new JLabel(strings.getString("ramsch_skat_owner")); //$NON-NLS-1$
		ramschSkatPanel.add(ramschSkatLabel, "wrap"); //$NON-NLS-1$
		ramschSkatLastTrick = new JRadioButton(strings.getString("ramsch_skat_last_trick")); //$NON-NLS-1$
		ramschSkatPanel.add(ramschSkatLastTrick, "wrap"); //$NON-NLS-1$
		ramschSkatLoser = new JRadioButton(strings.getString("ramsch_skat_loser")); //$NON-NLS-1$
		ramschSkatPanel.add(ramschSkatLoser);
		ramschPanel.add(ramschSkatPanel, "gapleft 20px"); //$NON-NLS-1$

		ramschSkatOwner = new ButtonGroup();
		ramschSkatOwner.add(ramschSkatLastTrick);
		ramschSkatOwner.add(ramschSkatLoser);

		pubRulesPanel.add(ramschPanel, "wrap"); //$NON-NLS-1$

		playRevolution = new JCheckBox(strings.getString("play_revolution")); //$NON-NLS-1$
		// pubRulesPanel.add(playRevolution);

		rulesPanel.add(pubRulesPanel, "gapleft 20px"); //$NON-NLS-1$

		return rulesPanel;
	}

	void activatePubRules(boolean isActivated) {

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
	public void setVisible(boolean isVisible) {

		if (isVisible) {
			setLocationRelativeTo(parent);
		}

		setOptionValues();

		super.setVisible(isVisible);
	}

	private void setOptionValues() {
		// common options
		showTipsAtStartUp.setSelected(options.isShowTipsAtStartUp().booleanValue());
		checkForNewVersion.setSelected(options.isCheckForNewVersionAtStartUp().booleanValue());
		language.setSelectedItem(options.getLanguage());

		switch (options.getCardFace()) {
		case FRENCH:
			cardFaceFrench.setSelected(true);
			break;
		case GERMAN:
			cardFaceGerman.setSelected(true);
			break;
		case TOURNAMENT:
			cardFaceTournament.setSelected(true);
			break;
		}

		savePath.setText(options.getSavePath());
		waitTime.setValue(options.getTrickRemoveDelayTime().intValue() / 1000);
		trickRemoveAfterClick.setSelected(options.isTrickRemoveAfterClick().booleanValue());

		if (options.isGameShortCut().booleanValue()) {
			gameShortCutYes.setSelected(true);
		} else {
			gameShortCutNo.setSelected(true);
		}

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
		bockEventContraReAnnounced.setSelected(options.isBockEventContraReAnnounced(false).booleanValue());
		bockEventPlayerHasX00Points.setSelected(options.isBockEventPlayerHasX00Points(false).booleanValue());
		bockEventLostGrand.setSelected(options.isBockEventLostGrand(false).booleanValue());
		playRamsch.setSelected(options.isPlayRamsch(false).booleanValue());
		schiebeRamsch.setSelected(options.isSchieberRamsch(false).booleanValue());
		schiebeRamschJacksInSkat.setSelected(options.isSchieberRamschJacksInSkat(false).booleanValue());
		ramschSkatLastTrick.setSelected(RamschSkatOwner.LAST_TRICK.equals(options.getRamschSkat()));
		ramschSkatLoser.setSelected(RamschSkatOwner.LOSER.equals(options.getRamschSkat()));
		ramschEventNoBid.setSelected(options.isRamschEventNoBid(false).booleanValue());
		ramschEventBockRamsch.setSelected(options.isRamschEventRamschAfterBock(false).booleanValue());
		playRevolution.setSelected(options.isPlayRevolution(false).booleanValue());

		// ISS options
		issAddress.setText(options.getIssAddress());
		issPort.setText(options.getIssPort().toString());
	}

	private CardFace getSelectedCardFace() {
		ButtonModel model = cardFace.getSelection();
		return CardFace.valueOf(model.getActionCommand());
	}

	private void refreshCardFaces() {

		JSkatGraphicRepository.instance().reloadCards(options.getCardFace());
		parent.repaint();
	}

	private class LanguageComboBoxRenderer extends AbstractI18NComboBoxRenderer {

		private static final long serialVersionUID = 1L;

		LanguageComboBoxRenderer() {
			super();
		}

		@Override
		public String getValueText(Object value) {

			String result = " "; //$NON-NLS-1$

			SupportedLanguage language = (SupportedLanguage) value;

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
}
