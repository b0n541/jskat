/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.JSkatAction;

class GameStartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GameStartPanel(ActionMap actions) {

		initPanel(actions);
	}

	public void initPanel(ActionMap actions) {

		this.setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		panel
				.add(new JButton(actions.get(JSkatAction.READY_TO_PLAY)),
						"center");
		panel.add(new JButton(actions.get(JSkatAction.TALK_ENABLED)), "center");
		panel.add(new JButton(actions.get(JSkatAction.CHANGE_TABLE_SEATS)),
				"center");
		panel.add(new JButton(actions.get(JSkatAction.LEAVE_ISS_TABLE)),
				"center");
		this.add(panel, "center, grow"); //$NON-NLS-1$
	}
}
