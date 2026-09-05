package org.jskat.gui.action.main;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.SaveSeriesAsCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for saving a skat series
 */
public class SaveSeriesAsAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public SaveSeriesAsAction() {

        putValue(NAME, STRINGS.getString("saveSeriesAs"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("saveSeriesAsTooltip"));

        setIcon(Icon.SAVE_AS);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new SaveSeriesAsCommand(e.getActionCommand()));
    }
}
