/**
 * This file is part of JSkat.
 * <p>
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.JSkatResourceBundle;

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
	 */
	public AbstractTabPanel(final String tabName) {
		this(tabName, null);
	}

	/**
	 * Constructor
	 * 
	 * @param tabName
	 *            Table name
	 * @param actions
	 *            JSkat actions
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
