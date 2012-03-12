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
public class MakeBidAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public MakeBidAction() {

		putValue(Action.NAME, strings.getString("make_bid")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION,
				strings.getString("make_bid_short_description")); //$NON-NLS-1$

		setActionCommand(JSkatAction.MAKE_BID);
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
