/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import net.miginfocom.swing.MigLayout;
import de.jskat.ai.PlayerType;
import de.jskat.control.JSkatMaster;

/**
 * Asks for user defined options on start of a skat series
 */
public class SkatSeriesStartDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JSkatMaster jskat;
	private JFrame parent;

	private ResourceBundle strings;

	private JComboBox player1;
	private JComboBox player2;
	private JComboBox player3;
	private JTextField numberOfRounds;
	private JCheckBox unlimited;

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
	public SkatSeriesStartDialog(JSkatMaster skatMaster, JFrame mainFrame,
			ResourceBundle newStrings) {

		jskat = skatMaster;
		parent = mainFrame;
		strings = newStrings;

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
		player1 = new JComboBox(playerTypes.toArray());
		player1.setRenderer(new PlayerComboBoxRenderer(strings));
		root.add(player1, "span2, growx, wrap"); //$NON-NLS-1$
		root.add(new JLabel(strings.getString("player") + " 2")); //$NON-NLS-1$ //$NON-NLS-2$
		player2 = new JComboBox(playerTypes.toArray());
		player2.setRenderer(new PlayerComboBoxRenderer(strings));
		root.add(player2, "span2, growx, wrap"); //$NON-NLS-1$
		root.add(new JLabel(strings.getString("player") + " 3")); //$NON-NLS-1$//$NON-NLS-2$

		// Human player can only be player 3
		playerTypes.add(PlayerType.HUMAN);
		player3 = new JComboBox(playerTypes.toArray());
		player3.setRenderer(new PlayerComboBoxRenderer(strings));
		player3.setSelectedIndex(player3.getItemCount() - 1);
		root.add(player3, "span2, growx, wrap"); //$NON-NLS-1$

		root.add(new JLabel(strings.getString("number_of_rounds"))); //$NON-NLS-1$
		numberOfRounds = new JTextField(10);
		numberOfRounds.setText("1"); //$NON-NLS-1$
		root.add(numberOfRounds);
		unlimited = new JCheckBox(strings.getString("unlimited")); //$NON-NLS-1$
		root.add(unlimited, "wrap"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(new MigLayout());
		JButton start = new JButton(strings.getString("start")); //$NON-NLS-1$
		start.setActionCommand("START"); //$NON-NLS-1$
		start.addActionListener(this);
		buttonPanel.add(start);
		JButton cancel = new JButton(strings.getString("cancel")); //$NON-NLS-1$
		cancel.setActionCommand("CANCEL"); //$NON-NLS-1$
		cancel.addActionListener(this);
		buttonPanel.add(cancel);
		root.add(buttonPanel, "span 3, center"); //$NON-NLS-1$

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
		} else if ("START".equals(e.getActionCommand())) { //$NON-NLS-1$

			ArrayList<PlayerType> playerNames = new ArrayList<PlayerType>();
			playerNames.add((PlayerType) player1.getSelectedItem());
			playerNames.add((PlayerType) player2.getSelectedItem());
			playerNames.add((PlayerType) player3.getSelectedItem());

			setVisible(false);

			jskat.startSeries(playerNames,
					Integer.parseInt(numberOfRounds.getText()),
					unlimited.isSelected());
		}

	}

	private class PlayerComboBoxRenderer extends JPanel implements
			ListCellRenderer {

		private static final long serialVersionUID = 1L;

		ResourceBundle strings;
		JLabel cellItemLabel;

		PlayerComboBoxRenderer(ResourceBundle newStrings) {

			super();
			strings = newStrings;

			setLayout(new MigLayout("fill")); //$NON-NLS-1$
			cellItemLabel = new JLabel(" "); //$NON-NLS-1$
			add(cellItemLabel);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			cellItemLabel.setFont(list.getFont());

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			PlayerType player = (PlayerType) value;

			if (player != null) {
				String cellText = null;

				switch (player) {
				case RANDOM:
					cellText = strings.getString("random_player"); //$NON-NLS-1$
					break;
				case NEURAL_NETWORK:
					cellText = strings.getString("neural_network_player"); //$NON-NLS-1$
					break;
				case ALGORITHMIC:
					cellText = strings.getString("algorithmic_player"); //$NON-NLS-1$
					break;
				case HUMAN:
					cellText = System.getProperty("user.name"); //$NON-NLS-1$
					break;
				}

				cellItemLabel.setText(cellText);
			} else {
				cellItemLabel.setText(" "); //$NON-NLS-1$
			}

			return this;
		}
	}
}
