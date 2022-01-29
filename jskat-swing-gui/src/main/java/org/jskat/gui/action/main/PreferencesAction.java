package org.jskat.gui.action.main;

import org.jskat.control.command.general.ShowPreferencesCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for showing the preferences dialog
 */
public class PreferencesAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PreferencesAction() {

        putValue(Action.NAME, STRINGS.getString("preferences")); //$NON-NLS-1$
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("preferences_tooltip")); //$NON-NLS-1$

        setIcon(Icon.PREFERENCES);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new ShowPreferencesCommand());
    }
}
