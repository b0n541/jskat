package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for inviting a player to a skat table on ISS
 */
public class InvitePlayerAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public InvitePlayerAction() {

        putValue(NAME, STRINGS.getString("invite_player"));
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        JSkatMaster.INSTANCE.getIssController().invitePlayer();
    }
}
