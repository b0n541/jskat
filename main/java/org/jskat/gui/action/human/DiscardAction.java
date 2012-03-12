package org.jskat.gui.action.human;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.action.JSkatAction;

/**
 * Implements the action for handling card panel clicks
 */
public class DiscardAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public DiscardAction() {

		putValue(Action.NAME, "Discard");
		putValue(Action.SHORT_DESCRIPTION, "Discard cards");

		setActionCommand(JSkatAction.DISCARD_CARDS);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.triggerHuman(e);
	}
}
