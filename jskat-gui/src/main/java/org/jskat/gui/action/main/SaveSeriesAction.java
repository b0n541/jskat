package org.jskat.gui.action.main;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.SaveSeriesCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for saving a skat series
 */
public class SaveSeriesAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public SaveSeriesAction() {

        putValue(NAME, STRINGS.getString("save_series"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("save_series_tooltip"));

        setIcon(Icon.SAVE);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new SaveSeriesCommand(e.getActionCommand()));
    }
}
