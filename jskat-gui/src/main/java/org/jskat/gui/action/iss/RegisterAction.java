package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for opening the ISS registration page in the default
 * browser
 */
public class RegisterAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public RegisterAction() {

        putValue(NAME, STRINGS.getString("registerOnIss"));
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        JSkatMaster.INSTANCE.getView().openWebPage("https://skatgame.net/iss/registration.html");
    }
}
