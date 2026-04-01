package org.jskat.gui.swing;

import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;

import javax.swing.*;
import java.awt.*;

/**
 * Creates toolbar buttons with text under the icon to save space
 */
public class ToolbarButton extends JButton {

    private final AbstractJSkatAction jskatAction;

    /**
     * @param action Action
     */
    public ToolbarButton(final AbstractJSkatAction action) {
        this.jskatAction = action;

        if (action != null) {
            setText((String) action.getValue(AbstractJSkatAction.NAME));
            setToolTipText((String) action.getValue(AbstractJSkatAction.SHORT_DESCRIPTION));
            setEnabled(action.isEnabled());
            action.enabledProperty().addListener((observable, oldValue, newValue) ->
                    SwingUtilities.invokeLater(() -> setEnabled(newValue)));

            addActionListener(e -> action.actionPerformed(new JSkatActionEvent(
                    (String) action.getValue(AbstractJSkatAction.ACTION_COMMAND_KEY),
                    e.getSource()
            )));

            setIconSize(IconSize.SMALL);
        }
    }

    /**
     * Sets the icon size of a toolbar button
     *
     * @param iconSize IconSize to set
     */
    public void setIconSize(final IconSize iconSize) {

        if (jskatAction == null) {
            return;
        }

        Image iconImage = null;
        switch (iconSize) {
            case SMALL:
                iconImage = (Image) jskatAction.getValue(AbstractJSkatAction.SMALL_ICON);
                break;
            case BIG:
                iconImage = (Image) jskatAction.getValue(AbstractJSkatAction.LARGE_ICON_KEY);
                break;
        }

        if (iconImage != null) {
            setIcon(new ImageIcon(iconImage));
        }
    }
}
