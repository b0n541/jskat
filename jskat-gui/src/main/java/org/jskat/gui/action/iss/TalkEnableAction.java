package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssToggleTalkEnabledCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;

/**
 * Implements the action for sending an enable talking message to ISS
 */
public class TalkEnableAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public TalkEnableAction() {

        putValue(NAME, STRINGS.getString("iss_chat_enabled"));
        setIcon(JSkatGraphicRepository.Icon.CHAT);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {
        if (e.getSource() instanceof String) {
            EVENTBUS.post(new IssToggleTalkEnabledCommand((String) e.getSource()));
        }
    }
}
