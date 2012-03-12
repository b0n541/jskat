package org.jskat.gui.action.human;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for handling click on bid button
 */
public class HoldBidAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public HoldBidAction() {

		putValue(Action.NAME, "hold bid"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, "Hold this bid");

		setActionCommand(JSkatAction.HOLD_BID);
		setIcon(Icon.OK);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.triggerHuman(e);
	}
}
