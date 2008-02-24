/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.options;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.DefaultComboBoxModel;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import jskat.control.JSkatMaster;
import jskat.data.JSkatDataModel;
import jskat.data.JSkatOptions;
import jskat.data.SkatTableOptions;

/**
 * Option dialog
 */
public class JSkatOptionsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2796992488427222149L;

	/**
	 * Creates new Options dialog
	 * 
	 * @param jskatMaster
	 *            The JSkatMaster that controls the game
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param parent
	 *            The parent JFrame
	 * @param modal
	 *            TRUE if the dialog is modal
	 */
	public JSkatOptionsDialog(JSkatMaster jskatMaster,
			JSkatDataModel dataModel, JFrame parent, boolean modal) {

		super(parent, modal);

		this.jskatMaster = jskatMaster;
		this.jskatStrings = dataModel.getResourceBundle();
		this.parent = parent;

		initComponents();
		setToInitialState();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(jskatStrings.getString("options"));
		setResizable(false);

		// First the two Buttons OK and Cancel
		widgetPanel = new JPanel();
		widgetPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		okButton = new JButton(jskatStrings.getString("ok"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				okButtonPressed();
			}
		});
		widgetPanel.add(okButton);

		cancelButton = new JButton(jskatStrings.getString("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelButtonPressed();
			}
		});
		widgetPanel.add(cancelButton);

		getContentPane().add(widgetPanel, BorderLayout.SOUTH);

		/*
		 * The General tab
		 */

		// Language selector
		fieldLabel = new JLabel(jskatStrings.getString("language"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fieldLabelPanel.add(fieldLabel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel = new JPanel();
		widgetPanel.setLayout(new GridBagLayout());
		widgetPanel.add(fieldLabelPanel, gridBagConstraints);

		language = new JComboBox();
		comboBoxModel = new DefaultComboBoxModel();
		comboBoxModel.addElement(jskatStrings.getString("german"));
		comboBoxModel.addElement(jskatStrings.getString("english"));
		language.setModel(comboBoxModel);
		language.setPreferredSize(new Dimension(150, 22));
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(language);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		// Card face option group
		fieldLabel = new JLabel(jskatStrings.getString("card_face"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fieldLabelPanel.add(fieldLabel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldLabelPanel, gridBagConstraints);

		cardFace = new ButtonGroup();
		cardFaceFrench = new JRadioButton(jskatStrings.getString("french"));
		cardFaceFrench.setSelected(true);
		cardFace.add(cardFaceFrench);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(cardFaceFrench);
		cardFaceGerman = new JRadioButton(jskatStrings.getString("german2"));
		cardFace.add(cardFaceGerman);
		fieldPanel.add(cardFaceGerman);
		cardFaceTournament = new JRadioButton(jskatStrings
				.getString("tournament"));
		cardFace.add(cardFaceTournament);
		fieldPanel.add(cardFaceTournament);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		// Save path definition
		fieldLabel = new JLabel(jskatStrings.getString("save_path"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fieldLabelPanel.add(fieldLabel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldLabelPanel, gridBagConstraints);

		savePath = new JTextField();
		savePath.setPreferredSize(new Dimension(200, 22));
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(savePath);
		savePathSearch = new JButton(jskatStrings.getString("select"));
		fieldPanel.add(savePathSearch);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		// Trick remove delay
		fieldLabel = new JLabel(jskatStrings.getString("trick_remove_time"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fieldLabelPanel.add(fieldLabel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldLabelPanel, gridBagConstraints);

		trickRemoveDelayTime = new JSlider();
		trickRemoveDelayTime.setSnapToTicks(true);
		trickRemoveDelayTime.setMinimum(0);
		trickRemoveDelayTime.setMaximum(20);
		trickRemoveDelayTime.setMajorTickSpacing(5);
		trickRemoveDelayTime.setMinorTickSpacing(1);
		trickRemoveDelayTime.setPaintTicks(true);
		trickRemoveDelayTime.setPaintLabels(true);
		trickRemoveDelayTime.setValue(1);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(trickRemoveDelayTime);
		trickRemoveAfterClick = new JCheckBox(jskatStrings
				.getString("click_to_remove_trick"));
		trickRemoveAfterClick.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {

				if (trickRemoveAfterClick.isSelected()) {

					trickRemoveDelayTime.setEnabled(false);

				} else {

					trickRemoveDelayTime.setEnabled(true);
				}
			}
		});
		fieldPanel.add(trickRemoveAfterClick);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		// Shortcut yes/no
		fieldLabel = new JLabel(jskatStrings.getString("shortcut_game"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fieldLabelPanel.add(fieldLabel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldLabelPanel, gridBagConstraints);

		gameShortCut = new ButtonGroup();
		gameShortCutYes = new JRadioButton(jskatStrings.getString("yes"));
		gameShortCut.add(gameShortCutYes);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(gameShortCutYes);
		gameShortCutNo = new JRadioButton(jskatStrings.getString("no"));
		gameShortCutNo.setSelected(true);
		gameShortCut.add(gameShortCutNo);
		fieldPanel.add(gameShortCutNo);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		// Cheat/Debug mode yes/no
		fieldLabel = new JLabel(jskatStrings.getString("cheat_debug_mode"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fieldLabelPanel.add(fieldLabel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldLabelPanel, gridBagConstraints);

		cheatDebugMode = new ButtonGroup();
		cheatDebugModeYes = new JRadioButton(jskatStrings.getString("yes"));
		cheatDebugMode.add(cheatDebugModeYes);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(cheatDebugModeYes);
		cheatDebugModeNo = new JRadioButton(jskatStrings.getString("no"));
		cheatDebugModeNo.setSelected(true);
		cheatDebugMode.add(cheatDebugModeNo);
		fieldPanel.add(cheatDebugModeNo);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		tabPanel = new JPanel();
		tabPanel.setLayout(new GridBagLayout());
		tabPanel.add(widgetPanel, gridBagConstraints);

		// Fill panels
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		tabPanel.add(fillPanel, gridBagConstraints);

		fillPanel2 = new JPanel();
		fillPanel2.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		tabPanel.add(fillPanel2, gridBagConstraints);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(jskatStrings.getString("general"), tabPanel);

		/*
		 * Rules tab
		 */

		// ISPA/pub rules
		// Shortcut yes/no
		skatRules = new ButtonGroup();
		officialRules = new JRadioButton(jskatStrings
				.getString("play_ispa_rules"));
		officialRules.setSelected(true);
		officialRules.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				officialRulesRadioButtonStateChanged(evt);
			}
		});
		skatRules.add(officialRules);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(officialRules);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel = new JPanel();
		widgetPanel.setLayout(new GridBagLayout());
		widgetPanel.add(fieldPanel, gridBagConstraints);
		// Pub rules
		pubRules = new JRadioButton(jskatStrings.getString("play_pub_rules"));
		skatRules.add(pubRules);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(pubRules);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		playContra = new JCheckBox(jskatStrings.getString("play_contra"));
		playContra.setEnabled(false);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new GridBagLayout());
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		fieldPanel.add(playContra, gridBagConstraints);
		playBock = new JCheckBox(jskatStrings.getString("play_bock"));
		playBock.setEnabled(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		fieldPanel.add(playBock, gridBagConstraints);
		playRamsch = new JCheckBox(jskatStrings.getString("play_ramsch"));
		playRamsch.setEnabled(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		fieldPanel.add(playRamsch, gridBagConstraints);
		playRevolution = new JCheckBox(jskatStrings
				.getString("play_revolution"));
		playRevolution.setEnabled(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		fieldPanel.add(playRevolution, gridBagConstraints);
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		fieldPanel.add(fillPanel, gridBagConstraints);

		// Bock game rules subtab
		fieldLabel = new JLabel(jskatStrings.getString("bock_game_events"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldLabelPanel.add(fieldLabel);
		bockEventLostGrand = new JCheckBox(jskatStrings
				.getString("bock_event_lost_grand"));
		bockEventLostGrand.setEnabled(false);
		bockEventLostWith60 = new JCheckBox(jskatStrings
				.getString("bock_event_lost_with_60"));
		bockEventLostWith60.setEnabled(false);
		bockEventLostAfterContra = new JCheckBox(jskatStrings
				.getString("bock_event_lost_after_contra"));
		bockEventLostAfterContra.setEnabled(false);
		bockEventContraReAnnounced = new JCheckBox(jskatStrings
				.getString("bock_event_contra_re"));
		bockEventContraReAnnounced.setEnabled(false);
		bockEventPlayerHasX00Points = new JCheckBox(jskatStrings
				.getString("bock_event_x00_points"));
		bockEventPlayerHasX00Points.setEnabled(false);
		widgetPanel2 = new JPanel();
		widgetPanel2.setLayout(new GridLayout(4, 2));
		widgetPanel2.add(fieldLabelPanel);
		widgetPanel2.add(new JPanel());
		widgetPanel2.add(bockEventLostGrand, gridBagConstraints);
		widgetPanel2.add(bockEventLostWith60, gridBagConstraints);
		widgetPanel2.add(bockEventLostAfterContra, gridBagConstraints);
		widgetPanel2.add(bockEventContraReAnnounced, gridBagConstraints);
		widgetPanel2.add(bockEventPlayerHasX00Points, gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		tabPanel2 = new JPanel();
		tabPanel2.setLayout(new GridBagLayout());
		tabPanel2.add(widgetPanel2, gridBagConstraints);
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		tabPanel2.add(fillPanel, gridBagConstraints);
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		tabPanel2.add(fillPanel, gridBagConstraints);
		tabbedPane2 = new JTabbedPane();
		tabbedPane2.add(tabPanel2, jskatStrings.getString("bock_game_rules"));

		// Ramsch game rules subtab
		fieldLabel = new JLabel(jskatStrings.getString("ramsch_game_events"));
		fieldLabelPanel = new JPanel();
		fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldLabelPanel.add(fieldLabel);
		ramschEventNoBid = new JCheckBox(jskatStrings
				.getString("ramsch_event_no_bid"));
		ramschEventNoBid.setEnabled(false);
		ramschEventRamschAfterBock = new JCheckBox(jskatStrings
				.getString("ramsch_event_ramsch_after_bock"));
		ramschEventRamschAfterBock.setEnabled(false);
		// Ramsch skat to loser or last trick
		ramschSkat = new ButtonGroup();
		ramschSkatLastTrick = new JRadioButton(jskatStrings
				.getString("ramsch_skat_to_last_trick"));
		ramschSkatLastTrick.setEnabled(false);
		ramschSkatLastTrick.setSelected(true);
		ramschSkat.add(ramschSkatLastTrick);
		// skat to loser
		ramschSkatLoser = new JRadioButton(jskatStrings
				.getString("ramsch_skat_to_loser"));
		ramschSkatLoser.setEnabled(false);
		ramschSkat.add(ramschSkatLoser);
		schieberRamsch = new JCheckBox(jskatStrings
				.getString("ramsch_play_schieberramsch"));
		schieberRamsch.setEnabled(false);
		schieberRamschJacksInSkat = new JCheckBox(jskatStrings
				.getString("ramsch_schieberramsch_jacks_in_skat"));
		schieberRamschJacksInSkat.setEnabled(false);
		ramschGrandHandPossible = new JCheckBox(jskatStrings
				.getString("ramsch_grand_hand"));
		ramschGrandHandPossible.setEnabled(false);
		widgetPanel2 = new JPanel();
		widgetPanel2.setLayout(new GridLayout(5, 2));
		widgetPanel2.add(fieldLabelPanel);
		widgetPanel2.add(new JPanel());
		widgetPanel2.add(ramschEventNoBid);
		widgetPanel2.add(ramschEventRamschAfterBock);
		widgetPanel2.add(ramschSkatLastTrick);
		widgetPanel2.add(ramschSkatLoser);
		widgetPanel2.add(schieberRamsch);
		widgetPanel2.add(schieberRamschJacksInSkat);
		widgetPanel2.add(ramschGrandHandPossible);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		tabPanel2 = new JPanel();
		tabPanel2.setLayout(new GridBagLayout());
		tabPanel2.add(widgetPanel2, gridBagConstraints);
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		tabPanel2.add(fillPanel, gridBagConstraints);
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		tabPanel2.add(fillPanel, gridBagConstraints);
		tabbedPane2.add(tabPanel2, jskatStrings.getString("ramsch_game_rules"));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 5;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(0, 10, 0, 0);
		fieldPanel.add(tabbedPane2, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(0, 30, 0, 0);
		widgetPanel.add(fieldPanel, gridBagConstraints);

		// --[ToDo]--//
		// --> Button for "Set to default rules"
		// --> Action Listener
		/*
		 * JButton setToDefault = new JButton("Set to default rules");
		 */
		// --[ToDo]--//
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		tabPanel = new JPanel();
		tabPanel.setLayout(new GridBagLayout());
		tabPanel.add(widgetPanel, gridBagConstraints);

		// Fill panels
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		tabPanel.add(fillPanel, gridBagConstraints);

		fillPanel2 = new JPanel();
		fillPanel2.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		tabPanel.add(fillPanel2, gridBagConstraints);

		tabbedPane.addTab(jskatStrings.getString("skat_rules"), tabPanel);

		/*
		 * Cheat-/Debug tab
		 */

		// Hide cards
		hideCards = new JCheckBox(jskatStrings.getString("show_all_cards"));
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(hideCards);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel = new JPanel();
		widgetPanel.setLayout(new GridBagLayout());
		widgetPanel.add(fieldPanel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		tabPanel = new JPanel();
		tabPanel.setLayout(new GridBagLayout());
		tabPanel.add(widgetPanel, gridBagConstraints);

		// Fill panels
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		tabPanel.add(fillPanel, gridBagConstraints);

		fillPanel2 = new JPanel();
		fillPanel2.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		tabPanel.add(fillPanel2, gridBagConstraints);
		tabbedPane.addTab(jskatStrings.getString("debug"), tabPanel);

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		getContentPane().add(new JPanel(), BorderLayout.NORTH);

		getContentPane().add(new JPanel(), BorderLayout.EAST);

		getContentPane().add(new JPanel(), BorderLayout.WEST);

		pack();
	}

	/**
	 * Enables/disables the checkboxes of the pub rules without changing their
	 * values
	 */
	private void officialRulesRadioButtonStateChanged(ChangeEvent evt) {

		if (officialRules.isSelected()) {

			playContra.setEnabled(false);
			playBock.setEnabled(false);
			playRamsch.setEnabled(false);
			playRevolution.setEnabled(false);
			bockEventContraReAnnounced.setEnabled(false);
			bockEventLostAfterContra.setEnabled(false);
			bockEventLostGrand.setEnabled(false);
			bockEventLostWith60.setEnabled(false);
			bockEventPlayerHasX00Points.setEnabled(false);
			ramschEventNoBid.setEnabled(false);
			ramschEventRamschAfterBock.setEnabled(false);
			ramschGrandHandPossible.setEnabled(false);
			ramschSkatLastTrick.setEnabled(false);
			ramschSkatLoser.setEnabled(false);
			schieberRamsch.setEnabled(false);
			schieberRamschJacksInSkat.setEnabled(false);

		} else {

			playContra.setEnabled(true);
			playBock.setEnabled(true);
			playRamsch.setEnabled(true);
			playRevolution.setEnabled(true);
			bockEventContraReAnnounced.setEnabled(true);
			bockEventLostAfterContra.setEnabled(true);
			bockEventLostGrand.setEnabled(true);
			bockEventLostWith60.setEnabled(true);
			bockEventPlayerHasX00Points.setEnabled(true);
			ramschEventNoBid.setEnabled(true);
			ramschEventRamschAfterBock.setEnabled(true);
			ramschGrandHandPossible.setEnabled(true);
			ramschSkatLastTrick.setEnabled(true);
			ramschSkatLoser.setEnabled(true);
			schieberRamsch.setEnabled(true);
			schieberRamschJacksInSkat.setEnabled(true);
		}
	}

	private void setToInitialState() {

		// select first tab page
		tabbedPane.setSelectedIndex(0);
		// set the location in the middle of the JSkatFrame
		setLocationRelativeTo(parent);

		// set all widgets to the values of the JSkatOptions
		JSkatOptions options = jskatMaster.getJSkatOptions();

		if (options.getLanguage() == JSkatOptions.Languages.GERMAN) {
			
			language.setSelectedIndex(0);
		}
		else  {
			
			language.setSelectedIndex(1);
		}
		if (options.getCardFace() == JSkatOptions.CardFaces.FRENCH) {
			
			cardFaceFrench.setSelected(true);
		} 
		else if (options.getCardFace() == JSkatOptions.CardFaces.GERMAN) {
			
			cardFaceGerman.setSelected(true);
		} 
		else {
			
			cardFaceTournament.setSelected(true);
		}
		savePath.setText(options.getSavePath());
		trickRemoveDelayTime.setValue(options.getTrickRemoveDelayTime() / 1000);
		trickRemoveAfterClick.setSelected(options.isTrickRemoveAfterClick());
		if (options.isGameShortCut()) {
			gameShortCutYes.setSelected(true);
		} else {
			gameShortCutNo.setSelected(true);
		}
		if (options.isCheatDebugMode()) {
			cheatDebugModeYes.setSelected(true);
		} else {
			cheatDebugModeNo.setSelected(true);
		}
		if (options.getRules() == SkatTableOptions.ISPA_RULES) {
			officialRules.setSelected(true);
		} else {
			pubRules.setSelected(true);
		}
		if (options.isPlayBock()) {
			playBock.setSelected(true);
		}
		if (options.isPlayContra()) {
			playContra.setSelected(true);
		}
		if (options.isPlayRamsch()) {
			playRamsch.setSelected(true);
		}
		if (options.isPlayRevolution()) {
			playRevolution.setSelected(true);
		}
		if (options.isBockEventContraReAnnounced()) {
			bockEventContraReAnnounced.setSelected(true);
		}
		if (options.isBockEventLostAfterContra()) {
			bockEventLostAfterContra.setSelected(true);
		}
		if (options.isBockEventLostGrand()) {
			bockEventLostGrand.setSelected(true);
		}
		if (options.isBockEventLostWith60()) {
			bockEventLostWith60.setSelected(true);
		}
		if (options.isBockEventPlayerHasX00Points()) {
			bockEventPlayerHasX00Points.setSelected(true);
		}
		if (options.isRamschEventNoBid()) {
			ramschEventNoBid.setSelected(true);
		}
		if (options.isRamschEventRamschAfterBock()) {
			ramschEventRamschAfterBock.setSelected(true);
		}
		if (options.isRamschGrandHandPossible()) {
			ramschGrandHandPossible.setSelected(true);
		}
		if (options.getRamschSkat() == SkatTableOptions.SKAT_TO_LAST_TRICK) {
			ramschSkatLastTrick.setSelected(true);
		} else {
			ramschSkatLoser.setSelected(true);
		}
		if (options.isSchieberRamsch()) {
			schieberRamsch.setSelected(true);
		}
		if (options.isSchieberRamschJacksInSkat()) {
			schieberRamschJacksInSkat.setSelected(true);
		}
	}

	/** 
	 * Shows the OptionsDialog
	 * 
	 * @param visible Shows the dialog if set to TRUE
	 */
	public void setVisible(boolean visible) {

		if (visible) {
			// first set the Dialog back to it's initial state
			setToInitialState();
		}

		super.setVisible(visible);
	}

	/** Starts a new Skat round and closes the dialog */
	private void okButtonPressed() {

		setVisible(false);

		jskatMaster.setJSkatOptions(this);

		dispose();
	}

	/** Closes the dialog only */
	private void cancelButtonPressed() {

		setVisible(false);
		dispose();
	}

	/**
	 * Gets the language
	 * 
	 * @return The index of the language
	 */
	public int getLanguage() {

		return language.getSelectedIndex();
	}

	/**
	 * Gets the card face
	 * 
	 * @return String of the card face
	 */
	public JSkatOptions.CardFaces getCardFace() {

		JSkatOptions.CardFaces result = null;
		
		if (cardFaceFrench.isSelected()) {
			
			result = JSkatOptions.CardFaces.FRENCH;
		}
		else if (cardFaceGerman.isSelected()) {
			
			result = JSkatOptions.CardFaces.GERMAN;
		}
		else {
			
			result = JSkatOptions.CardFaces.TOURNAMENT;
		}
		
		return result;
	}

	/**
	 * Gets the save path
	 * 
	 * @return The save path
	 */
	public String getSavePath() {

		return savePath.getText();
	}

	/**
	 * Gets the delay time for removing the cards after completion of a trick
	 * 
	 * @return The delay time
	 */
	public int getTrickRemoveDelayTime() {

		return trickRemoveDelayTime.getValue();
	}

	/**
	 * Gets the state of the checkbox 'click to remove the cards'
	 * 
	 * @return The state of the checkbox
	 */
	public boolean getTrickRemoveAfterClick() {

		return trickRemoveAfterClick.isSelected();
	}

	/**
	 * Gets the state of the radio button group 'Shortcut game'
	 * 
	 * @return The state of the radio button group
	 */
	public boolean getGameShortCut() {

		if (gameShortCutYes.isSelected())
			return true;
		else
			return false;
	}

	/**
	 * Gets the state of the radio button group 'Cheat-/debug mode'
	 * 
	 * @return The state of the radio button group
	 */
	public boolean getCheatDebugMode() {

		if (cheatDebugModeYes.isSelected())
			return true;
		else
			return false;
	}

	/**
	 * Gets the state of the radio button group 'Skat rules'
	 * 
	 * @return The state of the radio button group
	 */
	public int getRules() {

		if (officialRules.isSelected())
			return SkatTableOptions.ISPA_RULES;
		else
			return SkatTableOptions.PUB_RULES;
	}

	/**
	 * Get the state of the Play Bock check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getPlayBock() {

		return playBock.isSelected();
	}

	/**
	 * Gets the state of the Play Kontra check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getPlayContra() {

		return playContra.isSelected();
	}

	/**
	 * Gets the state of the Play Ramsch check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getPlayRamsch() {

		return playRamsch.isSelected();
	}

	/**
	 * Gets the state of the Play Revolution check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getPlayRevolution() {

		return playRevolution.isSelected();
	}

	/**
	 * Gets the state of the bockEventLostGrand check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getBockEventLostGrand() {

		return bockEventLostGrand.isSelected();
	}

	/**
	 * Gets the state of the bockEventLostWith60 check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getBockEventLostWith60() {

		return bockEventLostWith60.isSelected();
	}

	/**
	 * Gets the state of the bockEventLostAfterContra check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getBockEventLostAfterContra() {

		return bockEventLostAfterContra.isSelected();
	}

	/**
	 * Gets the state of the bockEventContraReAnnounced check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getBockEventContraReAnnounced() {

		return bockEventContraReAnnounced.isSelected();
	}

	/**
	 * Gets the state of the bockEventPlayerHasX00Points check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getBockEventPlayerHasX00Points() {

		return bockEventPlayerHasX00Points.isSelected();
	}

	/**
	 * Gets the state of the radio button group 'ramschSkat'
	 * 
	 * @return The state of the radio button group
	 */
	public int getRamschSkat() {

		if (ramschSkatLastTrick.isSelected()) {
			
			return SkatTableOptions.SKAT_TO_LAST_TRICK;

		} else {
			
			return SkatTableOptions.SKAT_TO_LOSER;
		}
	}

	/**
	 * Gets the state of the schieberRamsch check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getSchieberRamsch() {

		return schieberRamsch.isSelected();
	}

	/**
	 * Gets the state of the schieberRamschJacksInSkat check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getSchieberRamschJacksInSkat() {

		return schieberRamschJacksInSkat.isSelected();
	}

	/**
	 * Gets the state of the ramschEventNoBid check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getRamschEventNoBid() {

		return ramschEventNoBid.isSelected();
	}

	/**
	 * Gets the state of the ramschEventRamschAfterBock check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getRamschEventRamschAfterBock() {

		return ramschEventRamschAfterBock.isSelected();
	}

	/**
	 * Gets the state of the ramschGrandHandPossible check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getRamschGrandHandPossible() {

		return ramschGrandHandPossible.isSelected();
	}

	/**
	 * Gets the state of the Hide Cards check box
	 * 
	 * @return TRUE if the check box is selected
	 */
	public boolean getHideCards() {

		return hideCards.isSelected();
	}

	// Variables declaration
	private JSkatMaster jskatMaster;

	private ResourceBundle jskatStrings;

	private JFrame parent;

	private JButton okButton;

	private JButton cancelButton;

	private JTabbedPane tabbedPane;

	private JTabbedPane tabbedPane2;

	private JPanel tabPanel;

	private JPanel tabPanel2;

	private JPanel widgetPanel;

	private JPanel widgetPanel2;

	private JPanel fillPanel;

	private JPanel fillPanel2;

	private JLabel fieldLabel;

	private JPanel fieldLabelPanel;

	private JPanel fieldPanel;

	private GridBagConstraints gridBagConstraints;

	private DefaultComboBoxModel comboBoxModel;

	private JComboBox language;

	private ButtonGroup cardFace;

	private JRadioButton cardFaceFrench;

	private JRadioButton cardFaceGerman;

	private JRadioButton cardFaceTournament;

	private JTextField savePath;

	private JButton savePathSearch;

	private JSlider trickRemoveDelayTime;

	private JCheckBox trickRemoveAfterClick;

	private ButtonGroup gameShortCut;

	private JRadioButton gameShortCutYes;

	private JRadioButton gameShortCutNo;

	private ButtonGroup cheatDebugMode;

	private JRadioButton cheatDebugModeYes;

	private JRadioButton cheatDebugModeNo;

	private ButtonGroup skatRules;

	private JRadioButton officialRules;

	private JRadioButton pubRules;

	private JCheckBox playBock;

	private JCheckBox playContra;

	private JCheckBox playRamsch;

	private JCheckBox playRevolution;

	private JCheckBox bockEventLostGrand;

	private JCheckBox bockEventLostWith60;

	private JCheckBox bockEventLostAfterContra;

	private JCheckBox bockEventContraReAnnounced;

	private JCheckBox bockEventPlayerHasX00Points;

	private ButtonGroup ramschSkat;

	private JRadioButton ramschSkatLastTrick;

	private JRadioButton ramschSkatLoser;

	private JCheckBox schieberRamsch;

	private JCheckBox schieberRamschJacksInSkat;

	private JCheckBox ramschEventNoBid;

	private JCheckBox ramschEventRamschAfterBock;

	private JCheckBox ramschGrandHandPossible;

	private JCheckBox hideCards;
}
