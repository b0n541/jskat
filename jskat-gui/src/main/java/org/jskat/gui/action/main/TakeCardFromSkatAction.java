package org.jskat.gui.action.main;

import org.jskat.control.command.table.TakeCardFromSkatCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.util.Card;

/**
 * Implements the action for taking a card from skat
 */
public class TakeCardFromSkatAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public TakeCardFromSkatAction() {

        putValue(NAME, "Take card from skat");
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        if (e.getSource() instanceof Card) {

            EVENTBUS.post(new TakeCardFromSkatCommand(e.getActionCommand(), (Card) e.getSource()));
        }
    }
}
