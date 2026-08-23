package org.jskat.gui.action.main;

import org.jskat.control.command.table.SetActiveTableCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for changing the active table
 */
public class ChangeActiveTableAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ChangeActiveTableAction() {

        putValue(NAME, "Change active table");
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new SetActiveTableCommand(e.getActionCommand()));
    }
}
