package org.jskat.gui.swing;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.JSkatResourceBundle;

import javax.swing.*;

/**
 * Generic Tab Panel for JSkat
 */
public abstract class AbstractTabPanel extends JPanel {

    private static final long serialVersionUID = 1L;

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
    public AbstractTabPanel(final String tabName, final ActionMap actions) {

        super();
        setName(tabName);
        setActionMap(actions);
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
}
