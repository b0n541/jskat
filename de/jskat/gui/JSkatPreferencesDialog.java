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
	
	public JSkatPreferencesDialog(JSkatMaster skatMaster, JFrame mainFrame) {
		
		this.jskat = skatMaster;
		this.parent = mainFrame;
		
		initGUI();
	}
	
	private void initGUI() {
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(false);
		
		this.setTitle("Prefences");
		
		Container root = this.getContentPane();
		root.setLayout(new MigLayout());
		
		JTabbedPane prefTabs = new JTabbedPane();
		
		JPanel commonTab = new JPanel(new MigLayout("fill", "fill", "fill"));

		commonTab.add(new JLabel("Language"));
		String[] data = {"English", "German"};
		this.language = new JComboBox(data);
		JPanel languagePanel = new JPanel(new MigLayout("fill", "fill", "fill"));
		languagePanel.add(this.language);
		commonTab.add(languagePanel, "wrap");
		
		commonTab.add(new JLabel("Card face"));
		this.cardFace = new ButtonGroup();
		this.cardFaceFrench = new JRadioButton("french");
		this.cardFaceFrench.setSelected(true);
		this.cardFace.add(cardFaceFrench);
		this.cardFaceGerman = new JRadioButton("german");
		this.cardFaceGerman.setSelected(true);
		this.cardFace.add(cardFaceGerman);
		this.cardFaceTournament= new JRadioButton("tournament");
		this.cardFaceTournament.setSelected(true);
		this.cardFace.add(cardFaceTournament);
		JPanel cardFacePanel = new JPanel(new MigLayout("fill", "fill", "fill"));
		cardFacePanel.add(cardFaceFrench);
		cardFacePanel.add(cardFaceGerman);
		cardFacePanel.add(cardFaceTournament);
		commonTab.add(cardFacePanel, "wrap");

		commonTab.add(new JLabel("Save path"));
		this.savePath = new JTextField(20);
		JButton savePathButton = new JButton("Search");
		savePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(parent);
				if (result == JFileChooser.APPROVE_OPTION) {
					savePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		JPanel savePathPanel = new JPanel(new MigLayout("fill", "fill", "fill"));
		savePathPanel.add(this.savePath);
		savePathPanel.add(savePathButton);
		commonTab.add(savePathPanel, "wrap");

		commonTab.add(new JLabel("Wait time after trick"));
		this.waitTime = new JSlider();
		this.waitTime.setSnapToTicks(true);
		this.waitTime.setMinimum(0);
		this.waitTime.setMaximum(20);
		this.waitTime.setMajorTickSpacing(5);
		this.waitTime.setMinorTickSpacing(1);
		this.waitTime.setPaintTicks(true);
		this.waitTime.setPaintLabels(true);
		this.waitTime.setValue(1);
		this.trickRemoveAfterClick = new JCheckBox("Remove trick after click");
		this.trickRemoveAfterClick.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {

				if (trickRemoveAfterClick.isSelected()) {

					waitTime.setEnabled(false);

				} else {

					waitTime.setEnabled(true);
				}
			}
		});
		JPanel waitTimePanel = new JPanel(new MigLayout("fill", "fill", "fill"));
		waitTimePanel.add(this.waitTime);
		waitTimePanel.add(this.trickRemoveAfterClick);
		commonTab.add(waitTimePanel, "wrap");
		
		commonTab.add(new JLabel("Game short cut"));
		this.gameShortCut = new ButtonGroup();
		this.gameShortCutYes = new JRadioButton("Yes");
		this.gameShortCut.add(this.gameShortCutYes);
		this.gameShortCutNo = new JRadioButton("No");
		this.gameShortCutNo.setSelected(true);
		this.gameShortCut.add(this.gameShortCutNo);
		JPanel gameShortCutPanel = new JPanel(new MigLayout("fill", "fill", "fill"));
		gameShortCutPanel.add(this.gameShortCutYes);
		gameShortCutPanel.add(this.gameShortCutNo);
		commonTab.add(gameShortCutPanel, "wrap");
		
		prefTabs.add(commonTab, "Common");
		
		JPanel skatRulesTab = new JPanel(new MigLayout("fill", "fill", "fill"));
		prefTabs.add(skatRulesTab, "Skat rules");

		root.add(prefTabs, "wrap");
		
		JPanel buttonPanel = new JPanel(new MigLayout());
		JButton start = new JButton("OK");
		start.setActionCommand("OK");
		start.addActionListener(this);
		buttonPanel.add(start);
		JButton cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL");
		cancel.addActionListener(this);
		buttonPanel.add(cancel);
		root.add(buttonPanel, "center");
		
		this.pack();
	}

	/**
	 * @see JDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean isVisible) {
		
		if (isVisible) {
		
			this.setLocationRelativeTo(this.parent);
		}
		
		super.setVisible(isVisible);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ("CANCEL".equals(e.getActionCommand())) {
			
			this.setVisible(false);
		}
		else if ("OK".equals(e.getActionCommand())) {
			
			// TODO set new preferences
			
			this.setVisible(false);
		}
		
	}
}
