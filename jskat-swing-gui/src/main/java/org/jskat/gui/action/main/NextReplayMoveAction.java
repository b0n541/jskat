package org.jskat.gui.action.main;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.NextReplayMoveCommand;
import org.jskat.data.JSkatApplicationData;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for replaying a game
 */
public class NextReplayMoveAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public NextReplayMoveAction() {

        putValue(NAME, STRINGS.getString("next_replay_move"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("next_replay_move_tooltip"));

        setIcon(Icon.NEXT);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        JSkatEventBus.TABLE_EVENT_BUSSES.get(
                JSkatApplicationData.INSTANCE.getActiveTable()).post(
                new NextReplayMoveCommand());
    }
}
