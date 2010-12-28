/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
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
import de.jskat.control.JSkatMaster;
import de.jskat.data.JSkatOptions;

/**
 * Preferences dialog for JSkat
 */
public class JSkatPreferencesDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JSkatMaster jskat;
	private JFrame parent;

	private JComboBox language;
	private ButtonGroup cardFace;
	private JRadioButton cardFaceFrench;
	private JRadioButton cardFaceGerman;
	private JRadioButton cardFaceTournament;
	private JTextField savePath;
	private JSlider waitTime;
	private JCheckBox trickRemoveAfterClick;
	private ButtonGroup gameShortCut;
	private JRadioButton gameShortCutYes;
	private JRadioButton gameShortCutNo;

	/**
	 * Constructor
	 * 
	 * @param skatMaster
	 * @param mainFrame
	 */
	public JSkatPreferencesDialog(JFrame mainFrame) {

		jskat = JSkatMaster.instance();
		parent = mainFrame;

		initGUI(JSkatOptions.instance());
	}

	private void initGUI(JSkatOptions options) {

		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		setTitle("Prefences");

		Container root = getContentPane();
		root.setLayout(new MigLayout());

		JTabbedPane prefTabs = new JTabbedPane();

		JPanel commonTab = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		commonTab.add(new JLabel("Language"));
		String[] data = { "English", "German" };
		language = new JComboBox(data);
		JPanel languagePanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$
		languagePanel.add(language);
		commonTab.add(languagePanel, "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel("Card face"));
		cardFace = new ButtonGroup();
		cardFaceFrench = new JRadioButton("french");
		cardFaceFrench.setSelected(true);
		cardFace.add(cardFaceFrench);
		cardFaceGerman = new JRadioButton("german");
		cardFaceGerman.setSelected(true);
		cardFace.add(cardFaceGerman);
		cardFaceTournament = new JRadioButton("tournament");
		cardFaceTournament.setSelected(true);
		cardFace.add(cardFaceTournament);
		JPanel cardFacePanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cardFacePanel.add(cardFaceFrench);
		cardFacePanel.add(cardFaceGerman);
		cardFacePanel.add(cardFaceTournament);
		commonTab.add(cardFacePanel, "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel("Save path"));
		savePath = new JTextField(20);
		JButton savePathButton = new JButton("Search");
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
		commonTab.add(savePathPanel, "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel("Wait time after trick"));
		waitTime = new JSlider();
		waitTime.setSnapToTicks(true);
		waitTime.setMinimum(0);
		waitTime.setMaximum(20);
		waitTime.setMajorTickSpacing(5);
		waitTime.setMinorTickSpacing(1);
		waitTime.setPaintTicks(true);
		waitTime.setPaintLabels(true);
		waitTime.setValue(0);
		trickRemoveAfterClick = new JCheckBox("Remove trick after click");
		trickRemoveAfterClick.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {

				if (JSkatPreferencesDialog.this.trickRemoveAfterClick
						.isSelected()) {

					JSkatPreferencesDialog.this.waitTime.setEnabled(false);

				} else {

					JSkatPreferencesDialog.this.waitTime.setEnabled(true);
				}
			}
		});
		JPanel waitTimePanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		waitTimePanel.add(waitTime);
		waitTimePanel.add(trickRemoveAfterClick);
		commonTab.add(waitTimePanel, "wrap"); //$NON-NLS-1$

		commonTab.add(new JLabel("Game short cut"));
		gameShortCut = new ButtonGroup();
		gameShortCutYes = new JRadioButton("Yes");
		gameShortCut.add(gameShortCutYes);
		gameShortCutNo = new JRadioButton("No");
		gameShortCutNo.setSelected(true);
		gameShortCut.add(gameShortCutNo);
		JPanel gameShortCutPanel = new JPanel(new MigLayout(
				"fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		gameShortCutPanel.add(gameShortCutYes);
		gameShortCutPanel.add(gameShortCutNo);
		commonTab.add(gameShortCutPanel, "wrap"); //$NON-NLS-1$

		prefTabs.add(commonTab, "Common");

		JPanel skatRulesTab = new JPanel(new MigLayout("fill", "fill", "fill"));
		prefTabs.add(skatRulesTab, "Skat rules");

		root.add(prefTabs, "wrap"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(new MigLayout());
		JButton start = new JButton("OK");
		start.setActionCommand("OK"); //$NON-NLS-1$
		start.addActionListener(this);
		buttonPanel.add(start);
		JButton cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL"); //$NON-NLS-1$
		cancel.addActionListener(this);
		buttonPanel.add(cancel);
		root.add(buttonPanel, "center"); //$NON-NLS-1$

		pack();
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

			JSkatOptions options = JSkatOptions.instance();
			options.saveJSkatProperties();

			setVisible(false);
		}

	}
}
