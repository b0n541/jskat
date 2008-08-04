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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JDialog;
import javax.swing.DefaultComboBoxModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import jskat.control.JSkatMaster;
import jskat.data.JSkatDataModel;
import jskat.share.SkatConstants;

/**
 * Dialog for a new game series
 */
public class NewSkatSeriesDialog extends JDialog {

	private Log log = LogFactory.getLog(NewSkatSeriesDialog.class);

	private static final long serialVersionUID = 7666044764313126479L;

	/**
	 * Creates new game dialog
	 * 
	 * @param jskatMaster
	 *            The JSkatMaster that controls the game
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param aiPlayer
	 *            The AIPlayer class names that were found during startup
	 * @param parent
	 *            The parent JFrame
	 * @param modal
	 *            TRUE if the dialog is modal
	 */
	public NewSkatSeriesDialog(JSkatMaster jskatMaster,
			JSkatDataModel dataModel, Vector<String> aiPlayer, JFrame parent,
			boolean modal) {

		super(parent, modal);

		this.jskatMaster = jskatMaster;
		this.jskatStrings = dataModel.getResourceBundle();
		this.parent = parent;

		initComponents(aiPlayer);
		setToInitialState();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents(Vector<String> aiPlayer) {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(jskatStrings.getString("new_skat_round"));
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

		// Number of rounds definition
		fieldLabel = new JLabel(jskatStrings.getString("no_of_rounds"));
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

		numberOfSkatRounds = new JSpinner();
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
		spinnerModel.setMinimum(new Integer(1));
		spinnerModel.setMaximum(new Integer(999));
		spinnerModel.setStepSize(new Integer(1));
		spinnerModel.setValue(new Integer(12));
		numberOfSkatRounds.setModel(spinnerModel);
		numberOfSkatRounds.setPreferredSize(new Dimension(100, 22));
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fieldPanel.add(numberOfSkatRounds);
		unlimitedNumberOfSkatRounds = new JCheckBox();
		unlimitedNumberOfSkatRounds
				.setText(jskatStrings.getString("unlimited"));
		unlimitedNumberOfSkatRounds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (unlimitedNumberOfSkatRounds.isSelected()) {

					numberOfSkatRounds.setEnabled(false);

				} else {

					numberOfSkatRounds.setEnabled(true);
				}
			}
		});

		fieldPanel.add(unlimitedNumberOfSkatRounds);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		widgetPanel.add(fieldPanel, gridBagConstraints);

		// Player definition
		for (int playerNo = 1; playerNo < 4; playerNo++) {

			// Do the same for every player
			fieldLabel = new JLabel(jskatStrings.getString("player") + " "
					+ playerNo);
			fieldLabelPanel = new JPanel();
			fieldLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			fieldLabelPanel.add(fieldLabel);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = playerNo;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			widgetPanel.add(fieldLabelPanel, gridBagConstraints);

			JTextField currentPlayerName = new JTextField();

			switch (playerNo) {
			case (1):
				currentPlayerName.setText("Markus");
				player1Name = currentPlayerName;
				break;
			case (2):
				currentPlayerName.setText("Jan");
				player2Name = currentPlayerName;
				break;
			case (3):
				currentPlayerName.setText(jskatStrings.getString("player"));
				player3Name = currentPlayerName;
				break;
			}

			currentPlayerName.setPreferredSize(new Dimension(100, 22));
			fieldPanel = new JPanel();
			fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			fieldPanel.add(currentPlayerName);

			comboBoxModel = new DefaultComboBoxModel();

			if (playerNo == 3) {

				// For the third player add Human as player class
				comboBoxModel.addElement(jskatStrings.getString("human"));
			}
			for (int i = 0; i < aiPlayer.size(); i++) {

				// Now all AI player classes that were found at startup
				comboBoxModel.addElement(aiPlayer.get(i));
			}

			JComboBox currentPlayerClass = new JComboBox();
			currentPlayerClass.setModel(comboBoxModel);

			switch (playerNo) {
			case (1):
				player1Class = currentPlayerClass;
				break;
			case (2):
				player2Class = currentPlayerClass;
				break;
			case (3):
				player3Class = currentPlayerClass;
				break;
			}

			fieldPanel.add(currentPlayerClass);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = playerNo;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			widgetPanel.add(fieldPanel, gridBagConstraints);
		}

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		centerPanel.add(widgetPanel, gridBagConstraints);

		// Fill panels
		fillPanel = new JPanel();
		fillPanel.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		centerPanel.add(fillPanel, gridBagConstraints);

		fillPanel2 = new JPanel();
		fillPanel2.setPreferredSize(new Dimension(0, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		centerPanel.add(fillPanel2, gridBagConstraints);

		getContentPane().add(centerPanel, BorderLayout.CENTER);

		getContentPane().add(new JPanel(), BorderLayout.NORTH);

		getContentPane().add(new JPanel(), BorderLayout.EAST);

		getContentPane().add(new JPanel(), BorderLayout.WEST);

		pack();
	}

	private void setToInitialState() {

		// set the location in the middle of the JSkatFrame
		setLocationRelativeTo(parent);
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

		int numberOfRounds;

		if (unlimitedNumberOfSkatRounds.isSelected()) {

			numberOfRounds = -1;

		} else {

			numberOfRounds = ((Integer) numberOfSkatRounds.getValue())
					.intValue();
		}

		log.debug("Number of rounds " + numberOfRounds);

		jskatMaster
				.getCurrentSkatTable().createNewSkatSeries(
						numberOfRounds,
						new String[] { player1Name.getText(),
								player2Name.getText(), player3Name.getText() },
						new String[] {
								(String) player1Class.getSelectedItem(),
								(String) player2Class.getSelectedItem(),
								(player3Class.getSelectedIndex() == 0 ? SkatConstants.HUMANPLAYER
										: (String) player3Class
												.getSelectedItem()) });

		dispose();
	}

	/** Closes the dialog only */
	private void cancelButtonPressed() {

		setVisible(false);
		dispose();
	}

	/**
	 * Gets the number of skat rounds to be played
	 * 
	 * @return The number of skat rounds to be played
	 */
	public int getNumberOfSkatRounds() {

		return ((Integer) numberOfSkatRounds.getValue()).intValue();
	}

	/**
	 * Gets the state of the checkbox 'Unlimited number of skat rounds'
	 * 
	 * @return The state of the checkbox
	 */
	public boolean getUnlimitedNumberOfSkatRounds() {

		if (unlimitedNumberOfSkatRounds.isSelected())
			return true;
		else
			return false;
	}

	/**
	 * Gets the name of a player
	 * 
	 * @return The name of the first player
	 * @param player
	 *            The Number of the player
	 */
	public String getPlayerName(int player) {

		String playerName = new String();

		switch (player) {
		case 1:
			playerName = player1Name.getText();
			break;

		case 2:
			playerName = player2Name.getText();
			break;

		case 3:
			playerName = player3Name.getText();
			break;
		}

		return playerName;
	}

	/**
	 * Gets the AIPlayer class name for a player
	 * 
	 * @return The index of the AIPlayer class name
	 * @param player
	 *            The number of the player
	 */
	public int getPlayerClass(int player) {

		int playerClass = -1;

		switch (player) {

		case 1:
			player1Class.getSelectedIndex();
			break;
		case 2:
			player2Class.getSelectedIndex();
			break;
		case 3:
			player3Class.getSelectedIndex();
			break;
		}

		return playerClass;
	}

	// Variables declaration
	private JSkatMaster jskatMaster;

	private ResourceBundle jskatStrings;

	private JFrame parent;

	private JButton okButton;

	private JButton cancelButton;

	private JPanel centerPanel;

	private JPanel widgetPanel;

	private JPanel fillPanel;

	private JPanel fillPanel2;

	private JLabel fieldLabel;

	private JPanel fieldLabelPanel;

	private JPanel fieldPanel;

	private GridBagConstraints gridBagConstraints;

	private DefaultComboBoxModel comboBoxModel;

	private JSpinner numberOfSkatRounds;

	private JCheckBox unlimitedNumberOfSkatRounds;

	private JTextField player1Name;

	private JComboBox player1Class;

	private JTextField player2Name;

	private JComboBox player2Class;

	private JTextField player3Name;

	private JComboBox player3Class;
}