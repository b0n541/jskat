package org.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for creating a skat table on ISS
 */
public class CreateIssTableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public CreateIssTableAction() {

		// FIXME (jan 23.11.2010) use CreateTableAction

		putValue(Action.NAME, strings.getString("new_table")); //$NON-NLS-1$
		setIcon(Icon.TABLE);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.getIssController().requestTableCreation();
	}
}
