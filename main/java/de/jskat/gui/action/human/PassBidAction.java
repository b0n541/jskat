/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.human;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for handling click on pass bid button
 */
public class PassBidAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster,
	 *      JSkatGraphicRepository)
	 */
	public PassBidAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller, bitmaps);

		putValue(Action.NAME, strings.getString("pass")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, "Pass this bid");

		setActionCommand(JSkatAction.PASS_BID);
		setIcon(Icon.STOP);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.triggerHuman(e);
	}
}
