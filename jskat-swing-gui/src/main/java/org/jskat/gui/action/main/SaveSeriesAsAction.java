package org.jskat.gui.action.main;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for showing about dialog
 */
public class SaveSeriesAsAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public SaveSeriesAsAction() {

        putValue(Action.NAME, STRINGS.getString("save_series_as"));
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("save_series_as_tooltip"));

        setIcon(Icon.SAVE_AS);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: implement posting an event
    }
}
