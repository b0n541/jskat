package org.jskat.gui.action.human;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.player.JSkatPlayer;

/**
 * Abstract implementation of a human player action for JSkat<br>
 * When the action is performed the GUI player implementation of
 * {@link JSkatPlayer} is triggered
 */
public abstract class AbstractHumanJSkatAction extends AbstractJSkatAction {

    /**
     * Creates an action routed to the human player.
     */
    protected AbstractHumanJSkatAction() {
    }

    @Override
    public void actionPerformed(final JSkatActionEvent e) {
        JSkatMaster.INSTANCE.triggerHuman(new JSkatActionEvent(e
                .getActionCommand(), e
                .getSource()));
    }
}
