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
import javax.swing.JTextField;

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
	private JComboBox player1;
	private JComboBox player2;
	private JComboBox player3;
	private JTextField numberOfRounds;
	private JCheckBox unlimited;
	
	/**
	 * Constructor
	 * 
	 * @param skatMaster JSkat master
	 * @param mainFrame Main frame
	 */
	public SkatSeriesStartDialog(JSkatMaster skatMaster, JFrame mainFrame) {
		
		this.jskat = skatMaster;
		this.parent = mainFrame;
		
		initGUI();
	}
	
	private void initGUI() {
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(false);
		
		this.setTitle("Start new skat series");
		
		Container root = this.getContentPane();
		root.setLayout(new MigLayout());
		
		ArrayList<String> playerTypes = new ArrayList<String>();
		
		playerTypes.add(PlayerType.RANDOM.toString());
		playerTypes.add(PlayerType.NEURAL_NETWORK.toString());
		playerTypes.add(PlayerType.ALGORITHMIC.toString());

		root.add(new JLabel("Player 1"));
		this.player1 = new JComboBox(playerTypes.toArray());
		root.add(this.player1, "span2, growx, wrap"); //$NON-NLS-1$
		root.add(new JLabel("Player 2"));
		this.player2 = new JComboBox(playerTypes.toArray());
		root.add(this.player2, "span2, growx, wrap"); //$NON-NLS-1$
		root.add(new JLabel("Player 3"));
		// Human player can only be player 3
		playerTypes.add(PlayerType.HUMAN.toString());
		this.player3 = new JComboBox(playerTypes.toArray());
		root.add(this.player3, "span2, growx, wrap"); //$NON-NLS-1$
		root.add(new JLabel("Number of rounds"));
		this.numberOfRounds = new JTextField(10);
		this.numberOfRounds.setText("1"); //$NON-NLS-1$
		root.add(this.numberOfRounds);
		this.unlimited = new JCheckBox("unlimited");
		root.add(this.unlimited, "wrap"); //$NON-NLS-1$
		
		JPanel buttonPanel = new JPanel(new MigLayout());
		JButton start = new JButton("Start");
		start.setActionCommand("START"); //$NON-NLS-1$
		start.addActionListener(this);
		buttonPanel.add(start);
		JButton cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL"); //$NON-NLS-1$
		cancel.addActionListener(this);
		buttonPanel.add(cancel);
		root.add(buttonPanel, "span 3, center"); //$NON-NLS-1$
		
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

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ("CANCEL".equals(e.getActionCommand())) { //$NON-NLS-1$
			
			this.setVisible(false);
		}
		else if ("START".equals(e.getActionCommand())) { //$NON-NLS-1$
			
			ArrayList<PlayerType> playerNames = new ArrayList<PlayerType>();
			playerNames.add(PlayerType.getPlayerTypeFromString((String) this.player1.getSelectedItem()));
			playerNames.add(PlayerType.getPlayerTypeFromString((String) this.player2.getSelectedItem()));
			playerNames.add(PlayerType.getPlayerTypeFromString((String) this.player3.getSelectedItem()));
			
			this.setVisible(false);

			this.jskat.startSeries(playerNames, Integer.parseInt(this.numberOfRounds.getText()), this.unlimited.isSelected());
		}
		
	}
}
