package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssTableSeatChangeCommand;
import org.jskat.gui.action.AbstractJSkatAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for leaving a skat table on ISS
 */
public class ChangeTableSeatsAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ChangeTableSeatsAction() {

        putValue(Action.NAME, "Change table seats (3 <-> 4)");
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new IssTableSeatChangeCommand());
    }
}
