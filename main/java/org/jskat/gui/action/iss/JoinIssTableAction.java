package org.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for joining a skat table on ISS
 */
public class JoinIssTableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public JoinIssTableAction() {

		putValue(Action.NAME, "Join table");
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof String) {

			jskat.getIssController().joinTable((String) e.getSource());
		}
	}
}
