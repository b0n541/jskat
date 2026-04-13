package org.jskat.gui.action.main;

import org.jskat.control.command.table.ContinueSkatSeriesCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for continuing a local skat series
 */
public class ContinueSkatSeriesAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ContinueSkatSeriesAction() {

        putValue(NAME, STRINGS.getString("continue_series"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("continue_series_tooltip"));

        setIcon(Icon.PLAY);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new ContinueSkatSeriesCommand(e.getActionCommand()));
    }
}
