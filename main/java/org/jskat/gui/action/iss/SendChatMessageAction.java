package org.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.data.iss.ChatMessage;
import org.jskat.gui.action.AbstractJSkatAction;

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

		if (e.getSource() instanceof ChatMessage) {

			ChatMessage message = (ChatMessage) e.getSource();
			jskat.getIssController().sendChatMessage(message);
		}
	}
}
