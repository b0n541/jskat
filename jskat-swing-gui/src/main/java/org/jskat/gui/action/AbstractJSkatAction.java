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
package org.jskat.gui.action;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.util.JSkatResourceBundle;

import javafx.scene.control.MenuItem;

/**
 * Defines an abstract action for JSkat
 */
public abstract class AbstractJSkatAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	/**
	 * JSkat graphics repository
	 */
	protected final static JSkatGraphicRepository BITMAPS = JSkatGraphicRepository.INSTANCE;
	/**
	 * JSkat i18n strings
	 */
	protected final static JSkatResourceBundle STRINGS = JSkatResourceBundle.INSTANCE;
	/**
	 * JSkat event bus
	 */
	protected final static JSkatEventBus EVENTBUS = JSkatEventBus.INSTANCE;
	
	protected MenuItem menuItem;

	/**
	 * Constructor
	 */
	public AbstractJSkatAction() {

		setIcon(Icon.BLANK);
	}

	protected void setIcon(JSkatGraphicRepository.Icon icon) {
		putValue(
				SMALL_ICON,
				new ImageIcon(BITMAPS.getIconImage(icon,
						JSkatGraphicRepository.IconSize.SMALL)));
		putValue(
				LARGE_ICON_KEY,
				new ImageIcon(BITMAPS.getIconImage(icon,
						JSkatGraphicRepository.IconSize.BIG)));
	}

	protected void setActionCommand(JSkatAction action) {
		putValue(ACTION_COMMAND_KEY, action.toString());
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		super.setEnabled(isEnabled);

		if (menuItem != null) {
			menuItem.setDisable(!isEnabled);
		}
	}
}
