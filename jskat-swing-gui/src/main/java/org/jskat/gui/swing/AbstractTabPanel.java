/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.JSkatView;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.JSkatResourceBundle;

/**
 * Generic Tab Panel for JSkat
 * 
 */
public abstract class AbstractTabPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * JSkat view
	 */
	protected JSkatView view;
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
	 * @param actions
	 *            JSkat actions
	 */
	public AbstractTabPanel(final JSkatView view, final String tabName,
			final ActionMap actions) {

		super();
		setName(tabName);
		setActionMap(actions);
		bitmaps = JSkatGraphicRepository.instance();
		strings = JSkatResourceBundle.instance();
		options = JSkatOptions.instance();
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
