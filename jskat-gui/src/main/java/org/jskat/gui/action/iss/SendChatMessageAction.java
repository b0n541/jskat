package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.data.iss.ChatMessage;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for sending a chat message to ISS
 */
public class SendChatMessageAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public SendChatMessageAction() {

        putValue(NAME, STRINGS.getString("send"));
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        if (e.getSource() instanceof ChatMessage) {

            JSkatMaster.INSTANCE.getIssController().sendChatMessage((ChatMessage) e.getSource());
        }
    }
}
