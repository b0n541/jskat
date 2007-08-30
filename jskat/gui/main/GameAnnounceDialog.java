/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import jskat.control.SkatGame;
import jskat.share.SkatConstants;
import jskat.data.GameAnnouncement;

/**
 * A dialog for annoucing the next game
 * 
 * @author Jan Sch&auml;fer
 */
public class GameAnnounceDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4438019795900464680L;

	/**
	 * Creates new GameAnnounceDialog
	 * 
	 * @param parent
	 *            The parent JFrame
	 * @param modal
	 *            TRUE if the dialog is modal
	 * @param jskatStrings
	 *            The strings for i18n
	 * @param skatGame
	 *            The new skat game
	 */
	public GameAnnounceDialog(JFrame parent, boolean modal,
			ResourceBundle jskatStrings, SkatGame skatGame) {

		super(parent, modal);

		this.skatGame = skatGame;
		this.jskatStrings = jskatStrings;
		this.parent = parent;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		GridBagConstraints gridBagConstraints;

		centerPanel = new JPanel();
		gameTypePanel = new JPanel();
		gameTypeLabel = new JLabel();
		gameTypeComboBox = new JComboBox();
		gameTypeComboBoxPanel = new JPanel();
		announceLabel = new JLabel();
		announceLabelPanel = new JPanel();
		announcePanel = new JPanel();
		ouvertCheckBox = new JCheckBox();
		schneiderCheckBox = new JCheckBox();
		schwarzCheckBox = new JCheckBox();
		buttonPanel = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		northPanel = new JPanel();
		eastPanel = new JPanel();
		southPanel = new JPanel();
		westPanel = new JPanel();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setResizable(false);

		setTitle(jskatStrings.getString("game_announcement"));
		centerPanel.setLayout(new GridBagLayout());

		gameTypeLabel.setText(jskatStrings.getString("game"));
		gameTypePanel.add(gameTypeLabel);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		centerPanel.add(gameTypePanel, gridBagConstraints);

		// TODO take card face into account
		gameTypeComboBox.setModel(new DefaultComboBoxModel(
				new String[] { jskatStrings.getString("clubs"),
						jskatStrings.getString("spades"),
						jskatStrings.getString("hearts"),
						jskatStrings.getString("diamonds"),
						jskatStrings.getString("null"),
						jskatStrings.getString("grand") }));

		gameTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				if (gameTypeComboBox.getSelectedIndex() == 4) {

					schneiderCheckBox.setEnabled(false);
					schneiderCheckBox.setSelected(false);
					schwarzCheckBox.setEnabled(false);
					schwarzCheckBox.setSelected(false);
					ouvertCheckBox.setEnabled(true);

				} else {

					if (skatGame.getSkatGameData().isHand()) {

						schneiderCheckBox.setEnabled(true);
						schwarzCheckBox.setEnabled(true);
						ouvertCheckBox.setEnabled(true);

					} else {

						schneiderCheckBox.setEnabled(false);
						schneiderCheckBox.setSelected(false);
						schwarzCheckBox.setEnabled(false);
						schwarzCheckBox.setSelected(false);
						ouvertCheckBox.setEnabled(false);
						ouvertCheckBox.setSelected(false);
					}
				}
			}
		});

		gameTypeComboBox.setPreferredSize(new Dimension(150, 24));
		gameTypeComboBoxPanel.add(gameTypeComboBox);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		centerPanel.add(gameTypeComboBoxPanel, gridBagConstraints);

		announceLabel.setText(jskatStrings.getString("game_type"));
		announceLabelPanel.add(announceLabel);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		centerPanel.add(announceLabelPanel, gridBagConstraints);

		announcePanel.setLayout(new GridLayout(3, 1));

		schneiderCheckBox.setText(jskatStrings.getString("schneider"));
		schneiderCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				if (!schneiderCheckBox.isSelected()) {
					schwarzCheckBox.setSelected(false);
					ouvertCheckBox.setSelected(false);
				}
			}
		});

		if (!skatGame.getSkatGameData().isHand()) {

			schneiderCheckBox.setEnabled(false);
		}
		announcePanel.add(schneiderCheckBox);

		schwarzCheckBox.setText(jskatStrings.getString("schwarz"));
		schwarzCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				if (schwarzCheckBox.isSelected()) {
					schneiderCheckBox.setSelected(true);
				} else {
					ouvertCheckBox.setSelected(false);
				}
			}
		});
		if (!skatGame.getSkatGameData().isHand()) {

			schwarzCheckBox.setEnabled(false);
		}
		announcePanel.add(schwarzCheckBox);

		ouvertCheckBox.setText(jskatStrings.getString("ouvert"));
		ouvertCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				if (ouvertCheckBox.isSelected()
						&& gameTypeComboBox.getSelectedIndex() != 4) {
					schneiderCheckBox.setSelected(true);
					schwarzCheckBox.setSelected(true);
				}
			}
		});
		if (!skatGame.getSkatGameData().isHand())
			ouvertCheckBox.setEnabled(false);
		announcePanel.add(ouvertCheckBox);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		centerPanel.add(announcePanel, gridBagConstraints);

		okButton.setText(jskatStrings.getString("start_game"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				if (gameTypeComboBox.getSelectedIndex() != -1) {

					GameAnnouncement newGame = new GameAnnouncement();
					int gameType = gameTypeComboBox.getSelectedIndex();

					switch (gameType) {

					case (0):
					case (1):
					case (2):
					case (3):
						newGame.setGameType(SkatConstants.SUIT);
						newGame.setTrump(gameType);
						break;
					case (4):
						newGame.setGameType(SkatConstants.NULL);
						newGame.setTrump(-1);
						break;
					case (5):
						newGame.setGameType(SkatConstants.GRAND);
						newGame.setTrump(SkatConstants.SUIT_GRAND);
						break;
					}

					newGame.setOuvert(ouvertCheckBox.isSelected());
					newGame.setSchneider(schneiderCheckBox.isSelected());
					newGame.setSchwarz(schwarzCheckBox.isSelected());

					dispose();
					skatGame.playing(newGame);
				}
			}
		});

		buttonPanel.add(okButton);

		cancelButton.setText(jskatStrings.getString("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				dispose();
				skatGame.showSkat();
			}
		});

		buttonPanel.add(cancelButton);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		centerPanel.add(buttonPanel, gridBagConstraints);

		getContentPane().add(centerPanel, BorderLayout.CENTER);

		getContentPane().add(southPanel, BorderLayout.SOUTH);

		getContentPane().add(northPanel, BorderLayout.NORTH);

		getContentPane().add(eastPanel, BorderLayout.EAST);

		getContentPane().add(westPanel, BorderLayout.WEST);

		pack();
	}

	private void setToInitialState() {

		gameTypeComboBox.getModel().setSelectedItem(null);
		ouvertCheckBox.setSelected(false);
		schneiderCheckBox.setSelected(false);
		schwarzCheckBox.setSelected(false);
		setLocationRelativeTo(parent);
	}

	/**
	 * Shows the dialog
	 */
	public void setVisible(boolean visible) {

		if (visible) {
			setToInitialState();
		}

		super.setVisible(visible);
	}

	private SkatGame skatGame;

	private JFrame parent;

	private JButton okButton;

	private JButton cancelButton;

	private JCheckBox ouvertCheckBox;

	private JCheckBox schneiderCheckBox;

	private JCheckBox schwarzCheckBox;

	private JComboBox gameTypeComboBox;

	private JLabel gameTypeLabel;

	private JLabel announceLabel;

	private JPanel centerPanel;

	private JPanel gameTypePanel;

	private JPanel southPanel;

	private JPanel buttonPanel;

	private JPanel announcePanel;

	private JPanel northPanel;

	private JPanel gameTypeComboBoxPanel;

	private JPanel announceLabelPanel;

	private JPanel eastPanel;

	private JPanel westPanel;

	/** Holds a reference to the strings for i18n */
	private ResourceBundle jskatStrings;
}
