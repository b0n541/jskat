package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.data.iss.ChatMessage;
import org.jskat.gui.action.AbstractJSkatAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for sending a chat message
 */
public class SendChatMessageAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public SendChatMessageAction() {

        putValue(Action.NAME, STRINGS.getString("send_chat_message"));
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof ChatMessage) {

            ChatMessage message = (ChatMessage) e.getSource();
            JSkatMaster.INSTANCE.getIssController().sendChatMessage(message);
        }
    }
}
