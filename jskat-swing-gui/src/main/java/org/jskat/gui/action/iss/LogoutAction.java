package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssDisconnectCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for leaving the ISS
 */
public class LogoutAction extends AbstractJSkatAction {
    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LogoutAction() {

        putValue(Action.NAME, STRINGS.getString("disconnect_from_iss"));
        setIcon(Icon.LOG_OUT);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        EVENTBUS.post(new IssDisconnectCommand());
    }
}
