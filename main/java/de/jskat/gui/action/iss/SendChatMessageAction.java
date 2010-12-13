/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.data.iss.ISSChatMessage;
import de.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for sending a chat message
 */
public class SendChatMessageAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public SendChatMessageAction() {

		putValue(Action.NAME, "Send chat message");
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof ISSChatMessage) {

			ISSChatMessage message = (ISSChatMessage) e.getSource();
			jskat.getIssController().sendChatMessage(message);
		}
	}
}
