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

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LicenseAction() {

        putValue(Action.NAME, STRINGS.getString("license")); //$NON-NLS-1$
        putValue(Action.SHORT_DESCRIPTION, STRINGS.getString("license_tooltip")); //$NON-NLS-1$

        setIcon(Icon.LICENSE);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        this.EVENTBUS.post(new ShowLicenseCommand());
    }
}
