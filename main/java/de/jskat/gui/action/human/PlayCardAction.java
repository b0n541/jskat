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
 * Implements the action for handling card panel clicks during trick play
 */
public class PlayCardAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public PlayCardAction() {

		setActionCommand(JSkatAction.PLAY_CARD);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.triggerHuman(e);
	}
}
