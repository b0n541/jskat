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
import de.jskat.gui.action.JSkatActions;

/**
 * Implements the action for handling card panel clicks during discarding
 */
public class PutCardIntoSkatAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public PutCardIntoSkatAction(JSkatMaster controller) {
		
		super(controller);
		putValue(Action.ACTION_COMMAND_KEY, JSkatActions.PUT_CARD_INTO_SKAT.toString());
	}
	
	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		System.out.println(e.getActionCommand());
		
		this.jskat.putCardIntoSkat(e);
	}
}
