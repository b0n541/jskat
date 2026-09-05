package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssInvitePlayerCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;

/**
 * Implements the action for inviting a player to a skat table on ISS
 */
public class InvitePlayerAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public InvitePlayerAction() {
        putValue(NAME, STRINGS.getString("invitePlayer"));
        setIcon(JSkatGraphicRepository.Icon.INVITE);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {
        EVENTBUS.post(new IssInvitePlayerCommand());
    }
}
