/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.jskat.gui.action.JSkatAction;

import net.miginfocom.swing.MigLayout;

/**
 * Holds widgets for deciding of looking into skat or playing hand game
 */
class LookIntoSkatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Parent panel
	 */
	protected SkatTablePanel parent = null;

	/**
	 * Constructor
	 * 
	 * @param newParent Parent panel
	 */
	public LookIntoSkatPanel(SkatTablePanel newParent) {
		
		this.parent = newParent;
		initPanel();
	}
	
	private void initPanel() {
		
		this.setLayout(new MigLayout("fill"));
		
		ActionMap actions = this.parent.getActionMap();
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		panel.add(new JButton(actions.get(JSkatAction.LOOK_INTO_SKAT)));
		panel.add(new JButton(actions.get(JSkatAction.PLAY_HAND_GAME)));
		this.add(panel, "center");
	}
}
