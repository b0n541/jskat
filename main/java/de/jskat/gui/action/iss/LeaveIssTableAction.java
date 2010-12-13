/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for leaving a skat table on ISS
 */
public class LeaveIssTableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public LeaveIssTableAction() {

		putValue(Action.NAME, "Leave table");

		setIcon(Icon.LOG_OUT);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.leaveTable();
	}
}
