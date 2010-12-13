/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.human;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.action.JSkatAction;

/**
 * Implements the action for handling card panel clicks during discarding
 */
public class TakeCardFromSkatAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public TakeCardFromSkatAction() {

		setActionCommand(JSkatAction.TAKE_CARD_FROM_SKAT);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.takeCardFromSkat(e);
	}
}
