package org.jskat.gui.action.main;

import org.jskat.control.command.general.ShowLicenseCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for showing about dialog
 */
public class LicenseAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LicenseAction() {

        putValue(Action.NAME, STRINGS.getString("license"));
        putValue(Action.SHORT_DESCRIPTION, STRINGS.getString("license_tooltip"));

        setIcon(Icon.LICENSE);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new ShowLicenseCommand());
    }
}
