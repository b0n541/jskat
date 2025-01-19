package org.jskat.gui.action.human;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.player.JSkatPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Abstract implementation of a human player action for JSkat<br>
 * When the action is performed the GUI player implementation of
 * {@link JSkatPlayer} is triggered
 */
public abstract class AbstractHumanJSkatAction extends AbstractJSkatAction {


    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        JSkatMaster.INSTANCE.triggerHuman(new JSkatActionEvent(e
                .getActionCommand(), e
                .getSource()));
    }
}
