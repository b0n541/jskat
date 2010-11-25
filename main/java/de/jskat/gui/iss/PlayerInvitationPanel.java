/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog panel for player invitation on ISS
 */
public class PlayerInvitationPanel extends JPanel {

	private ButtonGroup firstPlayerGroup;
	private ButtonGroup secondPlayerGroup;
	private ButtonGroup thirdPlayerGroup;

	/**
	 * Constructor
	 * 
	 * @param owner
	 *            Parent frame
	 * @param player
	 *            Available player on ISS
	 */
	public PlayerInvitationPanel(Set<String> player) {

		initPanel(player);
	}

	void initPanel(Set<String> player) {

		this.setLayout(new MigLayout("fill")); //$NON-NLS-1$

		this.firstPlayerGroup = new ButtonGroup();
		this.secondPlayerGroup = new ButtonGroup();
		this.thirdPlayerGroup = new ButtonGroup();

		for (String currPlayer : player) {

			addPlayerInvitationLabel(currPlayer); //$NON-NLS-1$
		}
	}

	void addPlayerInvitationLabel(String playerName) {

		this.add(new JLabel(playerName));

		JRadioButton firstButton = new JRadioButton();
		firstButton.setActionCommand(playerName);
		this.firstPlayerGroup.add(firstButton);
		this.add(firstButton);

		JRadioButton secondButton = new JRadioButton();
		secondButton.setActionCommand(playerName);
		this.secondPlayerGroup.add(secondButton);
		this.add(secondButton);

		JRadioButton thirdButton = new JRadioButton();
		thirdButton.setActionCommand(playerName);
		this.thirdPlayerGroup.add(thirdButton);
		this.add(thirdButton, "wrap"); //$NON-NLS-1$
	}

	/**
	 * Gets the selected player names
	 * 
	 * @return Selected player names
	 */
	public List<String> getPlayer() {

		List<String> result = new ArrayList<String>();

		if (this.firstPlayerGroup.getSelection() != null) {
			result.add(this.firstPlayerGroup.getSelection().getActionCommand());
		}
		if (this.secondPlayerGroup.getSelection() != null) {
			result
					.add(this.secondPlayerGroup.getSelection()
							.getActionCommand());
		}
		if (this.thirdPlayerGroup.getSelection() != null) {
			result.add(this.thirdPlayerGroup.getSelection().getActionCommand());
		}

		return result;
	}
}
