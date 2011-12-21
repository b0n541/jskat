/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.SkatTableOptions.RamschSkatOwners;
import org.jskat.data.SkatTableOptions.RuleSets;
import org.jskat.gui.img.CardFace;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.JSkatResourceBundle;

/**
 * Preferences dialog for JSkat
 */
public class JSkatPreferencesDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(JSkatPreferencesDialog.class);

	JSkatResourceBundle strings;
	JSkatOptions options;

	private JFrame parent;

	// general options
	private JCheckBox checkForNewVersion;
	private JComboBox language;
	private ButtonGroup cardFace;
	private JRadioButton cardFaceFrench;
	private JRadioButton cardFaceGerman;
	private JRadioButton cardFaceTournament;
	private JTextField savePath;
	JSlider waitTime;
	JCheckBox trickRemoveAfterClick;
	private ButtonGroup gameShortCut;
	private JRadioButton gameShortCutYes;
	private JRadioButton gameShortCutNo;

	// rule options
	private JRadioButton rulesetISPA;
	private JRadioButton rulesetPub;
	private JRadioButton rulesetIndividual;
	private JCheckBox playContra;
	private JCheckBox playBock;
	private JCheckBox playRamsch;
	private JCheckBox playRevolution;
	private JCheckBox bockEventLostGrand;
	private JCheckBox bockEventLostWith60;
	private JCheckBox bockEventLostAfterContra;
	private JCheckBox bockEventContraReAnnounced;
	private JCheckBox bockEventPlayerHasX00Points;
	private RamschSkatOwners ramschSkat = RamschSkatOwners.LAST_TRICK;
	private JCheckBox schiebeRamsch;
	private JCheckBox schieberRamschJacksInSkat;
	private JCheckBox ramschEventNoBid;

	private JTextField issAddress;
	private JTextField issPort;

	final ActionListener rbChange = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(rulesetISPA.isSelected()) {
				ramschEventNoBid.setEnabled(false);
				bockEventContraReAnnounced.setEnabled(false);
				bockEventLostGrand.setEnabled(false);
				bockEventLostAfterContra.setEnabled(false);
				bockEventLostWith60.setEnabled(false);
				playContra.setEnabled(false);
				playRamsch.setEnabled(false);
				playBock.setEnabled(false);
				playRevolution.setEnabled(false);
				schiebeRamsch.setEnabled(false);
				schieberRamschJacksInSkat.setEnabled(false);

				ramschEventNoBid.setSelected(false);
				bockEventContraReAnnounced.setSelected(false);
				bockEventLostGrand.setSelected(false);
				bockEventLostAfterContra.setSelected(false);
				bockEventLostWith60.setSelected(false);
				playContra.setSelected(false);
				playRamsch.setSelected(false);
				playBock.setSelected(false);
				playRevolution.setSelected(false);
				schiebeRamsch.setSelected(false);
				schieberRamschJacksInSkat.setSelected(false);
				return;
			}
			if(rulesetPub.isSelected()) {
				ramschEventNoBid.setEnabled(false);
				bockEventContraReAnnounced.setEnabled(false);
				bockEventLostGrand.setEnabled(false);
				bockEventLostAfterContra.setEnabled(false);
				bockEventLostWith60.setEnabled(false);
				playContra.setEnabled(false);
				playRamsch.setEnabled(false);
				playBock.setEnabled(false);
				playRevolution.setEnabled(false);
				schiebeRamsch.setEnabled(false);
				schieberRamschJacksInSkat.setEnabled(false);

				ramschEventNoBid.setSelected(true);
				bockEventContraReAnnounced.setSelected(true);
				bockEventLostGrand.setSelected(true);
				bockEventLostAfterContra.setSelected(true);
				bockEventLostWith60.setSelected(true);
				playContra.setSelected(true);
				playRamsch.setSelected(true);
				playBock.setSelected(true);
				playRevolution.setSelected(true);
				schiebeRamsch.setSelected(true);
				schieberRamschJacksInSkat.setSelected(false);
				return;
			}
			if(rulesetIndividual.isSelected()) {
				ramschEventNoBid.setEnabled(true);
				bockEventContraReAnnounced.setEnabled(true);
				bockEventLostGrand.setEnabled(true);
				bockEventLostAfterContra.setEnabled(true);
				bockEventLostWith60.setEnabled(true);
				playContra.setEnabled(true);
				playRamsch.setEnabled(true);
				playBock.setEnabled(true);
				playRevolution.setEnabled(true);
				schiebeRamsch.setEnabled(true);
				schieberRamschJacksInSkat.setEnabled(true);
				return;
			}
			log.warn("could not decide which button is selected...");
		}
	};

	/**
	 * Constructor
	 * 
	 * @param mainFrame
	 */
	public JSkatPreferencesDialog(JFrame mainFrame) {

		strings = JSkatResourceBundle.instance();
		options = JSkatOptions.instance();
		parent = mainFrame;

		initGUI();
	}

	private void initGUI() {

		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		setTitle(strings.getString("preferences")); //$NON-NLS-1$

		Container root = getContentPane();
		root.setLayout(new MigLayout());

		JTabbedPane prefTabs = new JTabbedPane();

		JPanel commonTab = new JPanel(new MigLayout("fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		commonTab.add(new JLabel(strings.getString("check_for_new_version"))); //$NON-NLS-1$
		commonTab.add(getCheckVersionPanel(), "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel(strings.getString("language"))); //$NON-NLS-1$
		commonTab.add(getLanguagePanel(), "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel(strings.getString("card_face"))); //$NON-NLS-1$
		commonTab.add(getCardFacePanel(), "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel(strings.getString("save_path"))); //$NON-NLS-1$
		commonTab.add(getSavePathPanel(), "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel(strings.getString("wait_time_after_trick"))); //$NON-NLS-1$
		JPanel waitTimePanel = getWaitingTimePanel(strings, options);
		commonTab.add(waitTimePanel, "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel(strings.getString("game_short_cut"))); //$NON-NLS-1$
		JPanel gameShortCutPanel = getGameShortCutPanel();
		commonTab.add(gameShortCutPanel, "wrap"); //$NON-NLS-1$

		prefTabs.add(commonTab, strings.getString("common_options")); //$NON-NLS-1$

		JPanel skatRulesTab = getRulesPanel(); 
		prefTabs.add(skatRulesTab, strings.getString("skat_rules")); //$NON-NLS-1$

		JPanel issTab = new JPanel(new MigLayout("fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		issTab.add(new JLabel(strings.getString("iss_address"))); //$NON-NLS-1$
		issTab.add(getIssAddressPanel(), "wrap"); //$NON-NLS-1$
		issTab.add(new JLabel(strings.getString("iss_port"))); //$NON-NLS-1$
		issTab.add(getIssPortPanel(), "wrap"); //$NON-NLS-1$

		prefTabs.add(issTab, strings.getString("iss")); //$NON-NLS-1$

		root.add(prefTabs, "wrap"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(new MigLayout());
		JButton start = new JButton("OK"); //$NON-NLS-1$
		start.setActionCommand("OK"); //$NON-NLS-1$
		start.addActionListener(this);
		buttonPanel.add(start);
		JButton cancel = new JButton(strings.getString("cancel")); //$NON-NLS-1$
		cancel.setActionCommand("CANCEL"); //$NON-NLS-1$
		cancel.addActionListener(this);
		buttonPanel.add(cancel);
		root.add(buttonPanel, "center"); //$NON-NLS-1$

		pack();
	}

	private JPanel getSavePathPanel() {
		savePath = new JTextField(20);
		JButton savePathButton = new JButton(strings.getString("search")); //$NON-NLS-1$
		savePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser
						.showOpenDialog(JSkatPreferencesDialog.this.parent);
				if (result == JFileChooser.APPROVE_OPTION) {
					JSkatPreferencesDialog.this.savePath.setText(fileChooser
							.getSelectedFile().getAbsolutePath());
				}
			}
		});
		JPanel savePathPanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		savePathPanel.add(savePath);
		savePathPanel.add(savePathButton);
		return savePathPanel;
	}

	private JPanel getIssAddressPanel() {

		issAddress = new JTextField(20);
		issAddress.setText(options.getIssAddress());
		JPanel issAddressPanel = new JPanel(new MigLayout(
				"fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		issAddressPanel.add(issAddress);
		return issAddressPanel;
	}

	private JPanel getIssPortPanel() {

		issPort = new JTextField(20);
		issPort.setText(options.getIssPort().toString());
		JPanel issPortPanel = new JPanel(
				new MigLayout("fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

		JPanel cardFacePanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

		waitTime.setValue(options.getTrickRemoveDelayTime());

		trickRemoveAfterClick = new JCheckBox(strings.getString("remove_trick_after_click")); //$NON-NLS-1$
		trickRemoveAfterClick.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {

				if (JSkatPreferencesDialog.this.trickRemoveAfterClick.isSelected()) {

					JSkatPreferencesDialog.this.waitTime.setEnabled(false);

				} else {

					JSkatPreferencesDialog.this.waitTime.setEnabled(true);
				}
			}
		});
		trickRemoveAfterClick.setSelected(options.isTrickRemoveAfterClick());

		JPanel waitTimePanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

		if (options.isGameShortCut()) {
			gameShortCutYes.setSelected(true);
		} else {
			gameShortCutNo.setSelected(true);
		}

		JPanel gameShortCutPanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		gameShortCutPanel.add(gameShortCutYes);
		gameShortCutPanel.add(gameShortCutNo);
		return gameShortCutPanel;
	}

	private JPanel getCheckVersionPanel() {
		checkForNewVersion = new JCheckBox(strings.getString("check_for_new_version_at_start_up")); //$NON-NLS-1$
		checkForNewVersion.setSelected(options.isCheckForNewVersionAtStartUp());

		JPanel checkVersionPanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		checkVersionPanel.add(checkForNewVersion);
		return checkVersionPanel;
	}

	private JPanel getLanguagePanel() {

		language = new JComboBox(SupportedLanguage.values());
		language.setSelectedItem(options.getLanguage());
		language.setRenderer(new LanguageComboBoxRenderer());

		JPanel languagePanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		languagePanel.add(language);
		return languagePanel;
	}

	private JPanel getRulesPanel() {

		log.debug("Loaded rules: "+options.getRules());
		
		JPanel rulesPanel = new JPanel(new MigLayout("fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ButtonGroup bg = new ButtonGroup();
		JLabel lbl = new JLabel("Spielregeln");
		rulesPanel.add(lbl, "wrap");
		rulesetISPA = new JRadioButton("ISPA rules");
		rulesetISPA.addActionListener(rbChange);
		rulesPanel.add(rulesetISPA);
		rulesetISPA.setSelected(options.getRules()==RuleSets.ISPA);
		bg.add(rulesetISPA);
		rulesetPub = new JRadioButton("pub rules");
		rulesetPub.addActionListener(rbChange);
		rulesetPub.setSelected(options.getRules()==RuleSets.PUB);
		rulesPanel.add(rulesetPub, "wrap");
		bg.add(rulesetPub);
		rulesPanel.add(new JLabel(""));
		rulesetIndividual = new JRadioButton("individual rules");
		rulesetIndividual.setSelected(options.getRules()==RuleSets.INDIVIDUAL);
		rulesetIndividual.addActionListener(rbChange);
		rulesPanel.add(rulesetIndividual, "wrap");
		bg.add(rulesetIndividual);
		rulesPanel.add(new JLabel(""));
		ramschEventNoBid = new JCheckBox("Play Ramsch when passed in");
		rulesPanel.add(ramschEventNoBid, "wrap");
		rulesPanel.add(new JLabel(""));
		playContra = new JCheckBox("Play Contra");
		rulesPanel.add(playContra, "wrap");
		rulesPanel.add(new JLabel(""));
		playRevolution = new JCheckBox("Play Revolution");
		rulesPanel.add(playRevolution, "wrap");
		rulesPanel.add(new JLabel(""));
		schiebeRamsch = new JCheckBox("Play Schieberamsch");
		schiebeRamsch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(schiebeRamsch.isSelected()) {
					schieberRamschJacksInSkat.setEnabled(true);
				}
				else {
					schieberRamschJacksInSkat.setEnabled(false);
				}
			}
		});
		rulesPanel.add(schiebeRamsch, "wrap");
		rulesPanel.add(new JLabel(""));
		schieberRamschJacksInSkat = new JCheckBox("Allow Jacks in Schieberamsch skat");
		rulesPanel.add(schieberRamschJacksInSkat, "wrap");
		rulesPanel.add(new JLabel(""));
		playBock = new JCheckBox("Play Bock");
		rulesPanel.add(playBock, "wrap");
		rulesPanel.add(new JLabel(""));
		playRamsch = new JCheckBox("Play BockRamsch");
		rulesPanel.add(playRamsch, "wrap");
		rulesPanel.add(new JLabel(""));
		lbl = new JLabel("Bock events");
		rulesPanel.add(lbl, "wrap");
		rulesPanel.add(new JLabel(""));
		bockEventLostAfterContra = new JCheckBox("lost contra game");
		rulesPanel.add(bockEventLostAfterContra, "wrap");
		rulesPanel.add(new JLabel(""));
		bockEventContraReAnnounced = new JCheckBox("contra & re");
		rulesPanel.add(bockEventContraReAnnounced, "wrap");
		rulesPanel.add(new JLabel(""));
		bockEventLostGrand = new JCheckBox("lost grand game");
		rulesPanel.add(bockEventLostGrand, "wrap");
		rulesPanel.add(new JLabel(""));
		bockEventLostWith60 = new JCheckBox("lost with 60 points");
		rulesPanel.add(bockEventLostWith60, "wrap");
		rbChange.actionPerformed(new ActionEvent(this, 0, null));
		return rulesPanel;
	}

	/**
	 * @see JDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean isVisible) {

		if (isVisible) {

			setLocationRelativeTo(parent);
		}

		super.setVisible(isVisible);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if ("CANCEL".equals(e.getActionCommand())) { //$NON-NLS-1$

			setVisible(false);
		} else if ("OK".equals(e.getActionCommand())) { //$NON-NLS-1$

			options.setCheckForNewVersionAtStartUp(checkForNewVersion.isSelected());
			options.setLanguage((SupportedLanguage) language.getSelectedItem());
			options.setCardFace(getSelectedCardFace());
			options.setSavePath(savePath.getText());
			options.setTrickRemoveDelayTime(waitTime.getValue());
			options.setTrickRemoveAfterClick(trickRemoveAfterClick.isSelected());
			options.setGameShortCut(gameShortCutYes.isSelected());
			options.setIssAddress(issAddress.getText());
			options.setIssPort(Integer.valueOf(issPort.getText()));
			
			if(rulesetISPA.isSelected()) {
				options.setRules(RuleSets.ISPA);
				options.setRamschEventNoBid(false);
				options.setBockEventContraReAnnounced(false);
				options.setBockEventLostGrand(false);
				options.setBockEventLostAfterContra(false);
				options.setBockEventLostWith60(false);
				options.setPlayContra(false);
				options.setPlayRamsch(false);
				options.setPlayBock(false);
				options.setPlayRevolution(false);
				options.setSchieberRamsch(false);
				options.setSchieberRamschJacksInSkat(false);
				options.setRamschSkat(RamschSkatOwners.LAST_TRICK);
			}
			else {
				options.setRules(rulesetPub.isSelected()?RuleSets.PUB:RuleSets.INDIVIDUAL);
				options.setRamschEventNoBid(rulesetPub.isSelected() || ramschEventNoBid.isSelected());
				options.setBockEventContraReAnnounced(rulesetPub.isSelected() || bockEventContraReAnnounced.isSelected());
				options.setBockEventLostGrand(rulesetPub.isSelected() || bockEventLostGrand.isSelected());
				options.setBockEventLostAfterContra(rulesetPub.isSelected() || bockEventLostAfterContra.isSelected());
				options.setBockEventLostWith60(rulesetPub.isSelected() || bockEventLostWith60.isSelected());
				options.setPlayContra(rulesetPub.isSelected() || playContra.isSelected());
				options.setPlayRamsch(rulesetPub.isSelected() || playRamsch.isSelected());
				options.setPlayBock(rulesetPub.isSelected() || playBock.isSelected());
				options.setPlayRevolution(rulesetPub.isSelected() || playRevolution.isSelected());
				options.setSchieberRamsch(rulesetPub.isSelected() || schiebeRamsch.isSelected());
				options.setSchieberRamschJacksInSkat(!rulesetPub.isSelected() && schieberRamschJacksInSkat.isSelected());
				options.setRamschSkat(RamschSkatOwners.LAST_TRICK);
			}

			options.saveJSkatProperties();
			refreshCardFaces();

			setVisible(false);
		}

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
