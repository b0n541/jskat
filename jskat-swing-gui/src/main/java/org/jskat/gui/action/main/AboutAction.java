package org.jskat.gui.action.main;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.general.ShowAboutInformationCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for showing about dialog.
 */
public class AboutAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public AboutAction() {
        putValue(Action.NAME, STRINGS.getString("about"));
        putValue(Action.SHORT_DESCRIPTION, STRINGS.getString("about_tooltip"));
        setIcon(Icon.ABOUT);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JSkatEventBus.INSTANCE.post(new ShowAboutInformationCommand());
    }
}
