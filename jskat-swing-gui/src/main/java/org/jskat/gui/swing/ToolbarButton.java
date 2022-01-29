package org.jskat.gui.swing;

import org.jskat.gui.img.JSkatGraphicRepository.IconSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Creates toolbar buttons with text under the icon to save space
 */
public class ToolbarButton extends JButton {

    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(ToolbarButton.class);

    /**
     * @param action Action
     * @see JButton#JButton(Action)
     */
    public ToolbarButton(final Action action) {

        super(action);
        // setTextPosition();
        // setPreferredSize(new Dimension(120, 100));
        setIconSize(IconSize.SMALL);
    }

    private void setTextPosition() {

        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
    }

    /**
     * Sets the icon size of a toolbar button
     *
     * @param iconSize IconSize to set
     */
    public void setIconSize(final IconSize iconSize) {

        ImageIcon icon = null;
        switch (iconSize) {
            case SMALL:
                icon = (ImageIcon) getAction().getValue(Action.SMALL_ICON);
                break;
            case BIG:
                icon = (ImageIcon) getAction().getValue(Action.LARGE_ICON_KEY);
                break;
        }

        if (icon != null) {
            setIcon(icon);
        }
    }
}