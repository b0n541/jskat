package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.util.Card;

/**
 * Implements the action for putting a card into skat
 */
public class PutCardIntoSkatAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PutCardIntoSkatAction() {

        putValue(NAME, "Put card into skat");
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        if (e.getSource() instanceof Card) {

            JSkatMaster.INSTANCE.putCardIntoSkat(e.getActionCommand(), (Card) e.getSource());
        }
    }
}
