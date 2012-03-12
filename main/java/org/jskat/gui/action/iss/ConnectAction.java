package org.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for connecting to ISS
 */
public class ConnectAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public ConnectAction() {

		putValue(Action.NAME, strings.getString("connect_to_iss")); //$NON-NLS-1$

		setActionCommand(JSkatAction.CONNECT_TO_ISS);
		setIcon(Icon.CONNECT_ISS);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.getIssController().connectToISS(e);
	}
}
