package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssConnectCommand;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.iss.LoginCredentials;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for connecting to ISS
 */
public class ConnectAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ConnectAction() {

        putValue(Action.NAME, STRINGS.getString("connect_to_iss"));

        setActionCommand(JSkatAction.CONNECT_TO_ISS);
        setIcon(Icon.CONNECT_ISS);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof LoginCredentials) {
            EVENTBUS.post(new IssConnectCommand((LoginCredentials) e.getSource()));
        }
    }
}
