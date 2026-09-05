package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssConnectCommand;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.data.iss.LoginCredentials;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for connecting to ISS
 */
public class ConnectAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ConnectAction() {

        putValue(NAME, STRINGS.getString("connectToIss"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("connectToIss"));

        setActionCommand(JSkatAction.CONNECT_TO_ISS);
        setIcon(Icon.CONNECT_ISS);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {
        if (e.getSource() instanceof LoginCredentials) {
            EVENTBUS.post(new IssConnectCommand((LoginCredentials) e.getSource()));
        }
    }
}
