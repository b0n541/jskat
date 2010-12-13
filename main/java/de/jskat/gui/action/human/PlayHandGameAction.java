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

import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.action.JSkatAction;

/**
 * Implements the action for handling card panel clicks
 */
public class PlayHandGameAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public PlayHandGameAction() {

		putValue(Action.NAME, "Play hand game");
		putValue(Action.SHORT_DESCRIPTION, "Play hand game");

		setActionCommand(JSkatAction.PLAY_HAND_GAME);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.triggerHuman(e);
	}
}
