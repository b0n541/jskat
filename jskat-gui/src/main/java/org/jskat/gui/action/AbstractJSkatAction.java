package org.jskat.gui.action;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.MenuItem;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.util.JSkatResourceBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines an abstract action for JSkat
 */
public abstract class AbstractJSkatAction {
    /**
     * JSkat i18n strings
     */
    protected final static JSkatResourceBundle STRINGS = JSkatResourceBundle.INSTANCE;
    /**
     * JSkat event bus
     */
    protected final static JSkatEventBus EVENTBUS = JSkatEventBus.INSTANCE;

    /**
     * Key for the localized action name.
     */
    public final static String NAME = "Name";
    /**
     * Key for the localized short description.
     */
    public final static String SHORT_DESCRIPTION = "ShortDescription";
    /**
     * Key for the action command identifier.
     */
    public final static String ACTION_COMMAND_KEY = "ActionCommandKey";

    // deprecated: replace with dedicated fields for text and icons
    private final Map<String, Object> values = new HashMap<>();

    private Icon icon;
    private final BooleanProperty enabled = new SimpleBooleanProperty(true);

    /**
     * Menu item currently controlled by this action, when attached.
     */
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

    /**
     * Stores a named value associated with this action.
     * @param key value key
     * @param value value to store
     */
    public void putValue(String key, Object value) {
        values.put(key, value);
    }

    /**
     * Returns a named value associated with this action.
     * @param key value key
     * @return stored value, or {@code null} when absent
     */
    public Object getValue(String key) {
        return values.get(key);
    }

    /**
     * Sets the icon displayed for this action.
     * @param icon action icon
     */
    protected void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon displayed for this action.
     * @return action icon
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Returns the localized tooltip text for this action.
     * @return tooltip text
     */
    public String getTooltip() {
        return values.get(SHORT_DESCRIPTION).toString();
    }

    /**
     * Sets the command key from the corresponding action enum.
     * @param action action identifier
     */
    protected void setActionCommand(JSkatAction action) {
        putValue(ACTION_COMMAND_KEY, action.toString());
    }

    /**
     * Associates a menu item with this action.
     * @param menuItem menu item to control
     */
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        if (menuItem != null) {
            menuItem.disableProperty().bind(enabled.not());
        }
    }

    /**
     * Sets whether this action is enabled.
     * @param isEnabled {@code true} to enable the action
     */
    public void setEnabled(boolean isEnabled) {
        enabled.set(isEnabled);
    }

    /**
     * Returns whether this action is enabled.
     * @return enabled state
     */
    public boolean isEnabled() {
        return enabled.get();
    }

    /**
     * Returns the observable enabled state.
     * @return enabled property
     */
    public BooleanProperty enabledProperty() {
        return enabled;
    }
}
