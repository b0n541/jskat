package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for leaving a skat table on ISS
 */
public class InvitePlayerAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public InvitePlayerAction() {

        putValue(NAME, STRINGS.getString("invite"));

        setIcon(Icon.INVITE);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JSkatMaster.INSTANCE.invitePlayer();
    }
}
