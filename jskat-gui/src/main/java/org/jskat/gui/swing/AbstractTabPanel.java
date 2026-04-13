package org.jskat.gui.swing;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.JSkatResourceBundle;

import javax.swing.*;
import java.util.Map;

/**
 * Generic Tab Panel for JSkat
 */
public abstract class AbstractTabPanel extends JPanel {


    /**
     * JSkat bitmaps
     */
    protected JSkatGraphicRepository bitmaps;
    /**
     * JSkat strings
     */
    protected JSkatResourceBundle strings;
    /**
     * JSkat options
     */
    protected JSkatOptions options;

    protected Map<JSkatAction, AbstractJSkatAction> actions;

    /**
     * Constructor
     *
     * @param tabName Table name
     */
    public AbstractTabPanel(final String tabName) {
        this(tabName, null);
    }

    /**
     * Constructor
     *
     * @param tabName Table name
     * @param actions JSkat actions
     */
    public AbstractTabPanel(final String tabName, final Map<JSkatAction, AbstractJSkatAction> actions) {

        super();
        setName(tabName);
        this.actions = actions;
        this.bitmaps = JSkatGraphicRepository.INSTANCE;
        this.strings = JSkatResourceBundle.INSTANCE;
        this.options = JSkatOptions.instance();
        initPanel();
    }

    /**
     * Initializes the tab panel.
     */
    protected abstract void initPanel();

    /**
     * Sets the focus.
     */
    protected abstract void setFocus();

    /**
     * Gets the action map.
     *
     * @return Action map
     */
    public Map<JSkatAction, AbstractJSkatAction> getActions() {
        return actions;
    }
}
