package org.jskat.gui.action.main;

import org.jskat.control.command.table.LoadSeriesCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for loading a skat series
 */
public class LoadSeriesAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LoadSeriesAction() {

        putValue(NAME, STRINGS.getString("loadSeries"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("loadSeriesTooltip"));

        setIcon(Icon.LOAD);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new LoadSeriesCommand());
    }
}
