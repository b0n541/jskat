package org.jskat.gui.action.main;

import org.jskat.control.command.general.ShowPreferencesCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for showing the preferences dialog
 */
public class PreferencesAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PreferencesAction() {

        putValue(NAME, STRINGS.getString("preferences"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("preferences_tooltip"));

        setIcon(Icon.PREFERENCES);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new ShowPreferencesCommand());
    }
}
