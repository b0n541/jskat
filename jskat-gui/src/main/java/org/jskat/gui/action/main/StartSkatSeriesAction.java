package org.jskat.gui.action.main;

import org.jskat.control.command.table.StartSkatSeriesCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for starting a local skat series
 */
public class StartSkatSeriesAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public StartSkatSeriesAction() {

        putValue(NAME, STRINGS.getString("start_series"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("start_series_tooltip"));

        setIcon(Icon.PLAY);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new StartSkatSeriesCommand(e.getActionCommand()));
    }
}
