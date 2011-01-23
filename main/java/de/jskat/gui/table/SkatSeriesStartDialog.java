/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import de.jskat.ai.PlayerType;
import de.jskat.control.JSkatMaster;
import de.jskat.gui.AbstractI18NComboBoxRenderer;
import de.jskat.util.JSkatResourceBundle;

/**
 * Asks for user defined options on start of a skat series
 */
public class SkatSeriesStartDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final String CANCEL = "CANCEL"; //$NON-NLS-1$
	private static final String START = "START"; //$NON-NLS-1$

	private JSkatMaster jskat;
	private JFrame parent;

	JSkatResourceBundle strings;

	private JTextField player1name;
	private JTextField player2name;
	private JTextField player3name;
	private JComboBox player1;
	private JComboBox player2;
	private JComboBox player3;
	JSpinner numberOfRounds;
	JCheckBox unlimited;

	/**
	 * Constructor
	 * 
	 * @param skatMaster
	 *            JSkat master
	 * @param mainFrame
	 *            Main frame
	 * @param newStrings
	 *            i18n strings
	 */
	public SkatSeriesStartDialog(JSkatMaster skatMaster, JFrame mainFrame) {

		jskat = skatMaster;
		parent = mainFrame;
		strings = JSkatResourceBundle.instance();

		initGUI();
	}

	private void initGUI() {

		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		setTitle(strings.getString("start_series")); //$NON-NLS-1$

		Container root = getContentPane();
		root.setLayout(new MigLayout());

		ArrayList<PlayerType> playerTypes = new ArrayList<PlayerType>();

		playerTypes.add(PlayerType.RANDOM);
		playerTypes.add(PlayerType.NEURAL_NETWORK);
		playerTypes.add(PlayerType.ALGORITHMIC);

		root.add(new JLabel(strings.getString("player") + " 1")); //$NON-NLS-1$//$NON-NLS-2$
		player1name = new JTextField("Jan");
		root.add(player1name, "span2, growx"); //$NON-NLS-1$
		player1 = new JComboBox(playerTypes.toArray());
		player1.setRenderer(new PlayerComboBoxRenderer());
		root.add(player1, "growx, wrap"); //$NON-NLS-1$

		root.add(new JLabel(strings.getString("player") + " 2")); //$NON-NLS-1$ //$NON-NLS-2$
		player2name = new JTextField("Markus");
		root.add(player2name, "span2, growx"); //$NON-NLS-1$
		player2 = new JComboBox(playerTypes.toArray());
		player2.setRenderer(new PlayerComboBoxRenderer());
		root.add(player2, "growx, wrap"); //$NON-NLS-1$
		root.add(new JLabel(strings.getString("player") + " 3")); //$NON-NLS-1$//$NON-NLS-2$

		player3name = new JTextField(System.getProperty("user.name"));
		root.add(player3name, "span2, growx"); //$NON-NLS-1$
		// Human player can only be player 3
		playerTypes.add(PlayerType.HUMAN);
		player3 = new JComboBox(playerTypes.toArray());
		player3.setRenderer(new PlayerComboBoxRenderer());
		player3.setSelectedIndex(player3.getItemCount() - 1);
		root.add(player3, "span2, growx, wrap"); //$NON-NLS-1$

		root.add(new JLabel(strings.getString("number_of_rounds"))); //$NON-NLS-1$
		numberOfRounds = new JSpinner(new SpinnerNumberModel(12, 1, 48, 1));
		root.add(numberOfRounds);
		unlimited = new JCheckBox(strings.getString("unlimited")); //$NON-NLS-1$
		unlimited.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (unlimited.isSelected()) {
					numberOfRounds.setEnabled(false);
				} else {
					numberOfRounds.setEnabled(true);
				}
			}
		});
		root.add(unlimited, "wrap"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(new MigLayout());
		JButton start = new JButton(strings.getString("start")); //$NON-NLS-1$
		start.setActionCommand(START);
		start.addActionListener(this);
		buttonPanel.add(start);
		JButton cancel = new JButton(strings.getString("cancel")); //$NON-NLS-1$
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);
		buttonPanel.add(cancel);
		root.add(buttonPanel, "span 4, center"); //$NON-NLS-1$

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

		if (CANCEL.equals(e.getActionCommand())) { //$NON-NLS-1$

			setVisible(false);
		} else if (START.equals(e.getActionCommand())) { //$NON-NLS-1$

			ArrayList<PlayerType> playerNames = new ArrayList<PlayerType>();
			playerNames.add((PlayerType) player1.getSelectedItem());
			playerNames.add((PlayerType) player2.getSelectedItem());
			playerNames.add((PlayerType) player3.getSelectedItem());

			setVisible(false);

			jskat.startSeries(playerNames,
					Integer.parseInt(numberOfRounds.getValue().toString()),
					unlimited.isSelected());
		}

	}

	private class PlayerComboBoxRenderer extends AbstractI18NComboBoxRenderer {

		private static final long serialVersionUID = 1L;

		PlayerComboBoxRenderer() {
			super();
		}

		@Override
		public String getValueText(Object value) {

			String result = " "; //$NON-NLS-1$

			PlayerType player = (PlayerType) value;

			if (player != null) {

				switch (player) {
				case RANDOM:
					result = strings.getString("random_player"); //$NON-NLS-1$
					break;
				case NEURAL_NETWORK:
					result = strings.getString("neural_network_player"); //$NON-NLS-1$
					break;
				case ALGORITHMIC:
					result = strings.getString("algorithmic_player"); //$NON-NLS-1$
					break;
				case HUMAN:
					result = strings.getString("human_player"); //$NON-NLS-1$
					break;
				}
			}

			return result;
		}
	}
}
