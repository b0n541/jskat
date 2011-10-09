/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0
 * Build date: 2011-10-09
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
package org.jskat.gui;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;

/**
 * Creates toolbar buttons with text under the icon to save space
 */
public class ToolbarButton extends JButton {

	private static final long serialVersionUID = 1L;
	static Log log = LogFactory.getLog(ToolbarButton.class);

	/**
	 * @see JButton#JButton(Action)
	 */
	public ToolbarButton(Action a) {

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
	public void setIconSize(IconSize iconSize) {

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
