package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for registering on ISS
 */
public class RegisterAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public RegisterAction() {

        putValue(Action.NAME, STRINGS.getString("register_on_iss")); //$NON-NLS-1$

        setIcon(Icon.REGISTER);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JSkatMaster.INSTANCE.openIssRegisterPage();
    }
}
