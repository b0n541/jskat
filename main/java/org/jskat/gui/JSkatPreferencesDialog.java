/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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

import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.gui.img.CardFace;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.JSkatResourceBundle;

/**
 * Preferences dialog for JSkat
 */
public class JSkatPreferencesDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	JSkatResourceBundle strings;
	JSkatOptions options;

	private JFrame parent;

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

	private JTextField issAddress;
	private JTextField issPort;

	/**
	 * Constructor
	 * 
	 * @param skatMaster
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

		JPanel skatRulesTab = new JPanel(
				new MigLayout("fill", "fill", "shrink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

	private JPanel getLanguagePanel() {

		language = new JComboBox(SupportedLanguage.values());
		language.setSelectedItem(options.getLanguage());
		language.setRenderer(new LanguageComboBoxRenderer());

		JPanel languagePanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		languagePanel.add(language);
		return languagePanel;
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

			options.setLanguage((SupportedLanguage) language.getSelectedItem());
			options.setCardFace(getSelectedCardFace());
			options.setSavePath(savePath.getText());
			options.setTrickRemoveDelayTime(waitTime.getValue());
			options.setTrickRemoveAfterClick(trickRemoveAfterClick.isSelected());
			options.setGameShortCut(gameShortCutYes.isSelected());
			options.setIssAddress(issAddress.getText());
			options.setIssPort(Integer.valueOf(issPort.getText()));

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
