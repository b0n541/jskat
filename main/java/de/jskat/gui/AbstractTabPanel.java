/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import de.jskat.data.JSkatOptions;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.JSkatResourceBundle;

/**
 * Generic Tab Panel for JSkat
 * 
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
	 * @param tabName
	 *            Table name
	 * @param jskatBitmaps
	 *            JSkat bitmaps
	 * @param actions
	 *            JSkat actions
	 */
	public AbstractTabPanel(String tabName, ActionMap actions) {

		super();
		setName(tabName);
		setActionMap(actions);
		bitmaps = JSkatGraphicRepository.instance();
		strings = JSkatResourceBundle.instance();
		options = JSkatOptions.instance();
		initPanel();
	}

	/**
	 * Initializes the tab panel
	 */
	protected abstract void initPanel();

	/**
	 * Sets the focus
	 */
	protected abstract void setFocus();
}
