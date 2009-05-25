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
	 * Panel name
	 */
	protected String panelName;
	/**
	 * JSkat bitmaps
	 */
	protected JSkatGraphicRepository bitmaps;

	/**
	 * Constructor
	 * 
	 * @param newTableName Table name
	 * @param jskatBitmaps JSkat bitmaps
	 * @param actions JSkat actions
	 */
	protected JSkatTabPanel(String newTableName, JSkatGraphicRepository jskatBitmaps, ActionMap actions) {

		this.setActionMap(actions);
		this.panelName = newTableName;
		this.bitmaps = jskatBitmaps;
		initPanel();
	}
	
	/**
	 * Initializes the tab panel
	 */
	protected abstract void initPanel();
}
