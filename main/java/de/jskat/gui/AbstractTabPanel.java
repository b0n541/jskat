/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import de.jskat.data.JSkatOptions;
import de.jskat.gui.img.JSkatGraphicRepository;

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
	protected ResourceBundle strings;
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
	public AbstractTabPanel(String tabName,
			JSkatGraphicRepository jskatBitmaps, ActionMap actions,
			ResourceBundle jskatStrings, JSkatOptions jskatOptions) {

		super();
		setName(tabName);
		setActionMap(actions);
		bitmaps = jskatBitmaps;
		strings = jskatStrings;
		options = jskatOptions;
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
