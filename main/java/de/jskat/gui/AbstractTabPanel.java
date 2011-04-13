/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
