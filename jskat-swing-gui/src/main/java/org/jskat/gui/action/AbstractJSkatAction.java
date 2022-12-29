package org.jskat.gui.action;

import javafx.scene.control.MenuItem;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.util.JSkatResourceBundle;

import javax.swing.*;

/**
 * Defines an abstract action for JSkat
 */
public abstract class AbstractJSkatAction extends AbstractAction {
    /**
     * JSkat graphics repository
     */
    protected final static JSkatGraphicRepository BITMAPS = JSkatGraphicRepository.INSTANCE;
    /**
     * JSkat i18n strings
     */
    protected final static JSkatResourceBundle STRINGS = JSkatResourceBundle.INSTANCE;
    /**
     * JSkat event bus
     */
    protected final static JSkatEventBus EVENTBUS = JSkatEventBus.INSTANCE;

    protected MenuItem menuItem;

    /**
     * Constructor
     */
    public AbstractJSkatAction() {

        setIcon(Icon.BLANK);
    }

    protected void setIcon(JSkatGraphicRepository.Icon icon) {
        putValue(
                SMALL_ICON,
                new ImageIcon(BITMAPS.getIconImage(icon,
                        JSkatGraphicRepository.IconSize.SMALL)));
        putValue(
                LARGE_ICON_KEY,
                new ImageIcon(BITMAPS.getIconImage(icon,
                        JSkatGraphicRepository.IconSize.BIG)));
    }

    protected void setActionCommand(JSkatAction action) {
        putValue(ACTION_COMMAND_KEY, action.toString());
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);

        if (menuItem != null) {
            menuItem.setDisable(!isEnabled);
        }
    }
}
