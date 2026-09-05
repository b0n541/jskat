package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssDisconnectCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for leaving the ISS
 */
public class LogoutAction extends AbstractJSkatAction {
    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LogoutAction() {

        putValue(NAME, STRINGS.getString("disconnectFromIss"));
        setIcon(Icon.LOG_OUT);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {
        EVENTBUS.post(new IssDisconnectCommand());
    }
}
