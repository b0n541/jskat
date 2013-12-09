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

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.jskat.gui.img.JSkatGraphicRepository.IconSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates toolbar buttons with text under the icon to save space
 */
public class ToolbarButton extends JButton {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ToolbarButton.class);

	/**
	 * @see JButton#JButton(Action)
	 */
	public ToolbarButton(final Action a) {

		super(a);
		// setTextPosition();
		// setPreferredSize(new Dimension(120, 100));
		setIconSize(IconSize.SMALL);
	}

	private void setTextPosition() {

		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
	}

	/**
	 * Sets the icon size of a toolbar button
	 * 
	 * @param iconSize
	 *            IconSize to set
	 */
	public void setIconSize(final IconSize iconSize) {

		ImageIcon icon = null;
		switch (iconSize) {
		case SMALL:
			icon = (ImageIcon) getAction().getValue(Action.SMALL_ICON);
			break;
		case BIG:
			icon = (ImageIcon) getAction().getValue(Action.LARGE_ICON_KEY);
			break;
		}

		if (icon != null) {
			setIcon(icon);
		}
	}
}
