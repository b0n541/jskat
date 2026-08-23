package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssTableSeatChangeCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for leaving a skat table on ISS
 */
public class ChangeTableSeatsAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ChangeTableSeatsAction() {

        putValue(NAME, "Change table seats (3 <-> 4)");
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new IssTableSeatChangeCommand());
    }
}
