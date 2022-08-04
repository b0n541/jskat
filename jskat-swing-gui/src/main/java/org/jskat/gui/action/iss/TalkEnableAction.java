package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssToggleTalkEnabledCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for leaving a skat table on ISS
 */
public class TalkEnableAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public TalkEnableAction() {

        putValue(Action.NAME, STRINGS.getString("talk"));

        setIcon(Icon.CHAT);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new IssToggleTalkEnabledCommand());
    }
}
