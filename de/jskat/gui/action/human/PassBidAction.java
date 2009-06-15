/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.human;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.action.JSkatAction;

/**
 * Implements the action for handling card panel clicks
 */
public class PassBidAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public PassBidAction(JSkatMaster controller) {
		
		super(controller);
		
		putValue(Action.NAME, "Pass");
		putValue(Action.SHORT_DESCRIPTION, "Pass this bid");
		putValue(Action.ACTION_COMMAND_KEY, JSkatAction.PASS_BID.toString());
	}
	
	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		System.out.println(e.getActionCommand());
		
		this.jskat.triggerHuman(e);
	}
}
