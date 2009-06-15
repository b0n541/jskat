package de.jskat.gui;

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
	 * Constructor
	 * 
	 * @param tabName Table name
	 * @param jskatBitmaps JSkat bitmaps
	 * @param actions JSkat actions
	 */
	protected JSkatTabPanel(String tabName, JSkatGraphicRepository jskatBitmaps, ActionMap actions) {

		super();
		this.setName(tabName);
		this.setActionMap(actions);
		this.bitmaps = jskatBitmaps;
		initPanel();
	}
	
	/**
	 * Initializes the tab panel
	 */
	protected abstract void initPanel();
}
