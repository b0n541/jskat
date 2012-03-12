package org.jskat.gui.action.human;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.action.JSkatAction;

/**
 * Implements the action for handling card panel clicks during discarding
 */
public class PutCardIntoSkatAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public PutCardIntoSkatAction() {

		setActionCommand(JSkatAction.PUT_CARD_INTO_SKAT);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.putCardIntoSkat(e);
	}
}
