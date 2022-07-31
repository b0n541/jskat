package org.jskat.gui.action.main;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for showing about dialog
 */
public class LoadSeriesAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LoadSeriesAction() {

        putValue(Action.NAME, STRINGS.getString("load_series"));
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("load_series_tooltip"));

        setIcon(Icon.LOAD);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: implement posting an event
    }
}
