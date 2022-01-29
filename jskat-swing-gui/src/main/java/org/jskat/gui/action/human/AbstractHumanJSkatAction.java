package org.jskat.gui.action.human;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.player.JSkatPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Abstract implementation of an human player action for JSkat<br>
 * When the action is performed the GUI player implementation of
 * {@link JSkatPlayer} is triggered
 */
public abstract class AbstractHumanJSkatAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JSkatMaster.INSTANCE.triggerHuman(new JSkatActionEvent(e
                .getActionCommand(), e
                .getSource()));
    }
}
