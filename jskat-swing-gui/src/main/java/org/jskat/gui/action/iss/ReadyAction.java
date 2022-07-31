package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssReadyToPlayCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for leaving a skat table on ISS
 */
public class ReadyAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ReadyAction() {

        putValue(Action.NAME, STRINGS.getString("ready"));

        setIcon(Icon.OK);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new IssReadyToPlayCommand());
    }
}
