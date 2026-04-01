package org.jskat.gui.action;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.MenuItem;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.util.JSkatResourceBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines an abstract action for JSkat
 */
public abstract class AbstractJSkatAction {
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

    public final static String NAME = "Name";
    public final static String SHORT_DESCRIPTION = "ShortDescription";
    public final static String SMALL_ICON = "SmallIcon";
    public final static String LARGE_ICON_KEY = "LargeIconKey";
    public final static String ACTION_COMMAND_KEY = "ActionCommandKey";

    private final Map<String, Object> values = new HashMap<>();
    private final BooleanProperty enabled = new SimpleBooleanProperty(true);

    protected MenuItem menuItem;

    /**
     * Constructor
     */
    public AbstractJSkatAction() {
        setIcon(Icon.BLANK);
    }

    /**
     * Performs the action
     *
     * @param e Action event
     */
    public abstract void actionPerformed(JSkatActionEvent e);

    public void putValue(String key, Object value) {
        values.put(key, value);
    }

    public Object getValue(String key) {
        return values.get(key);
    }

    protected void setIcon(JSkatGraphicRepository.Icon icon) {
        putValue(SMALL_ICON, BITMAPS.getIconImage(icon, JSkatGraphicRepository.IconSize.SMALL));
        putValue(LARGE_ICON_KEY, BITMAPS.getIconImage(icon, JSkatGraphicRepository.IconSize.BIG));
    }

    protected void setActionCommand(JSkatAction action) {
        putValue(ACTION_COMMAND_KEY, action.toString());
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        if (menuItem != null) {
            menuItem.disableProperty().bind(enabled.not());
        }
    }

    public void setEnabled(boolean isEnabled) {
        enabled.set(isEnabled);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }
}
