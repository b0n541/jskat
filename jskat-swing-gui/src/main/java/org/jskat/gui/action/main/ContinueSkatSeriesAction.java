package org.jskat.gui.action.main;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.ReadyForNextGameCommand;
import org.jskat.data.JSkatApplicationData;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for continuing a local skat series
 */
public class ContinueSkatSeriesAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ContinueSkatSeriesAction() {

        putValue(NAME, STRINGS.getString("continue_series"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("continue_series_tooltip"));

        setIcon(Icon.PLAY);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        JSkatEventBus.TABLE_EVENT_BUSSES.get(
                JSkatApplicationData.INSTANCE.getActiveTable()).post(
                new ReadyForNextGameCommand());
    }
}
