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

import java.awt.Toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Determines the screen resolution
 */
public class ScreenResolution {

	private static final Logger LOG = LoggerFactory.getLogger(ScreenResolution.class);

	public static final boolean isBigScreen() {
		return getScreenHeight() > 800;
	}

	private static int getScreenHeight() {
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		LOG.debug("Screen height: " + screenHeight);
		return screenHeight;
	}

	public static final boolean isVeryBigScreen() {
		return getScreenHeight() > 1300;
	}
}
