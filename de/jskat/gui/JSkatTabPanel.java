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

import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Generic Tab Panel for JSkat
 * 
 */
public abstract class JSkatTabPanel extends JPanel {

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
	 * Constructor
	 * 
	 * @param tabName
	 *            Table name
	 * @param jskatBitmaps
	 *            JSkat bitmaps
	 * @param actions
	 *            JSkat actions
	 */
	public JSkatTabPanel(String tabName, JSkatGraphicRepository jskatBitmaps,
			ActionMap actions, ResourceBundle jskatStrings) {

		super();
		this.setName(tabName);
		this.setActionMap(actions);
		this.bitmaps = jskatBitmaps;
		this.strings = jskatStrings;
		initPanel();
	}

	/**
	 * Initializes the tab panel
	 */
	protected abstract void initPanel();
}
