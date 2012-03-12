package org.jskat.gui.action.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for creating a new table
 */
public class CreateTableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public CreateTableAction() {

		putValue(Action.NAME, strings.getString("new_table")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION,
				strings.getString("new_table_tooltip")); //$NON-NLS-1$

		setIcon(Icon.TABLE);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.createTable();
	}

}
