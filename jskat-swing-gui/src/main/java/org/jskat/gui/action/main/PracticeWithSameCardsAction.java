package org.jskat.gui.action.main;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.PracticeWithSameCardsCommand;
import org.jskat.data.JSkatApplicationData;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for practicing a game with the same card deal.
 * This allows the player to practice with the same hand without affecting the score.
 */
public class PracticeWithSameCardsAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PracticeWithSameCardsAction() {

        putValue(NAME, STRINGS.getString("practice_same_cards"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("practice_same_cards_tooltip"));

        setIcon(Icon.REDO);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        JSkatEventBus.TABLE_EVENT_BUSSES.get(
                JSkatApplicationData.INSTANCE.getActiveTable()).post(
                new PracticeWithSameCardsCommand(JSkatApplicationData.INSTANCE.getActiveTable()));
    }
}
