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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Screen;

/**
 * Determines the screen resolution
 */
public class ScreenResolution {

	private static final Logger LOG = LoggerFactory.getLogger(ScreenResolution.class);

	public static final boolean isBigScreen(Screen targetScreen) {
		return getScreenHeight(targetScreen) > 800;
	}

	private static int getScreenHeight(Screen targetScreen) {
		double height = targetScreen.getBounds().getHeight();
		LOG.debug("Screen height JavaFX: " + height);
		return (int) height;
	}

	public static final boolean isVeryBigScreen(Screen targetScreen) {
		return getScreenHeight(targetScreen) > 1300;
	}
}
