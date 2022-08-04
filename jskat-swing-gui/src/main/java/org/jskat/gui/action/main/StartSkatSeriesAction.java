package org.jskat.gui.action.main;

import org.jskat.control.command.skatseries.CreateSkatSeriesCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for starting a new skat series
 */
public class StartSkatSeriesAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public StartSkatSeriesAction() {

        putValue(Action.NAME, STRINGS.getString("start_series"));
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("start_series_tooltip"));

        setIcon(Icon.PLAY);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new CreateSkatSeriesCommand());
    }
}
